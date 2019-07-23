package fr.roro.islandwars.util.item;

import java.lang.reflect.Field;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
class EnchantmentGlow extends EnchantmentWrapper {

    private static EnchantmentGlow instance;

    private EnchantmentGlow() {
        super(99);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return true;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    public static EnchantmentGlow getInstance() {
        if (instance != null)
            return instance;

        try {
            Field field = Enchantment.class.getDeclaredField("acceptingNew");
            field.setAccessible(true);
            field.set(null, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            return instance;
        }

        EnchantmentGlow enchantmentGlow = new EnchantmentGlow();
        registerEnchantment(enchantmentGlow);

        return instance = enchantmentGlow;
    }
}
