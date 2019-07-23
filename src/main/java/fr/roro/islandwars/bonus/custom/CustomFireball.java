package fr.roro.islandwars.bonus.custom;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityFireball;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class CustomFireball extends EntityFireball {

    public CustomFireball(Player shooter) {
        super(((CraftWorld) shooter.getWorld()).getHandle());

        setLocation(shooter.getLocation().getX(), shooter.getLocation().getY(), shooter.getLocation().getZ(), shooter.getLocation().getYaw(), shooter.getLocation().getPitch());

        this.shooter = ((CraftPlayer) shooter).getHandle();

        getWorld().addEntity(this, SpawnReason.CUSTOM);
    }

    @Override
    protected float j() {
        return 0.50F;
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        if (!this.world.isClientSide) {
            if (movingobjectposition.entity != null) {
                if (!(movingobjectposition.entity instanceof EntityPlayer) && !(movingobjectposition.entity instanceof EntityFireball)) {
                    movingobjectposition.entity.damageEntity(DamageSource.fireball(this, this.shooter), 6.0F);
                    this.a(this.shooter, movingobjectposition.entity);
                }
            } else
                this.die();
        }
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.ac();
            if (damagesource.getEntity() != null) {
                return !CraftEventFactory.handleNonLivingEntityDamageEvent(this, damagesource, (double) f);
            } else {
                return false;
            }
        }
    }

}
