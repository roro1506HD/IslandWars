package fr.roro.islandwars.gui.defaults;

import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.Mode;
import fr.roro.islandwars.gui.AbstractGui;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.io.File;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class GuiOptions extends AbstractGui {

    @Override
    public void display(Player player) {
        this.createInventory(1, "§dOptions");

        // Template : this.setSlotData(new ItemBuilder(Material.AIR).setName("").getItemStack(), 0, "");
        this.update(player);

        this.openInventory(player);
    }

    @Override
    public void update(Player player) {
        this.setSlotData(getModeItem(), 0, "game_mode");
        this.setSlotData(getMapItem(), 1, "game_map");
    }

    @Override
    public void onClick(Player player, ItemStack itemStack, String action, int slot) {
        if (action.equals("game_mode")) {
            GameManager.getInstance().switchMode();

            update();
        } else if (action.equals("game_map")) {
            GameManager.getInstance().switchMap();

            update();
        }
    }

    private ItemStack getModeItem() {
        return new ItemBuilder(Material.WATCH)
                .setName("§bMode")
                .addLore("§7Choisissez dans quel mode")
                .addLore("§7vous voulez vous affronter.")
                .addLore("")
                .addLore((GameManager.getInstance().getMode() == Mode.NORMAL ? "§a" : "§7") + "▪ Mode Normal")
                .addLore((GameManager.getInstance().getMode() == Mode.ULTRA ? "§3" : "§7") + "▪ Mode Ultra")
                .addLore("")
                .addLore("§e» Cliquez pour changer")
                //.addLore("§c✖ Pas encore disponible")
                .getItemStack();
    }

    private ItemStack getMapItem() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.EMPTY_MAP)
                .setName("§bCarte")
                .addLore("§7Choisissez la carte sur laquelle")
                .addLore("§7vous voulez vous affronter.")
                .addLore("");

        int mapIndex = 0;

        for (File availableMap : GameManager.getInstance().getAvailableMaps())
            itemBuilder.addLore((mapIndex++ == GameManager.getInstance().getSelectedMap() ? "§a" : "§7") + "▪ " + availableMap.getName());

        return itemBuilder.addLore("")
                .addLore("§e» Cliquez pour changer")
                .getItemStack();
    }

}
