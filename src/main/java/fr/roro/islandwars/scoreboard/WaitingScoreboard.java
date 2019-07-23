package fr.roro.islandwars.scoreboard;

import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.player.GamePlayer;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class WaitingScoreboard {

    private int playersIndex = -1;
    private int timerIndex   = -1;

    public void initScoreboard(GamePlayer player) {
        int index = 0;

        player.getScoreboard().setLine(index++, "§a");

        if(this.playersIndex == -1)
            this.playersIndex = index;

        index = updatePlayers(player, index);

        player.getScoreboard().setLine(index++, "§b");

        if(this.timerIndex == -1)
            this.timerIndex = index;

        index = updateTimer(player, index);

        player.getScoreboard().setLine(index, "§c");
    }

    public void updatePlayers(GamePlayer player) {
        updatePlayers(player, this.playersIndex);
    }

    private int updatePlayers(GamePlayer player, int index) {
        player.getScoreboard().setLine(index++, "Joueurs : §a" + GameManager.getInstance().getPlayers().size());
        return index;
    }

    private int updateTimer(GamePlayer player, int index) {
        player.getScoreboard().setLine(index++, "Attente...");
        return index;
    }

}
