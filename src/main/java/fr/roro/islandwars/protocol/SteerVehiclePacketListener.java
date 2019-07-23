package fr.roro.islandwars.protocol;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.util.protocol.TinyProtocol;
import io.netty.channel.Channel;
import java.lang.reflect.Field;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import org.bukkit.entity.Player;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class SteerVehiclePacketListener extends TinyProtocol {

    public SteerVehiclePacketListener() {
        super(IslandWars.getInstance());
    }

    @Override
    public Object onPacketInAsync(Player player, Channel channel, Object packet) {
        if (packet instanceof PacketPlayInSteerVehicle) {
            PacketPlayInSteerVehicle packetPlayInSteerVehicle = (PacketPlayInSteerVehicle) packet;

            if (packetPlayInSteerVehicle.d()) {
                try {
                    Field field = PacketPlayInSteerVehicle.class.getDeclaredField("d");
                    field.setAccessible(true);
                    field.set(packet, false);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return super.onPacketInAsync(player, channel, packet);
    }
}
