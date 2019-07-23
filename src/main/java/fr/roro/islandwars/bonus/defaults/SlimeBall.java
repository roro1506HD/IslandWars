package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class SlimeBall extends AbstractBonus {

    public SlimeBall() {
        super(new ItemBuilder(Material.SLIME_BALL)
                .setName("§bPropulseur")
                .setLore(Collections.singletonList("§7Envoyez vos ennemis en l'air !"))
                .getItemStack(), true);
    }

    @Override
    protected void onClick(Player player, GamePlayer gamePlayer) {
        for (Player tempPlayer : Bukkit.getOnlinePlayers())
            if (tempPlayer != player && tempPlayer.getWorld().equals(player.getWorld()) && tempPlayer.getGameMode() != GameMode.SPECTATOR && tempPlayer.getLocation().distance(player.getLocation()) < 7)
                tempPlayer.setVelocity(tempPlayer.getVelocity().setY(0.3D + Math.random()));
    }

}
