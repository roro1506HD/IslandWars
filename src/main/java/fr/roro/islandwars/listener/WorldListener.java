package fr.roro.islandwars.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class WorldListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent event) {
        if (event.getNewState().getType().equals(Material.STATIONARY_WATER) || event.getNewState().getType().equals(Material.WATER))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.getBlock().getType().name().contains("WATER"))
            event.setCancelled(true);
    }

}
