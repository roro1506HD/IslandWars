package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.team.GameTeam;
import fr.roro.islandwars.util.WordUtil;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class FlintAndSteel extends AbstractBonus {

    public FlintAndSteel() {
        super(new ItemBuilder(Material.FLINT_AND_STEEL)
                .setName("§bBriquet")
                .setLore(Arrays.asList(
                        "§7Ce briquet n'est pas comme les autres!",
                        "§7Il permet d'enflammer vos arcs et vos épées!",
                        "§7",
                        "§cCependant§7, comme toute bonne chose,",
                        "§7il a une durée limitée!"))
                .getItemStack(), false);
    }

    @Override
    protected void onClick(Player player, GamePlayer gamePlayer) {
        modifyStuff(player, 1);
        Bukkit.getScheduler().runTaskLater(IslandWars.getInstance(), () -> modifyStuff(player, 0), 20 * 10);
    }

    @Override
    @EventHandler
    public void _onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT"))
            return;

        Player player = event.getPlayer();
        GamePlayer gamePlayer = GameManager.getInstance().getPlayer(player);

        if (player.getItemInHand() == null || !player.getItemInHand().isSimilar(getItemStack()))
            return;

        if (!gamePlayer.hasTeam()) {
            gamePlayer.sendMessage("§cVous devez être dans une équipe pour utiliser ce bonus.");
            return;
        }

        if (gamePlayer.getTeam().hasFlintAndSteelEnabled()) {
            gamePlayer.sendMessage("§cVotre équipe possède déjà les outils enflammés, veuillez patienter un peut avant d'utiliser ce bonus !");
            event.setCancelled(true);
            return;
        }

        onClick(player, GameManager.getInstance().getPlayer(player));
        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1)
            player.setItemInHand(new ItemStack(Material.AIR));
        else
            player.setItemInHand(new ItemBuilder(player.getItemInHand()).setAmount(player.getItemInHand().getAmount() - 1).getItemStack());
    }

    private void modifyStuff(Player player, int enchantmentLevel) {
        GameTeam gameTeam = GameManager.getInstance().getPlayer(player).getTeam();

        gameTeam.toggleFlintAndSteel();

        if (enchantmentLevel == 1)
            gameTeam.broadcastMessage(gameTeam.getColor() + player.getName() + " §ea activé les outils enflammés !");
        else if (enchantmentLevel == 0)
            gameTeam.broadcastMessage("§cLes outils enflammés d" + (WordUtil.isVowel(player.getName()) ? "'" : "e ") + gameTeam.getColor() + player.getName() + " §cont expirés.");

        for (GamePlayer gamePlayer : gameTeam.getGameMembers()) {
            ItemStack[] contents = gamePlayer.getPlayer().getInventory().getContents();

            enchantContents(enchantmentLevel, contents);
            gamePlayer.getPlayer().getInventory().setContents(contents);

            contents = gamePlayer.getPlayer().getOpenInventory().getTopInventory().getContents();

            enchantContents(enchantmentLevel, contents);
            gamePlayer.getPlayer().getOpenInventory().getTopInventory().setContents(contents);

            if (gamePlayer.getPlayer().getItemOnCursor() != null) {
                if (gamePlayer.getPlayer().getItemOnCursor().getType() == Material.STONE_SWORD)
                    gamePlayer.getPlayer().setItemOnCursor(new ItemBuilder(gamePlayer.getPlayer().getItemOnCursor()).addEnchantment(Enchantment.FIRE_ASPECT, enchantmentLevel).getItemStack());

                if (gamePlayer.getPlayer().getItemOnCursor().getType() == Material.BOW)
                    gamePlayer.getPlayer().setItemOnCursor(new ItemBuilder(gamePlayer.getPlayer().getItemOnCursor()).addEnchantment(Enchantment.ARROW_FIRE, enchantmentLevel).getItemStack());

                gamePlayer.getPlayer().updateInventory();
            }
        }
    }

    private void enchantContents(int enchantmentLevel, ItemStack[] contents) {
        for (int i = 0; i < contents.length; i++) {
            ItemStack itemStack = contents[i];

            if (itemStack == null)
                continue;

            if (itemStack.getType() == Material.STONE_SWORD)
                contents[i] = new ItemBuilder(itemStack).addEnchantment(Enchantment.FIRE_ASPECT, enchantmentLevel).getItemStack();

            if (itemStack.getType() == Material.BOW)
                contents[i] = new ItemBuilder(itemStack).addEnchantment(Enchantment.ARROW_FIRE, enchantmentLevel).getItemStack();
        }
    }

}
