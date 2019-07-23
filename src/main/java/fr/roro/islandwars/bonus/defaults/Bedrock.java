package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class Bedrock extends AbstractBonus {

    public Bedrock() {
        super(new ItemBuilder(Material.BEDROCK)
                .setAmount(3)
                .setName("§bPortion de Mur")
                .setLore(Arrays.asList(
                        "§7Avez-vous déjà rêvé de créer des",
                        "§7murs presque incassable ? Cet objet",
                        "§7est fait pour vous!"))
                .getItemStack(), false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlock().getType() == Material.BEDROCK) {
            if (event.getItemInHand().isSimilar(getItemStack())) {
                event.getBlock().setType(Material.AIR);
                event.getBlock().getLocation().add(0, 0, 0).getBlock().setType(Material.BEDROCK);
                event.getBlock().getLocation().add(0, 1, 0).getBlock().setType(Material.BEDROCK);
                event.getBlock().getLocation().add(0, 2, 0).getBlock().setType(Material.BEDROCK);
            }
        }
    }

}
