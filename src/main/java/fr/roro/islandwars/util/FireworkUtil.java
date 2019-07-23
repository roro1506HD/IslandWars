package fr.roro.islandwars.util;

import fr.roro.islandwars.game.GameManager;
import java.util.Random;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class FireworkUtil {

    public static FireworkEffect getRandomFirework() {
        Random random = GameManager.getInstance().getRandom();

        FireworkEffect.Builder effectBuilder = FireworkEffect.builder()
                .flicker(random.nextBoolean())
                .trail(random.nextBoolean())
                .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)]);

        for (int i = 0; i < random.nextInt(3) + 1; i++) {
            effectBuilder.withColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }

        for (int i = 0; i < random.nextInt(3) + 1; i++) {
            effectBuilder.withFade(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }

        return effectBuilder.build();
    }

}
