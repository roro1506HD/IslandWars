package fr.roro.islandwars.gui.defaults;

import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.gui.AbstractGui;
import fr.roro.islandwars.gui.item.ItemManager;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class GuiTeam extends AbstractGui {

    @Override
    public void display(Player player) {
        this.createInventory(1, "§aSélecteur d'équipe");

        // Template : this.setSlotData(new ItemBuilder(Material.AIR).setName("").getItemStack(), 0, "");
        this.update(player);

        this.openInventory(player);
    }

    @Override
    public void update(Player player) {
        this.setSlotData(GameManager.getInstance().getRedTeam().getBannerItem(player), 0, "team_red");
        this.setSlotData(GameManager.getInstance().getBlueTeam().getBannerItem(player), 1, "team_blue");

        this.setSlotData(getRandomTeamItem(), 8, "team_random");
    }

    @Override
    public void onClick(Player player, ItemStack itemStack, String action, int slot) {
        if (action.equals("team_red")) {
            if (!GameManager.getInstance().getRedTeam().hasPlayer(player)) {
                if (GameManager.getInstance().getTeam(player) != null)
                    GameManager.getInstance().getTeam(player).removePlayer(player);

                GameManager.getInstance().getRedTeam().addPlayer(player);

                update();
            }
        } else if (action.equals("team_blue")) {
            if (!GameManager.getInstance().getBlueTeam().hasPlayer(player)) {
                if (GameManager.getInstance().getTeam(player) != null)
                    GameManager.getInstance().getTeam(player).removePlayer(player);

                GameManager.getInstance().getBlueTeam().addPlayer(player);

                update();
            }
        } else if (action.equals("team_random")) {
            if (GameManager.getInstance().getTeam(player) != null) {
                GameManager.getInstance().getTeam(player).removePlayer(player);

                update();
            }
        }
    }

    private ItemStack getRandomTeamItem() {
        return new ItemBuilder(ItemManager.getInstance().getTeamSelector())
                .setName("§3Equipe aléatoire")
                .setLore(new ArrayList<>())
                .addLore("§7Choisissez cette équipe")
                .addLore("§7pour plus de suspense")
                .setAmount(GameManager.getInstance().getPlayers().size() - (GameManager.getInstance().getRedTeam().getGameMembers().size() + GameManager.getInstance().getBlueTeam().getGameMembers().size()))
                .getItemStack();
    }

}
