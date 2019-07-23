package fr.roro.islandwars.game.player;

import fr.roro.islandwars.game.GameManager;
import fr.roro.islandwars.game.player.statistic.Statistic;
import fr.roro.islandwars.game.player.statistic.StatisticType;
import fr.roro.islandwars.gui.item.ItemManager;
import fr.roro.islandwars.scoreboard.ScoreboardManager;
import fr.roro.islandwars.team.GameTeam;
import fr.roro.islandwars.util.ScoreboardSign;
import fr.roro.islandwars.util.item.ItemBuilder;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

/**
 * This file is a part of IslandWars project.
 *
 * @author roro1506_HD
 */
public class GamePlayer {

    private final CraftPlayer                   player;
    private final Map<StatisticType, Statistic> statistics;
    private final ScoreboardSign                scoreboard;
    private       boolean                       respawning;
    private       boolean                       spectator;
    private       boolean                       blazeRodEnabled;
    private       boolean                       emeraldEnabled;

    public GamePlayer(Player player, boolean spectator) {
        this.player = (CraftPlayer) player;
        this.scoreboard = new ScoreboardSign(this.player, "§e§lISLAND WARS");
        this.statistics = new HashMap<>();
        this.spectator = spectator;
    }

    public void initialize() {
        this.player.getInventory().clear();
        this.player.getInventory().setArmorContents(null);
        this.player.setMaxHealth(20.0D);
        this.player.setHealth(20.0D);
        this.player.setFoodLevel(20);
        this.player.setSaturation(20.0F);
        this.player.setExhaustion(20.0F);
        this.player.setWalkSpeed(0.2F);
        this.player.setLevel(0);
        this.player.setExp(0.0F);
        this.player.setAllowFlight(isSpectator());
        this.player.setFlying(isSpectator());
        this.player.setGameMode(this.spectator ? GameMode.SPECTATOR : GameMode.ADVENTURE);
        this.player.teleport(new Location(Bukkit.getWorlds().get(0), 0.5, 67, 0.5, 0, 0));
        this.player.getActivePotionEffects().stream()
                .map(PotionEffect::getType)
                .forEach(this.player::removePotionEffect);

        if (!this.spectator) {
            this.player.getInventory().setItem(0, ItemManager.getInstance().getTeamSelector());

            if (isGameManager())
                this.player.getInventory().setItem(4, ItemManager.getInstance().getOptionSelector());
        }
    }

    public void giveItems() {
        if (!hasTeam())
            return;

        this.player.getInventory().setHelmet(new ItemBuilder(Material.LEATHER_HELMET).setColor(getTeam().getDyeColor().getColor()).setUnbreakable(true).getItemStack());
        this.player.getInventory().setChestplate(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).setUnbreakable(true).getItemStack());
        this.player.getInventory().setLeggings(new ItemBuilder(Material.CHAINMAIL_LEGGINGS).setUnbreakable(true).getItemStack());
        this.player.getInventory().setBoots(new ItemBuilder(Material.CHAINMAIL_BOOTS).setUnbreakable(true).getItemStack());

        this.player.getInventory().setItem(0, new ItemBuilder(Material.STONE_SWORD).setUnbreakable(true).addEnchantment(Enchantment.FIRE_ASPECT, getTeam().hasFlintAndSteelEnabled() ? 1 : 0).getItemStack());
        this.player.getInventory().setItem(1, new ItemBuilder(Material.BOW).addEnchantment(Enchantment.ARROW_FIRE, getTeam().hasFlintAndSteelEnabled() ? 1 : 0).setUnbreakable(true).getItemStack());
        this.player.getInventory().setItem(2, new ItemBuilder(Material.SHEARS).addEnchantment(Enchantment.DIG_SPEED, 2).setUnbreakable(true).getItemStack());
        this.player.getInventory().setItem(8, new ItemStack(Material.WOOL, 4, getTeam().getDyeColor().getWoolData()));
        this.player.getInventory().setItem(9, new ItemStack(Material.ARROW, 8));
    }

    public void reset() {
        this.spectator = false;

        this.scoreboard.clearLines();
        ScoreboardManager.getInstance().getWaitingScoreboard().initScoreboard(this);

        if(hasBlazeRodEnabled())
            toggleBlazeRod();

        if(hasEmeraldEnabled())
            toggleEmerald();

        if(isRespawning())
            toggleRespawnStatus();

        this.statistics.clear();
        initialize();
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        PlayerConnection playerConnection = player.getHandle().playerConnection;

        playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut));
        playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle)));
        playerConnection.sendPacket(new PacketPlayOutTitle(EnumTitleAction.TITLE, new ChatComponentText(title)));
    }

    public void sendActionBar(String message) {
        player.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(new ChatComponentText(message), (byte) 2));
    }

    public void sendMessage(String message) {
        getPlayer().sendMessage("§3§lIslandWars §8» " + message);
    }

    public void toggleBlazeRod() {
        this.blazeRodEnabled = !this.blazeRodEnabled;
    }

    public void toggleEmerald() {
        this.emeraldEnabled = !this.emeraldEnabled;
    }

    public boolean hasBlazeRodEnabled() {
        return this.blazeRodEnabled;
    }

    public boolean hasEmeraldEnabled() {
        return this.emeraldEnabled;
    }

    public void toggleRespawnStatus() {
        this.respawning = !this.respawning;
    }

    public boolean isRespawning() {
        return this.respawning;
    }

    public GameTeam getTeam() {
        return GameManager.getInstance().getTeam(getPlayer());
    }

    public boolean hasTeam() {
        return !isSpectator() && getTeam() != null;
    }

    public boolean isSameTeam(GamePlayer target) {
        return hasTeam() && getTeam() != null && getTeam().hasPlayer(target.getPlayer());
    }

    public boolean isSpectator() {
        return spectator;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    public boolean isGameManager() {
        return getPlayer().getName().equals("roro1506_HD") || getPlayer().getName().equals("HeIIo") || getPlayer().getName().equals("Woctan");
    }

    public <T> Statistic<T> getStatistic(StatisticType<T, ? extends Statistic<T>> statisticType) {
        this.statistics.computeIfAbsent(statisticType, StatisticType::getDefaultStatistic);

        return (Statistic<T>) this.statistics.get(statisticType);
    }

    public ScoreboardSign getScoreboard() {
        return scoreboard;
    }

    public CraftPlayer getPlayer() {
        return player;
    }

}
