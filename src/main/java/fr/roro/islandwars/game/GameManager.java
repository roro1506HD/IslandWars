package fr.roro.islandwars.game;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.gui.GuiManager;
import fr.roro.islandwars.scoreboard.ScoreboardManager;
import fr.roro.islandwars.team.GameTeam;
import fr.roro.islandwars.util.FireworkUtil;
import fr.roro.islandwars.util.scanner.MapScanner;
import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class GameManager {

    private static GameManager instance;

    private final Map<UUID, GamePlayer>       playersByUuid = new HashMap<>();
    private final Map<String, List<Location>> gameLocations = new HashMap<>();
    private final List<File>                  availableMaps = new ArrayList<>();
    private final Random                      random;

    private GameLoop   gameLoop;
    private GameState  state;
    private Mode       mode;
    private int        selectedMap;
    private int        timeElapsed;
    private GameTeam   redTeam;
    private GameTeam   blueTeam;
    private GuiManager guiManager;

    public GameManager() {
        instance = this;

        this.random = new Random();
        this.gameLoop = new GameLoop();
        this.state = GameState.WAITING;
        this.mode = Mode.NORMAL;

        this.redTeam = new GameTeam("red", "Rouge", "Rouge", "§c[Rouge] ", "§c", DyeColor.RED);
        this.blueTeam = new GameTeam("blue", "Bleu", "Bleue", "§9[Bleu] ", "§9", DyeColor.BLUE);

        this.guiManager = new GuiManager();

        ScoreboardManager.getInstance().setupScoreboards();

        IslandWars.getInstance().getLogger().info("Scanning available maps...");

        for (File file : Objects.requireNonNull(IslandWars.getInstance().getDataFolder().listFiles())) {
            if (file.isDirectory())
                this.availableMaps.add(file);
        }

        IslandWars.getInstance().getLogger().info("Scanned " + this.availableMaps.size() + " available map" + (this.availableMaps.size() == 1 ? "" : "s"));

        if (this.availableMaps.isEmpty())
            Bukkit.shutdown();
    }

    private void scanSigns() {
        World world = Bukkit.getWorld("gameworld");
        List<String[]> detectedSigns = new MapScanner().scanSigns(world);

        IslandWars.getInstance().getLogger().info("Analyzing " + detectedSigns.size() + " signs...");

        for (String[] detectedSign : detectedSigns) {
            if (detectedSign[0].equals("\"LOCATION\"")) {
                String key = detectedSign[1].substring(1, detectedSign[1].length() - 1);

                List<Location> locations = this.gameLocations.computeIfAbsent(key, s -> new ArrayList<>());

                String[] parts = detectedSign[2].substring(1, detectedSign[2].length() - 1).split(",");
                Location location;

                if (parts.length > 4)
                    location = new Location(world, Double.valueOf(parts[0]), Double.valueOf(parts[1]), Double.valueOf(parts[2]), Float.valueOf(parts[3]), Float.valueOf(parts[4]));
                else
                    location = new Location(world, Double.valueOf(parts[0]), Double.valueOf(parts[1]), Double.valueOf(parts[2]));

                locations.add(location);
                IslandWars.getInstance().getLogger().info("Adding sign key : " + key + "(" + locations.size() + ")");

                String[] locationParts = detectedSign[4].split(":");
                world.getBlockAt(Integer.valueOf(locationParts[0]), Integer.valueOf(locationParts[1]), Integer.valueOf(locationParts[2])).setType(Material.AIR);
            }
            //IslandWars.getInstance().getLogger().info("Found sign at " + detectedSign[4] + " (" + detectedSign[0] + ")");
        }
    }

    public void startGame() {
        // Creating map
        File usedMap = this.availableMaps.get(this.selectedMap);

        broadcastMessage("§7Carte choisie : §e" + usedMap.getName() + "§7 !");
        broadcastMessage("§7Création du monde...");

        try {
            FileUtils.copyDirectory(usedMap, new File("gameworld"));
            new WorldCreator("gameworld").createWorld();
        } catch (Exception ex) {
            ex.printStackTrace();
            broadcastMessage("§cUne erreur est survenue.");
            return;
        }

        scanSigns();

        this.state = GameState.IN_GAME;
        this.gameLoop.reset();

        // Automatically fill teams
        List<GamePlayer> randomPlayers = getPlayers().stream()
                .filter(((Predicate<GamePlayer>) GamePlayer::hasTeam).negate())
                .collect(Collectors.toList());

        Collections.shuffle(randomPlayers);

        for (GamePlayer gamePlayer : randomPlayers) {
            List<GameTeam> availableTeams = Arrays.asList(this.blueTeam, this.redTeam);
            GameTeam lowestTeam = null;

            Collections.shuffle(availableTeams);

            for (GameTeam gameTeam : availableTeams) {
                if (lowestTeam == null || gameTeam.getMembers().size() < lowestTeam.getMembers().size())
                    lowestTeam = gameTeam;
            }

            if (lowestTeam == null)
                continue;

            lowestTeam.addPlayer(gamePlayer.getPlayer());
        }

        for (Player player : getPlayers().stream().map(GamePlayer::getPlayer).collect(Collectors.toList())) {
            GamePlayer gamePlayer = getPlayer(player);
            GameTeam team = getTeam(player);

            // Reset player
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().setArmorContents(null);
            player.getInventory().clear();
            player.setLevel(0);
            player.setExp(0.0F);
            player.getActivePotionEffects().stream()
                    .map(PotionEffect::getType)
                    .forEach(player::removePotionEffect);

            // Teleport player
            gamePlayer.sendMessage("§7Téléportation en cours...");
            player.teleport(team.getSpawn());

            // Give items
            gamePlayer.giveItems();

            // Switch scoreboard
            gamePlayer.getScoreboard().clearLines();
            ScoreboardManager.getInstance().getGameScoreboard().initScoreboard(gamePlayer);

            // Send title
            gamePlayer.sendTitle("§eIsland Wars", "§7Mode §" + (this.mode == Mode.ULTRA ? "3Ultra" : "aNormal"), 10, 20 * 4, 10);
        }

        Bukkit.getScheduler().runTaskTimer(IslandWars.getInstance(), this.gameLoop, 20, 20);

    }

    public void checkWin() {
        if (this.redTeam.getGameMembers().isEmpty()) {
            broadcastMessage("§eL'équipe §cRouge §en'a plus de joueurs.");
            broadcastMessage("§eVictoire de l'équipe §9Bleue §e!");
            finishGame();
        } else if (this.blueTeam.getGameMembers().isEmpty()) {
            broadcastMessage("§eL'équipe §9Bleue §en'a plus de joueurs.");
            broadcastMessage("§eVictoire de l'équipe §cRouge §e!");
            finishGame();
        } else if (this.redTeam.getScore() >= 75) {
            broadcastMessage("§eVictoire de l'équipe §cRouge §e!");
            finishGame();
        } else if (this.blueTeam.getScore() >= 75) {
            broadcastMessage("§eVictoire de l'équipe §9Bleue §e!");
            finishGame();
        }
    }

    private void finishGame() {
        this.state = GameState.FINISHED;
        Bukkit.getScheduler().cancelTasks(IslandWars.getInstance());

        for (GamePlayer gamePlayer : getPlayers()) {
            gamePlayer.getPlayer().setGameMode(GameMode.ADVENTURE);
            gamePlayer.getPlayer().setAllowFlight(true);
            gamePlayer.getPlayer().getInventory().clear();
            gamePlayer.getPlayer().getInventory().setArmorContents(null);

            if (gamePlayer.hasBlazeRodEnabled())
                gamePlayer.toggleBlazeRod();
        }

        new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                if (++timer >= 20) {
                    resetGame();
                    cancel();
                    return;
                }

                for (GamePlayer gamePlayer : getPlayers()) {
                    Firework firework = gamePlayer.getPlayer().getWorld().spawn(gamePlayer.getPlayer().getLocation(), Firework.class);
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();
                    fireworkMeta.setPower(random.nextInt(3) + 1);
                    fireworkMeta.addEffect(FireworkUtil.getRandomFirework());
                    firework.setFireworkMeta(fireworkMeta);
                }
            }
        }.runTaskTimer(IslandWars.getInstance(), 20, 20);
    }

    private void resetGame() {
        this.state = GameState.WAITING;
        this.timeElapsed = 0;
        this.gameLocations.clear();

        Bukkit.getOnlinePlayers().stream()
                .map(getInstance()::getPlayer)
                .forEach(GamePlayer::reset);

        getPlayers().forEach(ScoreboardManager.getInstance().getWaitingScoreboard()::updatePlayers);

        this.redTeam.reset();
        this.blueTeam.reset();

        Bukkit.unloadWorld("gameworld", false);

        try {
            FileUtils.deleteDirectory(new File("gameworld"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getTimeElapsed() {
        return this.timeElapsed;
    }

    int increaseTimeElapsed() {
        return ++this.timeElapsed;
    }

    public GamePlayer addPlayer(Player player, boolean spectator) {
        GamePlayer gamePlayer = this.playersByUuid.computeIfAbsent(player.getUniqueId(), uuid -> new GamePlayer(player, spectator));

        gamePlayer.getScoreboard().create();

        if (this.state == GameState.WAITING) {
            ScoreboardManager.getInstance().getWaitingScoreboard().initScoreboard(gamePlayer);
            getAllPlayers().forEach(ScoreboardManager.getInstance().getWaitingScoreboard()::updatePlayers);
        } else
            ScoreboardManager.getInstance().getGameScoreboard().initScoreboard(gamePlayer);

        return gamePlayer;
    }

    public boolean removePlayer(Player player) {
        if (!playersByUuid.containsKey(player.getUniqueId()))
            return false;

        GamePlayer gamePlayer = this.playersByUuid.remove(player.getUniqueId());

        if (this.state == GameState.WAITING)
            getAllPlayers().forEach(ScoreboardManager.getInstance().getWaitingScoreboard()::updatePlayers);

        if (gamePlayer.hasTeam())
            gamePlayer.getTeam().removePlayer(player);

        return this.state == GameState.IN_GAME && !gamePlayer.isSpectator();
    }

    public void broadcastMessage(String message) {
        this.playersByUuid.values().forEach(gamePlayer -> gamePlayer.sendMessage(message));
    }

    public GameTeam getRedTeam() {
        return redTeam;
    }

    public GameTeam getBlueTeam() {
        return blueTeam;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public GameTeam getTeam(Player player) {
        if (getRedTeam().hasPlayer(player))
            return getRedTeam();
        else if (getBlueTeam().hasPlayer(player))
            return getBlueTeam();
        else
            return null;
    }

    public Random getRandom() {
        return random;
    }

    public GameState getState() {
        return state;
    }

    public void switchMode() {
        this.mode = this.mode == Mode.NORMAL ? Mode.ULTRA : Mode.NORMAL;
    }

    public void switchMap() {
        if(++this.selectedMap > this.availableMaps.size() - 1)
            this.selectedMap = 0;
    }

    public int getSelectedMap() {
        return this.selectedMap;
    }

    public Mode getMode() {
        return mode;
    }

    public List<File> getAvailableMaps() {
        return new ArrayList<>(this.availableMaps);
    }

    public List<GamePlayer> getPlayers() {
        return this.playersByUuid.values().stream()
                .filter(((Predicate<GamePlayer>) GamePlayer::isSpectator).negate())
                .collect(Collectors.toList());
    }

    public List<GamePlayer> getAllPlayers() {
        return new ArrayList<>(this.playersByUuid.values());
    }

    public GamePlayer getPlayer(Player player) {
        return this.playersByUuid.get(player.getUniqueId());
    }

    public GamePlayer getPlayer(UUID uuid) {
        return this.playersByUuid.get(uuid);
    }

    public List<Location> getLocation(String key) {
        return this.gameLocations.get(key);
    }

    public static GameManager getInstance() {
        return instance;
    }
}
