package fr.roro.islandwars.game.player.statistic;

/**
 * Copyright (c) 2015 - 2018 UHCFr. All rights reserved.
 * This file is a part of UHCFr project.
 *
 * @author Romaric "roro1506_HD" Gomez
 */
public abstract class Statistic<T> {

    private T value;

    Statistic(T defaultValue) {
        this.value = defaultValue;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public abstract void incrementValue(T amount);

    public abstract void decrementValue(T amount);

}
