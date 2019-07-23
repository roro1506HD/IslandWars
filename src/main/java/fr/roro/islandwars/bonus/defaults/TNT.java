package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.lang.reflect.Field;
import java.util.Arrays;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class TNT extends AbstractBonus {

    public TNT() {
        super(new ItemBuilder(Material.TNT)
                .setName("§bTNT")
                .setLore(Arrays.asList(
                        "§7Cette §cT§fN§cT §7est un explosif hors du",
                        "§7commun ! Grâce à son électronique sofistiqué,",
                        "§7elle s'allume toute seule dès qu'on la pose!"))
                .getItemStack(), false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() != Material.TNT || !event.getItemInHand().isSimilar(getItemStack()))
            return;

        event.getBlock().setType(Material.AIR);

        TNTPrimed tntPrimed = event.getBlock().getWorld().spawn(event.getBlock().getLocation().add(0.5, 0.5, 0.5), TNTPrimed.class);

        tntPrimed.setFuseTicks(40);
        tntPrimed.setIsIncendiary(false);
        tntPrimed.setVelocity(new Vector());

        EntityTNTPrimed entityTNTPrimed = ((CraftTNTPrimed) tntPrimed).getHandle();

        try {
            Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
            sourceField.setAccessible(true);
            sourceField.set(entityTNTPrimed, ((CraftPlayer) event.getPlayer()).getHandle());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
