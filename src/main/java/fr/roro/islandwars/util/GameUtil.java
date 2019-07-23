package fr.roro.islandwars.util;

import fr.roro.islandwars.game.GameManager;
import org.bukkit.block.Block;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class GameUtil {

    public static boolean isBlockBreakable(Block block) {
        boolean breakable = GameManager.getInstance().getLocation("redSpawn").stream().noneMatch(location -> location.distance(block.getLocation()) < 11);

        if (breakable)
            breakable = GameManager.getInstance().getLocation("blueSpawn").stream().noneMatch(location -> location.distance(block.getLocation()) < 11);

        return breakable;
    }

}
