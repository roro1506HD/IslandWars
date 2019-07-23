package fr.roro.islandwars.bonus.custom;

import net.minecraft.server.v1_8_R3.EntityEnderPearl;
import net.minecraft.server.v1_8_R3.EntityHuman;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class CustomEnderPearl extends EntityEnderPearl {

    public CustomEnderPearl(Player shooter) {
        super(((CraftWorld) shooter.getWorld()).getHandle(), ((CraftPlayer) shooter).getHandle());
        getWorld().addEntity(this, SpawnReason.CUSTOM);
    }

    @Override
    public void t_() {
        if (getShooter() != null && getShooter() instanceof EntityHuman && getShooter().isAlive() && (passenger == null || passenger != getShooter()))
            if (passenger == null)
                getShooter().mount(this);
            else {
                passenger.mount(null);
                getShooter().mount(this);
            }

        if (this.locY <= 0) {
            this.die();
            return;
        }

        super.t_();
    }
}
