package fr.roro.islandwars.bonus;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.util.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public abstract class AbstractBonus implements Listener {

    private final ItemStack itemStack;
    private final boolean   canInteract;

    protected AbstractBonus(ItemStack itemStack, boolean interact) {
        this.itemStack = itemStack;
        this.canInteract = interact;

        Bukkit.getPluginManager().registerEvents(this, IslandWars.getInstance());
    }

    protected void onClick(Player player, GamePlayer gamePlayer) {
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @EventHandler
    public void _onInteract(PlayerInteractEvent event) {
        if (!this.canInteract || !event.getAction().name().contains("RIGHT"))
            return;

        Player player = event.getPlayer();

        if (player.getItemInHand() == null || !player.getItemInHand().isSimilar(this.itemStack))
            return;

        onClick(player, GameManager.getInstance().getPlayer(player));
        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1)
            player.setItemInHand(new ItemStack(Material.AIR));
        else
            player.setItemInHand(new ItemBuilder(player.getItemInHand()).setAmount(player.getItemInHand().getAmount() - 1).getItemStack());
    }

}
