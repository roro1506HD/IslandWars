package fr.roro.islandwars.bonus.defaults;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.bonus.AbstractBonus;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.util.block.BlockPattern;
import fr.roro.islandwars.util.block.GeometryUtil;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.Arrays;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class BlackWool extends AbstractBonus {

    public BlackWool() {
        super(new ItemBuilder(Material.WOOL, 1, 15)
                .setName("§bPlateforme")
                .setLore(Arrays.asList(
                        "§7Cet objet permet de créer une",
                        "§7plateforme sous vos pieds!"))
                .getItemStack(), true);
    }

    @Override
    public void onClick(Player player, GamePlayer gamePlayer) {
        if (!gamePlayer.hasTeam()) {
            gamePlayer.sendMessage("§cVous devez être dans une équipe pour utiliser ce bonus.");
            return;
        }

        GeometryUtil.makeCylinder(player.getLocation(), new BlockPattern(Material.WOOL, gamePlayer.getTeam().getDyeColor().getWoolData()), 3.0, -2, true, block -> block.setMetadata("placed", new FixedMetadataValue(IslandWars.getInstance(), true)));
    }


}
