package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.util.ParticleUtil;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.Arrays;
import java.util.Random;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class BlazeRod extends AbstractBonus {

    private static final Random RANDOM = new Random();

    public BlazeRod() {
        super(new ItemBuilder(Material.BLAZE_ROD)
                .setName("§bBâton Enflammé")
                .setLore(Arrays.asList(
                        "§7Grâce à ce bâton magique, vous pouvez",
                        "§7enflammer vos enemis dans un rayon de 3 blocs!"))
                .getItemStack(), false);
    }

    @Override
    protected void onClick(Player player, GamePlayer gamePlayer) {
        gamePlayer.toggleBlazeRod();

        new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                if (timer++ > (20 * 10) && gamePlayer.hasBlazeRodEnabled())
                    gamePlayer.toggleBlazeRod();

                if(!gamePlayer.hasBlazeRodEnabled()) {
                    cancel();
                    return;
                }

                circle(player.getLocation().add(0, 0.1, 0));

                Bukkit.getOnlinePlayers().stream()
                        .filter(tempPlayer -> tempPlayer.getWorld().equals(player.getWorld()))
                        .filter(tempPlayer -> tempPlayer.getLocation().distance(player.getLocation()) < 3)
                        .filter(tempPlayer -> tempPlayer.getGameMode() != GameMode.SPECTATOR && tempPlayer.getGameMode() != GameMode.CREATIVE)
                        .map(GameManager.getInstance()::getPlayer)
                        .filter(tempPlayer -> tempPlayer.hasTeam() && !tempPlayer.getTeam().hasPlayer(player))
                        .forEach(tempPlayer -> {
                            tempPlayer.getPlayer().setFireTicks(10);
                            tempPlayer.getPlayer().getHandle().damageEntity(DamageSource.BURN, 1);
                        });
            }
        }.runTaskTimer(IslandWars.getInstance(), 1, 1);
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

        if (gamePlayer.hasBlazeRodEnabled()) {
            gamePlayer.sendMessage("§cVous avez déjà ce bonus actif ! Veuillez attendre la fin de celui-ci pour l'activer une nouvelle fois !");
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

    private void circle(Location location) {
        int rand = RANDOM.nextInt(50);

        for (int i = 0; i < 50; i++) {
            double angle, x, z;

            angle = 2 * Math.PI * i / 50;
            x = Math.cos(angle) * 3.0;
            z = Math.sin(angle) * 3.0;

            Vector original = location.toVector();

            location.add(x, 0, z);

            Vector newVector = location.toVector();
            Vector calculated = original.subtract(newVector);

            if (i == rand)
                ParticleUtil.play(EnumParticle.FLAME, location, calculated.getX(), calculated.getY(), calculated.getZ(), 0.05, 0);

            ParticleUtil.play(EnumParticle.FLAME, location, 0, 0, 0, 0, 1);

            location.subtract(x, 0, z);
        }
    }

}
