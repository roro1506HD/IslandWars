package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.bonus.custom.CustomEnderPearl;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.Collections;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class EnderPearl extends AbstractBonus {

    public EnderPearl() {
        super(new ItemBuilder(Material.ENDER_PEARL)
                .setName("§bRider")
                .setLore(Collections.singletonList("§7Cette perle vous permet de voler !"))
                .getItemStack(), true);
    }

    @Override
    protected void onClick(Player player, GamePlayer gamePlayer) {
        if (player.getVehicle() != null && player.getVehicle() instanceof org.bukkit.entity.EnderPearl)
            player.getVehicle().remove();

        new CustomEnderPearl(player);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getCause() == TeleportCause.ENDER_PEARL && event.getPlayer().getVehicle() != null && event.getPlayer().getVehicle() instanceof org.bukkit.entity.EnderPearl) {
            event.setCancelled(true);
            event.getPlayer().teleport(event.getTo());
        }
    }

}
