package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.EntityTNTPrimed;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitBlockEvent;
import org.bukkit.event.entity.ProjectileHitEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class Egg extends AbstractBonus {

    public Egg() {
        super(new ItemBuilder(Material.EGG)
                .setName("§bGrenade")
                .setLore(Arrays.asList(
                        "§7Cet oeuf a la particularité d'exploser à l'impact.",
                        "§7Oui, c'est une §cgrenade§7."))
                .getItemStack(), true);
    }

    @Override
    protected void onClick(Player player, GamePlayer gamePlayer) {
        org.bukkit.entity.Egg egg = player.launchProjectile(org.bukkit.entity.Egg.class, player.getLocation().getDirection().multiply(1.5));
        egg.setMetadata("source", new FixedMetadataValue(IslandWars.getInstance(), player.getUniqueId()));
    }

    @EventHandler
    public void onProjectileHitBlock(ProjectileHitBlockEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Egg && event.getEntity().hasMetadata("source"))
            explode(event.getEntity());
    }

    @EventHandler
    public void onProjectileHitEntity(ProjectileHitEntityEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Egg && event.getEntity().hasMetadata("source"))
            explode(event.getEntity());
    }

    private void explode(Entity entity) {
        entity.remove();

        Player source = Bukkit.getPlayer((UUID) entity.getMetadata("source").get(0).value());
        TNTPrimed tntPrimed = entity.getWorld().spawn(entity.getLocation(), TNTPrimed.class);

        tntPrimed.setFuseTicks(1);
        tntPrimed.setIsIncendiary(false);
        tntPrimed.setVelocity(new Vector());

        EntityTNTPrimed entityTNTPrimed = ((CraftTNTPrimed) tntPrimed).getHandle();

        try {
            Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
            sourceField.setAccessible(true);
            sourceField.set(entityTNTPrimed, ((CraftPlayer) source).getHandle());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
