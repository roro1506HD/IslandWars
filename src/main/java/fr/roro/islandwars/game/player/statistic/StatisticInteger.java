package fr.roro.islandwars.game.player.statistic;

/**
 * Copyright (c) 2015 - 2018 UHCFr. All rights reserved.
 * This file is a part of UHCFr project.
 *
 * @author Romaric "roro1506_HD" Gomez
 */
public class StatisticInteger extends Statistic<Integer> {

    StatisticInteger(Integer defaultValue) {
        super(defaultValue);
    }

    @Override
    public void incrementValue(Integer amount) {
        this.setValue(this.getValue() + amount);
    }

    @Override
    public void decrementValue(Integer amount) {
        this.incrementValue(-amount);
    }
}
