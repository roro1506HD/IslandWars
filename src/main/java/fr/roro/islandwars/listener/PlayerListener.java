package fr.roro.islandwars.listener;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.GameState;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.gui.defaults.GuiOptions;
import fr.roro.islandwars.gui.defaults.GuiTeam;
import fr.roro.islandwars.gui.item.ItemManager;
import fr.roro.islandwars.util.GameUtil;
import net.minecraft.server.v1_8_R3.DamageSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = GameManager.getInstance().addPlayer(player, !GameManager.getInstance().getState().equals(GameState.WAITING));
        int players = GameManager.getInstance().getPlayers().size();

        gamePlayer.initialize();

        if (gamePlayer.isSpectator())
            Bukkit.broadcastMessage(String.format("§3§lIslandWars §8» §7%s a rejoint la partie en mode spectateur.", player.getName()));
        else
            Bukkit.broadcastMessage(String.format("§3§lIslandWars §8» §e%s a rejoint la partie ! §7(%s joueur%s)", player.getName(), players, players == 1 ? "" : "s"));

        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        boolean eliminated = GameManager.getInstance().removePlayer(player);
        int players = GameManager.getInstance().getPlayers().size();

        if (GameManager.getInstance().getState().equals(GameState.IN_GAME) && eliminated) {
            Bukkit.broadcastMessage(String.format("§3§lIslandWars §8» §c%s a quitté la partie, il/elle est donc éliminé(e).", player.getName()));
            GameManager.getInstance().checkWin();
        } else
            Bukkit.broadcastMessage(String.format("§3§lIslandWars §8» §c%s a quitté la partie §7(%s joueur%s)", player.getName(), players, players == 1 ? "" : "s"));

        event.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getY() <= 0) {
            GamePlayer gamePlayer = GameManager.getInstance().getPlayer(event.getPlayer());

            if (GameManager.getInstance().getState() == GameState.IN_GAME) {
                if (gamePlayer.isSpectator())
                    event.getPlayer().teleport(Bukkit.getOnlinePlayers().stream().filter(tempPlayer -> !GameManager.getInstance().getPlayer(tempPlayer).isSpectator()).map(Entity::getLocation).findAny().orElse(new Location(Bukkit.getWorlds().get(0), 0.5, 67, 0.5, 0, 0)));
                else
                    ((CraftPlayer) event.getPlayer()).getHandle().damageEntity(DamageSource.OUT_OF_WORLD, (float) event.getPlayer().getHealth() * 2);
            } else if(GameManager.getInstance().getState() == GameState.FINISHED) {
                if (gamePlayer.isSpectator())
                    event.getPlayer().teleport(Bukkit.getOnlinePlayers().stream().filter(tempPlayer -> !GameManager.getInstance().getPlayer(tempPlayer).isSpectator()).map(Entity::getLocation).findAny().orElse(new Location(Bukkit.getWorlds().get(0), 0.5, 67, 0.5, 0, 0)));
                else
                    event.getPlayer().teleport(gamePlayer.getTeam().getSpawn());
            } else
                event.getPlayer().teleport(new Location(Bukkit.getWorlds().get(0), 0.5, 67, 0.5, 0, 0));
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (!event.getAction().toString().contains("RIGHT"))
            return;

        ItemStack clickedItem = player.getItemInHand();

        if (clickedItem == null || clickedItem.getType().equals(Material.AIR))
            return;

        if (clickedItem.equals(ItemManager.getInstance().getTeamSelector()))
            GameManager.getInstance().getGuiManager().openGui(player, new GuiTeam());
        else if (clickedItem.equals(ItemManager.getInstance().getOptionSelector()))
            GameManager.getInstance().getGuiManager().openGui(player, new GuiOptions());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!GameManager.getInstance().getState().equals(GameState.IN_GAME)) {
            event.setCancelled(true);
            return;
        }

        if (!GameUtil.isBlockBreakable(event.getBlock())) {
            event.setCancelled(true);
            GameManager.getInstance().getPlayer(event.getPlayer()).sendMessage("§cVous ne pouvez pas construire ici !");
            return;
        }

        event.getBlock().setMetadata("placed", new FixedMetadataValue(IslandWars.getInstance(), true));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!GameManager.getInstance().getState().equals(GameState.IN_GAME)) {
            event.setCancelled(true);
            return;
        }

        if (!event.getBlock().hasMetadata("placed")) {
            event.setCancelled(true);
            GameManager.getInstance().getPlayer(event.getPlayer()).sendMessage("§cVous ne pouvez casser que les blocs que des joueurs ont posé.");
        }

        event.getBlock().removeMetadata("placed", IslandWars.getInstance());
    }

}
