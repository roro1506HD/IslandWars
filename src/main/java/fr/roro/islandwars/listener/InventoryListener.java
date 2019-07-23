package fr.roro.islandwars.listener;

import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.GameState;
import fr.roro.islandwars.gui.AbstractGui;
import fr.roro.islandwars.gui.GuiManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.PlayerInventory;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class InventoryListener implements Listener {

    private final GuiManager guiManager;

    public InventoryListener(GuiManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() instanceof PlayerInventory)
            if (GameManager.getInstance().getState().equals(GameState.WAITING))
                event.setCancelled(true);

        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            AbstractGui gui = this.guiManager.getPlayerGui(player);

            if (gui != null) {
                if (event.getClickedInventory() instanceof PlayerInventory)
                    return;

                String action = gui.getAction(event.getSlot());

                if (action != null)
                    gui.onClick(player, event.getCurrentItem(), action, event.getSlot(), event.getClick());

                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (this.guiManager.getPlayerGui(event.getPlayer()) != null)
            this.guiManager.removeClosedGui((Player) event.getPlayer());
    }

}
