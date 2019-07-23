package fr.roro.islandwars.scoreboard;

import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.game.player.statistic.StatisticType;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class GameScoreboard {

    private int scoresIndex = -1;
    private int statsIndex  = -1;
    private int timerIndex  = -1;

    public void initScoreboard(GamePlayer player) {
        int index = 0;

        player.getScoreboard().setLine(index++, "§a");

        if (this.scoresIndex == -1)
            this.scoresIndex = index;

        index = updateScores(player, index);

        player.getScoreboard().setLine(index++, "§b");

        if (this.statsIndex == -1)
            this.statsIndex = index;

        index = updateStats(player, index);

        player.getScoreboard().setLine(index++, "§c");

        if (this.timerIndex == -1)
            this.timerIndex = index;

        index = updateTimer(player, index);

        player.getScoreboard().setLine(index, "§d");
    }

    public void updateScores(GamePlayer player) {
        updateScores(player, this.scoresIndex);
    }

    private int updateScores(GamePlayer player, int index) {
        player.getScoreboard().setLine(index++, "§9Bleus : §f" + GameManager.getInstance().getBlueTeam().getScore());
        player.getScoreboard().setLine(index++, "§cRouges : §f" + GameManager.getInstance().getRedTeam().getScore());
        return index;
    }

    public void updateStats(GamePlayer player) {
        updateStats(player, this.statsIndex);
    }

    private int updateStats(GamePlayer player, int index) {
        player.getScoreboard().setLine(index++, "Kills : §3" + player.getStatistic(StatisticType.KILLS).getValue() + "§f/§9" + player.getStatistic(StatisticType.TEAM_KILLS).getValue());

        if (player.getTeam() == null)
            player.getScoreboard().setLine(index++, "Team Kills : §3" + player.getTeam().getKills() + "§f/§9" + player.getTeam().getTeamKills());
        else
            player.getScoreboard().setLine(index++, "Team Kills : §30§f/§90");

        player.getScoreboard().setLine(index++, "Morts : §c" + player.getStatistic(StatisticType.DEATHS).getValue());
        return index;
    }

    public void updateTimer(GamePlayer player) {
        updateTimer(player, this.timerIndex);
    }

    private int updateTimer(GamePlayer player, int index) {
        player.getScoreboard().setLine(index++, formatTimer(GameManager.getInstance().getTimeElapsed()));
        return index;
    }

    private String formatTimer(int time) {
        return "Temps : §6" + String.format("%02d:%02d", time / 60, time % 60);
    }


}
