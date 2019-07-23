package fr.roro.islandwars.bonus;

import fr.roro.islandwars.bonus.defaults.*;
import org.bukkit.entity.Player;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public enum BonusType {

    BEDROCK(new Bedrock()),
    BLACK_WOOL(new BlackWool()),
    BLAZE_ROD(new BlazeRod()),
    EGG(new Egg()),
    EMERALD(new Emerald()),
    ENDER_EYE(new EnderEye()),
    ENDER_PEARL(new EnderPearl()),
    FIREBALL(new Fireball()),
    FLINT_AND_STEEL(new FlintAndSteel()),
    REDSTONE(new Redstone()),
    SLIME_BALL(new SlimeBall()),
    SNOWBALL(new Snowball()),
    TNT(new TNT());

    private final AbstractBonus bonus;

    BonusType(AbstractBonus bonus) {
        this.bonus = bonus;
    }

    public AbstractBonus getBonus() {
        return this.bonus;
    }

    public void giveItem(Player player) {
        player.getInventory().addItem(bonus.getItemStack());
    }

}
