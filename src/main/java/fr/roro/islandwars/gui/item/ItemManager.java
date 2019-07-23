package fr.roro.islandwars.gui.item;

import fr.roro.islandwars.util.item.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class ItemManager {

    private static ItemManager instance;

    private ItemManager() {

    }

    public static ItemManager getInstance() {
        return instance == null ? instance = new ItemManager() : instance;
    }

    public ItemStack getTeamSelector() {
        return new ItemBuilder(Material.BANNER)
                .setBaseColor(DyeColor.BLUE)
                .addPattern(new Pattern(DyeColor.RED, PatternType.DIAGONAL_RIGHT))
                .addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_DOWNLEFT))
                .addFlag(ItemFlag.HIDE_POTION_EFFECTS)
                .setName("§aSélecteur d'équipe §8▪ §7Clic-droit")
                .getItemStack();
    }

    public ItemStack getOptionSelector() {
        return new ItemBuilder(Material.REDSTONE_COMPARATOR)
                .setName("§dOptions §8▪ §7Clic-droit")
                .getItemStack();
    }

}
