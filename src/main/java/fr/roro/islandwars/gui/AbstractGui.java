package fr.roro.islandwars.gui;

import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.gui.defaults.GuiOptions;
import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public abstract class AbstractGui {

    private HashMap<Integer, String> actions = new HashMap<>();
    private Inventory                inventory;

    public abstract void display(Player player);

    public void update(Player player) {

    }

    protected void update() {
        for (Player tempPlayer : Bukkit.getOnlinePlayers()) {
            AbstractGui abstractGui = GameManager.getInstance().getGuiManager().getPlayerGui(tempPlayer.getUniqueId());
            if (abstractGui != null && abstractGui.getClass().isInstance(this))
                abstractGui.update(tempPlayer);
        }
    }

    public void onClose(Player player) {

    }

    public void onClick(Player player, ItemStack itemStack, String action, int slot, ClickType clickType) {
        this.onClick(player, itemStack, action, slot);
    }

    public void onClick(Player player, ItemStack itemStack, String action, int slot) {

    }

    protected void createInventory(int rows, String name) {
        this.inventory = Bukkit.createInventory(null, rows * 9, name);
    }

    protected void openInventory(Player player) {
        player.openInventory(this.inventory);
    }

    public void setSlotData(Inventory inventory, String name, Material material, int slot, String[] description, String action) {
        this.setSlotData(inventory, name, new ItemStack(material, 1), slot, description, action);
    }

    public void setSlotData(String name, Material material, int slot, String[] description, String action) {
        this.setSlotData(this.inventory, name, new ItemStack(material, 1), slot, description, action);
    }

    public void setSlotData(String name, ItemStack item, int slot, String[] description, String action) {
        this.setSlotData(this.inventory, name, item, slot, description, action);
    }

    public void setSlotData(Inventory inv, String name, ItemStack item, int slot, String[] description, String action) {
        this.actions.put(slot, action);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);

        if (description != null)
            meta.setLore(Arrays.asList(description));

        item.setItemMeta(meta);
        inv.setItem(slot, item);
    }

    public void setSlotData(Inventory inv, ItemStack item, int slot, String action) {
        this.actions.put(slot, action);
        inv.setItem(slot, item);
    }

    public void setSlotData(ItemStack item, int slot, String action) {
        setSlotData(this.inventory, item, slot, action);
    }

    public String getAction(int slot) {
        if (!this.actions.containsKey(slot))
            return null;

        return this.actions.get(slot);
    }

    public Inventory getInventory() {
        return this.inventory;
    }

}
