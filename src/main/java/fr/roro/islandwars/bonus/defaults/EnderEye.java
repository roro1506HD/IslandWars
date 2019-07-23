package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.team.GameTeam;
import fr.roro.islandwars.util.GameUtil;
import fr.roro.islandwars.util.ParticleUtil;
import fr.roro.islandwars.util.WordUtil;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.Arrays;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class EnderEye extends AbstractBonus {

    public EnderEye() {
        super(new ItemBuilder(Material.EYE_OF_ENDER).setName("§bFlèches destructrices").setLore(Arrays.asList(
                "§7Ce gadget permet de rendre vos flèches destructrices!",
                "§7Cela veut dire que vos flèches causeront des",
                "§7trous lorsqu'elles toucheront un bloc!"))
                .getItemStack(), false);
    }

    @Override
    protected void onClick(Player player, GamePlayer gamePlayer) {
        GameTeam gameTeam = gamePlayer.getTeam();

        gameTeam.toggleEnderEye();
        gameTeam.broadcastMessage(gameTeam.getColor() + player.getName() + " §ea activé les flèches destructrices !");

        Bukkit.getScheduler().runTaskLater(IslandWars.getInstance(), () -> {
            gameTeam.toggleEnderEye();
            gameTeam.broadcastMessage("§cLes flèches destructrices d" + (WordUtil.isVowel(player.getName()) ? "'" : "e ") + gameTeam.getColor() + player.getName() + " §cont expirées.");
        }, 20 * 20);
    }

    @EventHandler
    public void onProjectileHitBlock(ProjectileHitBlockEvent event) {
        if (!(event.getEntity() instanceof Arrow))
            return;

        Arrow arrow = (Arrow) event.getEntity();

        if (arrow.getShooter() == null || !(arrow.getShooter() instanceof Player))
            return;

        GameTeam gameTeam = GameManager.getInstance().getPlayer((Player) arrow.getShooter()).getTeam();

        if (GameUtil.isBlockBreakable(event.getBlock()) && gameTeam != null && gameTeam.hasEnderEyeEnabled()) {
            arrow.remove();
            event.getBlock().setType(Material.AIR);
            ParticleUtil.play(EnumParticle.FLAME, event.getBlock().getLocation().add(0.5, 0.5, 0.5), 0, 0, 0, 0.05, 10);
            event.getBlock().getWorld().playSound(event.getBlock().getLocation().add(0.5, 0.5, 0.5), Sound.CHICKEN_EGG_POP, 1.0F, 1.0F);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
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

        if (gamePlayer.getTeam().hasEnderEyeEnabled()) {
            gamePlayer.sendMessage("§cVotre équipe possède déjà les flèches destructrices, veuillez attendre un peu avant d'utiliser ce bonus !");
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

}
