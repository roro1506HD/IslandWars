package fr.roro.islandwars.gui;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.listener.InventoryListener;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class GuiManager {

    private final ConcurrentHashMap<UUID, AbstractGui> currentGuis;

    public GuiManager() {
        this.currentGuis = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(new InventoryListener(this), IslandWars.getInstance());
    }

    public void openGui(Player player, AbstractGui gui) {
        if (this.currentGuis.containsKey(player.getUniqueId()))
            this.closeGui(player);
        this.currentGuis.put(player.getUniqueId(), gui);
        gui.display(player);
    }

    public void closeGui(Player player) {
        player.closeInventory();
        this.removeClosedGui(player);
    }

    public void removeClosedGui(Player player) {
        if (this.currentGuis.containsKey(player.getUniqueId())) {
            this.getPlayerGui(player).onClose(player);
            this.currentGuis.remove(player.getUniqueId());
        }
    }

    public AbstractGui getPlayerGui(HumanEntity player) {
        return getPlayerGui(player.getUniqueId());
    }

    public AbstractGui getPlayerGui(UUID uuid) {
        if (this.currentGuis.containsKey(uuid))
            return this.currentGuis.get(uuid);
        return null;
    }

    public ConcurrentHashMap<UUID, AbstractGui> getCurrentGuis() {
        return currentGuis;
    }

}
