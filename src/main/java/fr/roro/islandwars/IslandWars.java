package fr.roro.islandwars;

import fr.roro.islandwars.command.GiveCommand;
import fr.roro.islandwars.command.StartCommand;
import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.listener.DeathListener;
import fr.roro.islandwars.listener.EntityListener;
import fr.roro.islandwars.listener.PlayerListener;
import fr.roro.islandwars.listener.WorldListener;
import fr.roro.islandwars.protocol.SteerVehiclePacketListener;
import fr.roro.islandwars.util.ScoreboardSign;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class IslandWars extends JavaPlugin {

    private static IslandWars                 instance;
    private        SteerVehiclePacketListener steerVehiclePacketListener;

    @Override
    public void onDisable() {
        GameManager.getInstance().getAllPlayers().stream().map(GamePlayer::getScoreboard).forEach(ScoreboardSign::destroy);
        this.steerVehiclePacketListener.close();
    }

    @Override
    public void onEnable() {
        instance = this;

        // Check good spigot
        try {
            Class.forName("org.bukkit.event.entity.ProjectileHitBlockEvent");
            Class.forName("org.bukkit.event.entity.ProjectileHitEntityEvent");
            Class.forName("org.bukkit.event.entity.ProjectileUpdateLogicEvent");
            Class.forName("org.bukkit.event.entity.EntityTNTFuseEvent");
        } catch (Exception ex) {
            getLogger().info("Ce serveur n'utilise pas la bonne version de spigot. (1.8.8-custom)");
            setEnabled(false);
            return;
        }

        // Check and/or generate plugin folder
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();

        // Start Game Instance
        new GameManager();

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldListener(), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);

        // Register packets
        this.steerVehiclePacketListener = new SteerVehiclePacketListener();

        // Register commands
        ((CraftServer) Bukkit.getServer()).getCommandMap().register(getDescription().getName(), new StartCommand());
        ((CraftServer) Bukkit.getServer()).getCommandMap().register(getDescription().getName(), new GiveCommand());

        // Add online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            GamePlayer gamePlayer = GameManager.getInstance().addPlayer(player, false);
            gamePlayer.initialize();
            gamePlayer.sendMessage("§eVous avez automatiquement été ajouté dans la liste des joueurs.");
        }

        if (Bukkit.getWorld("gameworld") != null)
            Bukkit.unloadWorld("gameworld", false);

        try {
            FileUtils.deleteDirectory(new File("gameworld"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static IslandWars getInstance() {
        return instance;
    }
}
