package fr.roro.islandwars.game.player.statistic;

/**
 * Copyright (c) 2015 - 2018 UHCFr. All rights reserved.
 * This file is a part of UHCFr project.
 *
 * @author Romaric "roro1506_HD" Gomez
 */
public class StatisticType<T, R extends Statistic<T>> {

    //@formatter:off
    public static StatisticType<Integer, StatisticInteger> KILLS      = new StatisticType<>(0, StatisticInteger.class);
    public static StatisticType<Integer, StatisticInteger> TEAM_KILLS = new StatisticType<>(0, StatisticInteger.class);
    public static StatisticType<Integer, StatisticInteger> DEATHS     = new StatisticType<>(0, StatisticInteger.class);
    //@formatter:on

    private final T        defaultValue;
    private final Class<R> statisticClass;

    private StatisticType(T defaultValue, Class<R> statisticClass) {
        this.defaultValue = defaultValue;
        this.statisticClass = statisticClass;
    }

    public R getDefaultStatistic() {
        try {
            return statisticClass.getDeclaredConstructor(this.defaultValue.getClass()).newInstance(this.defaultValue);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public T getDefaultValue() {
        return defaultValue;
    }

}
