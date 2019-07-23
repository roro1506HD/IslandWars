package fr.roro.islandwars.listener;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.GameState;
import fr.roro.islandwars.util.GameUtil;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Iterator;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTNTFuseEvent;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class EntityListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!GameManager.getInstance().getState().equals(GameState.IN_GAME) && event.getEntity() instanceof Player)
            event.setCancelled(true);

        if(event.getEntity() instanceof Player && GameManager.getInstance().getPlayer(event.getEntity().getUniqueId()).isRespawning())
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof TNTPrimed)
            event.getEntity().setCustomNameVisible(true);
    }

    @EventHandler
    public void onEntityTNTFuse(EntityTNTFuseEvent event) {
        DecimalFormat format = new DecimalFormat("0.0");

        if (!event.getEntity().isCustomNameVisible())
            event.getEntity().setCustomNameVisible(true);

        event.getEntity().setCustomName("§cAttention ! §r" + format.format(event.getFuseTicks() / 20.0D));
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Iterator<Block> blockIterator = event.blockList().iterator();

        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();

            if(GameUtil.isBlockBreakable(block)) {
                if(block.hasMetadata("placed"))
                    block.removeMetadata("placed", IslandWars.getInstance());

                if(block.hasMetadata("owner"))
                    block.removeMetadata("owner", IslandWars.getInstance());
            }

            if (!GameUtil.isBlockBreakable(block))
                blockIterator.remove();
            else if (block.getType().equals(Material.TNT)) {
                blockIterator.remove();
                block.setType(Material.AIR);

                TNTPrimed tntPrimed = block.getWorld().spawn(block.getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);

                if (((TNTPrimed) event.getEntity()).getSource() != null) {
                    Entity source = ((TNTPrimed) event.getEntity()).getSource();

                    try {
                        Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
                        sourceField.setAccessible(true);
                        sourceField.set(((CraftTNTPrimed) tntPrimed).getHandle(), ((CraftLivingEntity) source).getHandle());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

}
