package fr.roro.islandwars.command;

import fr.roro.islandwars.bonus.BonusType;
import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.GameState;
import fr.roro.islandwars.game.player.GamePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class GiveCommand extends Command {

    public GiveCommand() {
        super("iwgive");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player))
            return false;

        Player player = (Player) sender;
        GamePlayer gamePlayer = GameManager.getInstance().getPlayer(player);

        if(!gamePlayer.isGameManager()) {
            gamePlayer.sendMessage("§cVous n'êtes pas autorisé à utiliser cette commande.");
            return false;
        }

        if(GameManager.getInstance().getState() != GameState.IN_GAME) {
            gamePlayer.sendMessage("§cCette commande n'est utilisable seulement en jeu !");
            return false;
        }

        try {
            BonusType bonusType = BonusType.valueOf(args[0]);

            bonusType.giveItem(player);

            GameManager.getInstance().broadcastMessage("§a" + player.getName() + " §evient de se give " + bonusType.getBonus().getItemStack().getItemMeta().getDisplayName());
        } catch (Exception e) {
            sender.sendMessage("Error!");
        }
        return true;
    }
}
