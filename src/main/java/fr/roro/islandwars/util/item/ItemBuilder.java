package fr.roro.islandwars.util.item;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * The MIT License (MIT)
 * Created on 05/12/2016.
 * Copyright (c) 2016 roro1506_HD
 */
public class ItemBuilder {

    ItemStack itemStack;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, 0);
    }

    public ItemBuilder(Material material, int amount, int durability) {
        this.itemStack = new ItemStack(material, amount, (short) durability);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemMeta getItemMeta() {
        return getItemStack().getItemMeta();
    }

    void setItemMeta(ItemMeta meta) {
        itemStack.setItemMeta(meta);
    }

    public ItemBuilder setName(String name) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(name);
        setItemMeta(meta);
        if (getItemMeta() instanceof BookMeta) {
            BookMeta meta1 = (BookMeta) getItemMeta();
            meta1.setTitle(name);
            setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addLore(String line) {
        ItemMeta meta = getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null)
            lore = Lists.newArrayList();
        lore.add(line);
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public List<String> getLore() {
        ItemMeta meta = getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null)
            lore = Lists.newArrayList();
        return lore;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta meta = getItemMeta();
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setType(Material material) {
        itemStack.setType(material);
        return this;
    }

    public ItemBuilder addFlag(ItemFlag... flags) {
        if (flags.length == 0)
            throw new IllegalArgumentException("flags must have at least one flag!");
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flags);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean force) {
        if (level == 0)
            return removeEnchantment(enchantment);

        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, force);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        return addEnchantment(enchantment, level, true);
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        ItemMeta meta = getItemMeta();
        meta.removeEnchant(enchantment);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        itemStack.setDurability((short) durability);
        return this;
    }

    public ItemBuilder setOwner(String owner) {
        if (getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) getItemMeta();
            meta.setOwner(owner);
            setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setTextures(String textures) {
        if (getItemMeta() instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) getItemMeta();
            try {
                GameProfile profile = new GameProfile(UUID.randomUUID(), null);
                profile.getProperties().put("textures", new Property("textures", textures));
                Field field = meta.getClass().getDeclaredField("profile");
                field.setAccessible(true);
                field.set(meta, profile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = getItemMeta();
        meta.spigot().setUnbreakable(unbreakable);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setColor(Color color) {
        if (getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();
            meta.setColor(color);
            setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setBaseColor(DyeColor dyeColor) {
        if (getItemMeta() instanceof BannerMeta) {
            BannerMeta meta = (BannerMeta) getItemMeta();
            meta.setBaseColor(dyeColor);
            setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addPattern(Pattern pattern) {
        if (getItemMeta() instanceof BannerMeta) {
            BannerMeta meta = (BannerMeta) getItemMeta();
            meta.addPattern(pattern);
            setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setGlowing(boolean glowing) {
        if (glowing)
            addEnchantment(EnchantmentGlow.getInstance(), 1, true);
        else
            removeEnchantment(EnchantmentGlow.getInstance());
        return this;
    }

    public ItemBuilder setAuthor(String author) {
        if (getItemMeta() instanceof BookMeta) {
            BookMeta meta = (BookMeta) getItemMeta();
            meta.setAuthor(author);
            setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setPages(List<String> pages) {
        if (getItemMeta() instanceof BookMeta) {
            BookMeta meta = (BookMeta) getItemMeta();
            meta.setPages(pages);
            setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder addPage(String content) {
        if (getItemMeta() instanceof BookMeta) {
            BookMeta meta = (BookMeta) getItemMeta();
            List<String> pages = meta.getPages();
            pages.add(content);
            meta.setPages(pages);
            setItemMeta(meta);
        }
        return this;
    }

}
