package fr.roro.islandwars.game;

import fr.roro.islandwars.bonus.BonusType;
import fr.roro.islandwars.scoreboard.ScoreboardManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class GameLoop implements Runnable {

    private int bonusGiveTimerTemplate = 45;
    private int gearGiveTimerTemplate = 30;

    private int gearGiveTimer = gearGiveTimerTemplate;
    private int bonusGiveTimer = bonusGiveTimerTemplate;

    GameLoop() {
    }

    void reset() {
        this.bonusGiveTimerTemplate = GameManager.getInstance().getMode() == Mode.ULTRA ? 20 : 45;
        this.bonusGiveTimer = this.bonusGiveTimerTemplate;

        this.gearGiveTimerTemplate = GameManager.getInstance().getMode() == Mode.ULTRA ? 10 : 30;
        this.gearGiveTimer = this.gearGiveTimerTemplate;
    }

    @Override
    public void run() {
        int timeElapsed = GameManager.getInstance().increaseTimeElapsed();

        if (GameManager.getInstance().getMode() == Mode.ULTRA)
            this.bonusGiveTimerTemplate = timeElapsed < 60 * 5 ? 20 : timeElapsed < 60 * 10 ? 10 : 5;
        else
            this.bonusGiveTimerTemplate = timeElapsed < 60 * 5 ? 45 : timeElapsed < 60 * 10 ? 30 : 15;

        this.gearGiveTimer--;
        this.bonusGiveTimer--;

        if (this.gearGiveTimer == 0) {
            this.gearGiveTimer = this.gearGiveTimerTemplate;
            GameManager.getInstance().getPlayers().forEach(gamePlayer -> gamePlayer.getPlayer().getInventory().addItem(new ItemStack(Material.ARROW), new ItemStack(Material.WOOL, 1, gamePlayer.getTeam().getDyeColor().getWoolData())));
        }

        if (this.bonusGiveTimer == 0) {
            this.bonusGiveTimer = this.bonusGiveTimerTemplate;
            GameManager.getInstance().getPlayers().forEach(gamePlayer -> BonusType.values()[GameManager.getInstance().getRandom().nextInt(BonusType.values().length)].giveItem(gamePlayer.getPlayer()));
        }

        GameManager.getInstance().getAllPlayers().forEach(gamePlayer -> gamePlayer.sendActionBar("« §e§lProchain bonus dans §a§l" + this.bonusGiveTimer + "s §f»"));
        GameManager.getInstance().getAllPlayers().forEach(ScoreboardManager.getInstance().getGameScoreboard()::updateTimer);
    }

}
