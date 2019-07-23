package fr.roro.islandwars.command;

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
public class StartCommand extends Command {

    public StartCommand() {
        super("start");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(!(sender instanceof Player)) {
            GameManager.getInstance().startGame();
            return false;
        }

        Player player = (Player) sender;
        GamePlayer gamePlayer = GameManager.getInstance().getPlayer(player);

        if(!gamePlayer.isGameManager()) {
            gamePlayer.sendMessage("§cVous n'êtes pas autorisé à utiliser cette commande.");
            return false;
        }

        if(!GameManager.getInstance().getState().equals(GameState.WAITING)) {
            gamePlayer.sendMessage("§cLa partie n'est pas en phase d'attente !");
            return false;
        }

        GameManager.getInstance().startGame();
        return true;
    }
}
