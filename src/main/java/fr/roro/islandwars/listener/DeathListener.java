package fr.roro.islandwars.listener;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.GameState;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.game.player.statistic.StatisticType;
import fr.roro.islandwars.scoreboard.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (GameManager.getInstance().getState() != GameState.IN_GAME)
            return;

        Player player = event.getEntity();
        GamePlayer gamePlayer = GameManager.getInstance().getPlayer(player);

        player.setHealth(20);

        if (!gamePlayer.hasTeam())
            return;

        if (event.getEntity().getKiller() == null)
            GameManager.getInstance().broadcastMessage(gamePlayer.getTeam().getColor() + player.getName() + " §eest mort.");
        else {
            if (event.getEntity().getKiller().equals(player))
                GameManager.getInstance().broadcastMessage(gamePlayer.getTeam().getColor() + player.getName() + " §ea été tué par " + gamePlayer.getTeam().getColor() + "lui même...");
            else
                GameManager.getInstance().broadcastMessage(gamePlayer.getTeam().getColor() + player.getName() + " §ea été tué par " + GameManager.getInstance().getTeam(player.getKiller()).getColor() + player.getKiller().getName());

            if (!gamePlayer.getTeam().hasPlayer(event.getEntity().getKiller()))
                GameManager.getInstance().getPlayer(event.getEntity().getKiller()).getStatistic(StatisticType.KILLS).incrementValue(1);
            else if(!player.equals(player.getKiller()))
                GameManager.getInstance().getPlayer(event.getEntity().getKiller()).getStatistic(StatisticType.TEAM_KILLS).incrementValue(1);

            ScoreboardManager.getInstance().getGameScoreboard().updateStats(GameManager.getInstance().getPlayer(event.getEntity().getKiller()));
        }
        event.setDeathMessage(null);

        Bukkit.getScheduler().runTask(IslandWars.getInstance(), () -> player.teleport(gamePlayer.getTeam().getSpawn()));

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        gamePlayer.giveItems();
        gamePlayer.toggleRespawnStatus();

        if (gamePlayer.hasBlazeRodEnabled())
            gamePlayer.toggleBlazeRod();

        if (gamePlayer.hasEmeraldEnabled())
            gamePlayer.toggleEmerald();

        gamePlayer.getStatistic(StatisticType.DEATHS).incrementValue(1);
        ScoreboardManager.getInstance().getGameScoreboard().updateStats(gamePlayer);

        gamePlayer.sendMessage("§aVous êtes invincible pendant 5 secondes !");

        if (gamePlayer.getTeam().equals(GameManager.getInstance().getRedTeam()))
            GameManager.getInstance().getBlueTeam().incrementScore();
        else
            GameManager.getInstance().getRedTeam().incrementScore();

        Bukkit.getScheduler().runTaskLater(IslandWars.getInstance(), () -> {
            gamePlayer.toggleRespawnStatus();
            gamePlayer.sendMessage("§cVous n'êtes plus invincible !");
        }, 20 * 5);

        GameManager.getInstance().getAllPlayers().forEach(ScoreboardManager.getInstance().getGameScoreboard()::updateScores);
        GameManager.getInstance().checkWin();
    }

}
