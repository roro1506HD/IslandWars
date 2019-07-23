package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.util.GameUtil;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitBlockEvent;
import org.bukkit.event.entity.ProjectileHitEntityEvent;
import org.bukkit.event.entity.ProjectileUpdateLogicEvent;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class Snowball extends AbstractBonus {

    private static final List<org.bukkit.entity.Snowball>             ENTITIES = new ArrayList<>();
    private static final Map<org.bukkit.entity.Snowball, List<Block>> BLOCKS   = new HashMap<>();

    public Snowball() {
        super(new ItemBuilder(Material.SNOW_BALL)
                .setName("§bChemin de Glace")
                .setLore(Arrays.asList(
                        "§7Cette boule de neige vous permet",
                        "§7de créer une trainée de glace.",
                        "§7",
                        "§cAttention! §7Cette trainée s'enlève au",
                        "§7bout de 45 secondes!"))
                .getItemStack(), true);

    }

    @Override
    protected void onClick(Player player, GamePlayer gamePlayer) {
        org.bukkit.entity.Snowball snowball = launchSnowball(player);
        List<Block> blocks = new ArrayList<>();

        ENTITIES.add(snowball);
        BLOCKS.put(snowball, blocks);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!snowball.isValid())
                    ENTITIES.remove(snowball);
            }
        }.runTaskTimer(IslandWars.getInstance(), 20, 20);

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= (20 * 45)) {
                    cancel();
                    for (Block block : blocks) {
                        if (block.getType() == Material.SNOW_BLOCK) {
                            block.setType(Material.AIR);
                            BLOCKS.remove(snowball);
                        }
                    }
                } else if (ticks >= (20 * 44 + 10)) {
                    for (Block block : blocks) {
                        if (block.getType() == Material.PACKED_ICE)
                            block.setType(Material.SNOW_BLOCK);
                    }
                } else if (ticks >= (20 * 44)) {
                    for (Block block : blocks) {
                        if (block.getType() == Material.ICE)
                            block.setType(Material.PACKED_ICE);
                    }
                } else if (ticks >= (20 * 43 + 10)) {
                    for (Block block : blocks) {
                        if (block.getType() == Material.PACKED_ICE)
                            block.setType(Material.ICE);
                    }
                } else if (ticks >= (20 * 43)) {
                    for (Block block : blocks) {
                        if (block.getType() == Material.ICE)
                            block.setType(Material.PACKED_ICE);
                    }
                } else if (ticks >= (20 * 42 + 10)) {
                    for (Block block : blocks) {
                        if (block.getType() == Material.PACKED_ICE)
                            block.setType(Material.ICE);
                    }
                } else if (ticks >= (20 * 42)) {
                    for (Block block : blocks) {
                        if (block.getType() == Material.ICE)
                            block.setType(Material.PACKED_ICE);
                    }
                } else if (ticks >= (20 * 41 + 10)) {
                    for (Block block : blocks) {
                        if (block.getType() == Material.PACKED_ICE)
                            block.setType(Material.ICE);
                    }
                } else if (ticks >= (20 * 41)) {
                    for (Block block : blocks) {
                        if (block.getType() == Material.ICE)
                            block.setType(Material.PACKED_ICE);
                    }
                }
                ticks++;
            }
        }.runTaskTimer(IslandWars.getInstance(), 1, 1);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof org.bukkit.entity.Snowball && ENTITIES.contains(event.getDamager()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onProjectileHitBlock(ProjectileHitBlockEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Snowball)
            ENTITIES.remove(event.getEntity());
    }

    @EventHandler
    public void onProjectileHitEntity(ProjectileHitEntityEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Snowball)
            ENTITIES.remove(event.getEntity());
    }

    @EventHandler
    public void onProjectileUpdateLogic(ProjectileUpdateLogicEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Snowball && ENTITIES.contains(event.getEntity())) {
            org.bukkit.entity.Snowball snowball = (org.bukkit.entity.Snowball) event.getEntity();
            Block block = snowball.getLocation().getBlock();
            List<Block> blocks = BLOCKS.get(snowball);

            if (!block.equals(((Player) snowball.getShooter()).getEyeLocation().getBlock()) && GameUtil.isBlockBreakable(block) && block.getType() == Material.AIR && !blocks.contains(block)) {
                blocks.add(block);

                Bukkit.getScheduler().runTaskLater(IslandWars.getInstance(), () -> block.setType(Material.ICE), 5L);
            }
        }
    }

    private org.bukkit.entity.Snowball launchSnowball(Player player) {
        net.minecraft.server.v1_8_R3.EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.server.v1_8_R3.World world = entityPlayer.world;
        net.minecraft.server.v1_8_R3.Entity launch = new net.minecraft.server.v1_8_R3.EntitySnowball(world, entityPlayer);
        //((Projectile)launch.getBukkitEntity()).setVelocity(player.getLocation().getDirection().multiply(1.5));
        world.addEntity(launch);
        return (org.bukkit.entity.Snowball) launch.getBukkitEntity();
    }

}
