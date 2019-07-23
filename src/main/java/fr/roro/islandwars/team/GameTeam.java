package fr.roro.islandwars.team;

import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.player.GamePlayer;
import fr.roro.islandwars.game.player.statistic.StatisticType;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class GameTeam {

    private final String     id;
    private final String     name;
    private final String     femaleName;
    private final String     prefix;
    private final String     color;
    private final DyeColor   dyeColor;
    private final List<UUID> members;
    private       int        score;
    private       Team       scoreboardTeam;
    private       boolean    enderEyeEnabled;
    private       boolean    flintAndSteelEnabled;

    public GameTeam(String id, String name, String femaleName, String prefix, String color, DyeColor dyeColor) {
        this.id = id;
        this.name = name;
        this.femaleName = femaleName;
        this.prefix = prefix;
        this.color = color;
        this.dyeColor = dyeColor;
        this.members = new ArrayList<>();

        this.scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(name);

        if (this.scoreboardTeam == null)
            this.scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(name);

        new ArrayList<>(this.scoreboardTeam.getEntries()).forEach(this.scoreboardTeam::removeEntry);

        this.scoreboardTeam.setPrefix(prefix);
        this.scoreboardTeam.setAllowFriendlyFire(true);
    }

    public void reset() {
        this.scoreboardTeam.getEntries().forEach(this.scoreboardTeam::removeEntry);
        this.members.clear();
        this.score = 0;
    }

    public void broadcastMessage(String message) {
        getGameMembers().forEach(gamePlayer -> gamePlayer.sendMessage(message));
    }

    public void toggleEnderEye() {
        this.enderEyeEnabled = !this.enderEyeEnabled;
    }

    public void toggleFlintAndSteel() {
        this.flintAndSteelEnabled = !this.flintAndSteelEnabled;
    }

    public boolean hasEnderEyeEnabled() {
        return this.enderEyeEnabled;
    }

    public boolean hasFlintAndSteelEnabled() {
        return this.flintAndSteelEnabled;
    }

    public String getName() {
        return name;
    }

    public String getFemaleName() {
        return femaleName;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getColor() {
        return color;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    public Team getScoreboardTeam() {
        return scoreboardTeam;
    }

    public ItemStack getBannerItem(Player player) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.BANNER)
                .setName(this.color + "Equipe " + getFemaleName())
                .addLore("§7Membres (" + getGameMembers().size() + ") :")
                .addLore("")
                .setAmount(getMembers().size())
                .setBaseColor(this.dyeColor);

        if (getGameMembers().isEmpty())
            itemBuilder.addLore("§c▪ Personne");

        getGameMembers().forEach(gamePlayer -> itemBuilder.addLore("§7▪ " + getColor() + (gamePlayer.getPlayer().equals(player) ? "§l" : "") + gamePlayer.getPlayer().getName()));

        itemBuilder.addLore("");

        if (hasPlayer(player))
            itemBuilder.addLore("§c» Vous êtes déjà dans cette équipe");
        else
            itemBuilder.addLore("§e» Cliquez pour rejoindre cette équipe");

        return itemBuilder.getItemStack();
    }

    public void addPlayer(Player player) {
        if (this.members.contains(player.getUniqueId()))
            return;

        this.members.add(player.getUniqueId());
        this.scoreboardTeam.addEntry(player.getName());
    }

    public boolean hasPlayer(Player player) {
        return this.members.contains(player.getUniqueId());
    }

    public void removePlayer(Player player) {
        if (!this.members.contains(player.getUniqueId()))
            return;

        this.members.remove(player.getUniqueId());
        this.scoreboardTeam.removeEntry(player.getName());
    }

    public String getId() {
        return this.id;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public List<GamePlayer> getGameMembers() {
        return this.members.stream()
                .map(GameManager.getInstance()::getPlayer)
                .collect(Collectors.toList());
    }

    public void incrementScore() {
        this.score++;
    }

    public int getScore() {
        return this.score;
    }

    public int getTeamKills() {
        return this.members.stream()
                .map(GameManager.getInstance()::getPlayer)
                .mapToInt(gamePlayer -> gamePlayer.getStatistic(StatisticType.TEAM_KILLS).getValue())
                .sum();
    }

    public int getKills() {
        return this.members.stream()
                .map(GameManager.getInstance()::getPlayer)
                .mapToInt(gamePlayer -> gamePlayer.getStatistic(StatisticType.KILLS).getValue())
                .sum();
    }

    public Location getSpawn() {
        List<Location> availableSpawns = GameManager.getInstance().getLocation(getId().toLowerCase() + "Spawn");
        return availableSpawns.get(GameManager.getInstance().getRandom().nextInt(availableSpawns.size()));
    }

}
