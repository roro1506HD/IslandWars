package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class Redstone extends AbstractBonus {

    public Redstone() {
        super(new ItemBuilder(Material.REDSTONE)
                .setName("§bMine")
                .setLore(Collections.singletonList(
                        "§7Cette mine permet de faire exploser les enemis!"))
                .getItemStack(), false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.REDSTONE_WIRE || !getItemStack().isSimilar(event.getItemInHand()))
            return;

        GamePlayer gamePlayer = GameManager.getInstance().getPlayer(event.getPlayer());

        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    Block toCheck = event.getBlock().getLocation().add(x, y, z).getBlock();
                    if (toCheck.getType() == Material.REDSTONE_WIRE && toCheck.hasMetadata("owner")) {
                        gamePlayer.sendMessage("§cVous ne pouvez pas placer de mines aussi proche l'une de l'autre !");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

        gamePlayer.sendMessage("§aVous venez de placer une mine !");
        event.getBlock().setMetadata("owner", new FixedMetadataValue(IslandWars.getInstance(), event.getPlayer().getUniqueId()));

        Bukkit.getScheduler().runTask(IslandWars.getInstance(), () -> event.getBlock().removeMetadata("placed", IslandWars.getInstance()));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SPECTATOR && event.getTo().getBlock().getType() == Material.REDSTONE_WIRE && event.getTo().getBlock().hasMetadata("owner")) {
            Player owner = Bukkit.getPlayer((UUID) event.getTo().getBlock().getMetadata("owner").get(0).value());

            event.getTo().getBlock().setType(Material.AIR);
            event.getTo().getBlock().removeMetadata("owner", IslandWars.getInstance());

            TNTPrimed tntPrimed = event.getTo().getWorld().spawn(event.getTo().getBlock().getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);

            tntPrimed.setFuseTicks(1);
            tntPrimed.setIsIncendiary(false);
            tntPrimed.setVelocity(new Vector());

            EntityTNTPrimed entityTNTPrimed = ((CraftTNTPrimed) tntPrimed).getHandle();

            try {
                Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
                sourceField.setAccessible(true);
                sourceField.set(entityTNTPrimed, ((CraftPlayer) owner).getHandle());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
