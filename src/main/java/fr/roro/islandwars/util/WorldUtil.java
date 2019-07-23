package fr.roro.islandwars.util;

import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class WorldUtil {

    public static Location getLocation(World world, BlockPosition blockPosition) {
        return new Location(world, blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    }

    public static Block getBukkitBlock(net.minecraft.server.v1_8_R3.World world, BlockPosition blockPosition) {
        return world.getWorld().getBlockAt(getLocation(world.getWorld(), blockPosition));
    }

}
