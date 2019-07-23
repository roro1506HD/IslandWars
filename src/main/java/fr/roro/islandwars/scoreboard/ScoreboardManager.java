package fr.roro.islandwars.scoreboard;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class ScoreboardManager {

    private static final ScoreboardManager instance = new ScoreboardManager();
    private              WaitingScoreboard waitingScoreboard;
    private              GameScoreboard    gameScoreboard;

    private ScoreboardManager() {
    }

    public static ScoreboardManager getInstance() {
        return instance;
    }

    public void setupScoreboards() {
        this.waitingScoreboard = new WaitingScoreboard();
        this.gameScoreboard = new GameScoreboard();
    }

    public WaitingScoreboard getWaitingScoreboard() {
        return this.waitingScoreboard;
    }

    public GameScoreboard getGameScoreboard() {
        return this.gameScoreboard;
    }
}
