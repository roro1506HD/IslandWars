package fr.roro.islandwars.util;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class ParticleUtil {

    public static void play(Player player, EnumParticle particle, Location location, double offsetX, double offsetY, double offsetZ, double speed, int amount) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldParticles(particle, true, (float) location.getX(), (float) location.getY(), (float) location.getZ(), (float) offsetX, (float) offsetY, (float) offsetZ, (float) speed, amount));
    }

    public static void play(EnumParticle particle, Location location, double offsetX, double offsetY, double offsetZ, double speed, int amount) {
        Bukkit.getOnlinePlayers().forEach(player -> play(player, particle, location, offsetX, offsetY, offsetZ, speed, amount));
    }

}
