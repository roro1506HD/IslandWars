package fr.roro.islandwars.util.block;

import fr.roro.islandwars.util.GameUtil;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class GeometryUtil {

    public static int makeCylinder(Location pos, BlockPattern block, double radius, int height, boolean filled, Consumer<Block> consumer) {
        return makeCylinder(pos, block, radius, radius, height, filled, consumer);
    }

    public static int makeCylinder(Location pos, BlockPattern block, double radiusX, double radiusZ, int height, boolean filled, Consumer<Block> consumer) {
        int affected = 0;

        radiusX += 0.5;
        radiusZ += 0.5;

        if (height == 0)
            return 0;
        else if (height < 0)
            pos = pos.subtract(0, height = -height, 0);

        if (pos.getBlockY() < 0)
            pos.setY(0);
        else if (pos.getBlockY() + height - 1 > 256)
            height = 256 - pos.getBlockY() + 1;

        final double invRadiusX = 1 / radiusX;
        final double invRadiusZ = 1 / radiusZ;

        final int ceilRadiusX = (int) Math.ceil(radiusX);
        final int ceilRadiusZ = (int) Math.ceil(radiusZ);

        double nextXn = 0;
        forX:
        for (int x = 0; x <= ceilRadiusX; ++x) {
            final double xn = nextXn;
            nextXn = (x + 1) * invRadiusX;
            double nextZn = 0;
            for (int z = 0; z <= ceilRadiusZ; ++z) {
                final double zn = nextZn;
                nextZn = (z + 1) * invRadiusZ;

                double distanceSq = lengthSq(xn, zn);
                if (distanceSq > 1) {
                    if (z == 0)
                        break forX;
                    break;
                }

                if (!filled)
                    if (lengthSq(nextXn, zn) <= 1 && lengthSq(xn, nextZn) <= 1)
                        continue;

                for (int y = 0; y < height; ++y) {
                    if (setBlock(pos.clone().add(x, y, z), block, consumer))
                        ++affected;
                    if (setBlock(pos.clone().add(-x, y, z), block, consumer))
                        ++affected;
                    if (setBlock(pos.clone().add(x, y, -z), block, consumer))
                        ++affected;
                    if (setBlock(pos.clone().add(-x, y, -z), block, consumer))
                        ++affected;
                }
            }
        }

        return affected;
    }

    private static double lengthSq(double x, double z) {
        return (x * x) + (z * z);
    }

    private static boolean setBlock(Location location, BlockPattern pattern, Consumer<Block> consumer) {
        if (!GameUtil.isBlockBreakable(location.getBlock()))
            return false;

        location.getBlock().setType(pattern.getMaterial(), false);
        location.getBlock().setData(pattern.getData(), false);

        consumer.accept(location.getBlock());

        return (location.getBlock().getType() == pattern.getMaterial()) && (location.getBlock().getData() == pattern.getData());
    }

}
