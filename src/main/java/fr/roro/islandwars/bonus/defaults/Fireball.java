package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.bonus.custom.CustomFireball;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.util.GameUtil;
import fr.roro.islandwars.util.ParticleUtil;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.Arrays;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFireball;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class Fireball extends AbstractBonus {

    public Fireball() {
        super(new ItemBuilder(Material.FIREBALL)
                .setName("§bBoule de destruction")
                .setLore(Arrays.asList(
                        "§7Cette boule de destruction permet",
                        "§7comme son nom l'indique, de détruire",
                        "§7tous les blocs dans un rayon de 2 blocs."))
                .getItemStack(), true);
    }

    @Override
    protected void onClick(Player player, GamePlayer gamePlayer) {
        org.bukkit.entity.Fireball fireball = spawnFireball(player);

        fireball.setYield(0.0F);

        new BukkitRunnable() {
            Location startLocation = fireball.getLocation();

            @Override
            public void run() {
                if (!fireball.isValid()) {
                    cancel();
                    return;
                }

                ParticleUtil.play(EnumParticle.FLAME, fireball.getLocation(), 1, 1, 1, 0, 5);

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            Location newLocation = fireball.getLocation().clone().add(x, y, z);

                            if (newLocation.getBlock().getType() != Material.AIR && GameUtil.isBlockBreakable(newLocation.getBlock()))
                                newLocation.getBlock().setType(Material.AIR);
                        }
                    }
                }

                if (startLocation.distance(fireball.getLocation()) >= 30) {
                    cancel();
                    fireball.remove();
                    ParticleUtil.play(EnumParticle.EXPLOSION_LARGE, fireball.getLocation(), 0, 0, 0, 0, 1);
                }
            }
        }.runTaskTimer(IslandWars.getInstance(), 1, 1);
    }

    private org.bukkit.entity.Fireball spawnFireball(Player player) {
        CustomFireball customFireball = new CustomFireball(player);

        ((CraftFireball) customFireball.getBukkitEntity()).setDirection(player.getLocation().getDirection());

        return (org.bukkit.entity.Fireball) customFireball.getBukkitEntity();
    }

}
