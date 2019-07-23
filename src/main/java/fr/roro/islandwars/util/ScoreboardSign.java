package fr.roro.islandwars.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ScoreboardSign {

    private final VirtualTeam[] lines   = new VirtualTeam[15];
    private final Player        player;
    private       boolean       created = false;
    private       String        objectiveName;

    /**
     * Create a scoreboard sign for a given player and using a specifig objective name
     *
     * @param player the player viewing the scoreboard sign
     * @param objectiveName the name of the scoreboard sign (displayed at the top of the scoreboard)
     */
    public ScoreboardSign(Player player, String objectiveName) {
        this.player = player;
        this.objectiveName = objectiveName;
    }

    /**
     * Send the initial creation packets for this scoreboard sign. Must be called at least once.
     */
    public void create() {
        if (this.created)
            return;

        PlayerConnection player = getPlayer();
        player.sendPacket(createObjectivePacket(0, this.objectiveName));
        player.sendPacket(setObjectiveSlot());
        int i = 0;
        while (i < this.lines.length)
            sendLine(i++);

        this.created = true;
    }

    /**
     * Send the packets to remove this scoreboard sign. A destroyed scoreboard sign must be recreated using {@link
     * ScoreboardSign#create()} in order
     * to be used again
     */
    public void destroy() {
        if (!this.created)
            return;

        getPlayer().sendPacket(createObjectivePacket(1, null));
        for (VirtualTeam team : this.lines)
            if (team != null)
                getPlayer().sendPacket(team.removeTeam());

        this.created = false;
    }

    /**
     * Change the name of the objective. The name is displayed at the top of the scoreboard.
     *
     * @param name the name of the objective, max 32 char
     */
    public void setObjectiveName(String name) {
        this.objectiveName = name;
        if (this.created)
            getPlayer().sendPacket(createObjectivePacket(2, name));
    }

    /**
     * Change a scoreboard line and send the packets to the player. Can be called async.
     *
     * @param line the number of the line (0 <= line < 15)
     * @param value the new value for the scoreboard line
     */
    public void setLine(int line, String value) {
        VirtualTeam team = getOrCreateTeam(line);
        //String old = team.getCurrentPlayer();

        //if (old != null && created)
        //getPlayer().sendPacket(removeLine(old));

        team.setValue(value);
        sendLine(line);
    }

    public void clearLines() {
        if(!this.created)
            return;

        for (int i = 0; i < 15; i++)
            if (hasLine(i))
                removeLine(i);
    }

    /**
     * Get if this line exists
     * @param line the line to check
     * @return if the line exists
     */
    public boolean hasLine(int line) {
        if (line > 14 || line < 0)
            return false;

        return this.lines[line] != null;
    }

    /**
     * Remove a given scoreboard line
     *
     * @param line the line to remove
     */
    public void removeLine(int line) {
        VirtualTeam team = getOrCreateTeam(line);
        String old = team.getCurrentPlayer();

        if (old != null && this.created) {
            getPlayer().sendPacket(removeLine(old));
            getPlayer().sendPacket(team.removeTeam());
        }

        this.lines[line] = null;
    }

    /**
     * Get the current value for a line
     *
     * @param line the line
     * @return the content of the line
     */
    public String getLine(int line) {
        if (line > 14)
            return null;
        if (line < 0)
            return null;
        return getOrCreateTeam(line).getValue();
    }

    /**
     * Get the team assigned to a line
     *
     * @return the {@link VirtualTeam} used to display this line
     */
    public VirtualTeam getTeam(int line) {
        if (line > 14)
            return null;
        if (line < 0)
            return null;
        return getOrCreateTeam(line);
    }

    private PlayerConnection getPlayer() {
        return ((CraftPlayer) this.player).getHandle().playerConnection;
    }

    private void sendLine(int line) {
        if (line > 14)
            return;
        if (line < 0)
            return;
        if (!this.created)
            return;

        VirtualTeam val = getOrCreateTeam(line);
        for (Packet packet : val.sendLine())
            getPlayer().sendPacket(packet);
        getPlayer().sendPacket(sendScore(val.getCurrentPlayer(), line));
        val.reset();
    }

    private VirtualTeam getOrCreateTeam(int line) {
        if (this.lines[line] == null)
            this.lines[line] = new VirtualTeam("__fakeScore" + line, line);

        return this.lines[line];
    }

    /*
        Factories
         */
    private PacketPlayOutScoreboardObjective createObjectivePacket(int mode, String displayName) {
        PacketPlayOutScoreboardObjective packet = new PacketPlayOutScoreboardObjective();
        // Nom de l'objectif
        setField(packet, "a", this.player.getName());

        // Mode
        // 0 : créer
        // 1 : Supprimer
        // 2 : Mettre à jour
        setField(packet, "d", mode);

        if (mode == 0 || mode == 2) {
            setField(packet, "b", displayName);
            setField(packet, "c", IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER);
        }

        return packet;
    }

    private PacketPlayOutScoreboardDisplayObjective setObjectiveSlot() {
        PacketPlayOutScoreboardDisplayObjective packet = new PacketPlayOutScoreboardDisplayObjective();
        // Slot
        setField(packet, "a", 1);
        setField(packet, "b", player.getName());

        return packet;
    }

    private PacketPlayOutScoreboardScore sendScore(String line, int score) {
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore(line);
        setField(packet, "b", this.player.getName());
        setField(packet, "c", 15 - score);
        setField(packet, "d", PacketPlayOutScoreboardScore.EnumScoreboardAction.CHANGE);

        return packet;
    }

    private PacketPlayOutScoreboardScore removeLine(String line) {
        return new PacketPlayOutScoreboardScore(line);
    }

    private static void setField(Object edit, String fieldName, Object value) {
        try {
            Field field = edit.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(edit, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    enum Colors {
        A(''),
        B(''),
        C(''),
        D(''),
        E(''),
        F(''),
        G(''),
        H(''),
        I(''),
        J(''),
        K(''),
        L(''),
        M(''),
        N(''),
        O('');

        char cha;

        Colors(char cha) {
            this.cha = cha;
        }

        @Override
        public String toString() {
            return this.cha + "";
        }
    }

    /**
     * This class is used to manage the content of a line. Advanced users can use it as they want, but they are
     * encouraged to read and understand the
     * code before doing so. Use these methods at your own risk.
     */
    public class VirtualTeam {

        private final String name;
        private       String prefix;
        private       String suffix;
        private       String currentPlayer;

        private boolean prefixChanged, suffixChanged = false;
        private boolean first = true;

        private int line;

        private VirtualTeam(String name, String prefix, String suffix, int line) {
            this.name = name;
            this.prefix = prefix;
            this.suffix = suffix;
            this.line = line;
            this.currentPlayer = Colors.values()[line].toString();
        }

        private VirtualTeam(String name, int line) {
            this(name, "", "", line);
        }

        public String getName() {
            return this.name;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public void setPrefix(String prefix) {
            if (this.prefix == null || !this.prefix.equals(prefix))
                this.prefixChanged = true;
            this.prefix = prefix;
        }

        public int getLine() {
            return this.line;
        }

        public String getSuffix() {
            return this.suffix;
        }

        public void setSuffix(String suffix) {
            if (this.suffix == null || !this.suffix.equals(this.prefix))
                this.suffixChanged = true;
            this.suffix = suffix;
        }

        private PacketPlayOutScoreboardTeam createPacket(int mode) {
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            setField(packet, "a", this.name);
            setField(packet, "h", mode);
            setField(packet, "b", "");
            setField(packet, "c", this.prefix);
            setField(packet, "d", this.suffix);
            setField(packet, "i", 0);
            setField(packet, "e", "always");
            setField(packet, "f", 0);

            return packet;
        }

        private PacketPlayOutScoreboardTeam createTeam() {
            return createPacket(0);
        }

        private PacketPlayOutScoreboardTeam updateTeam() {
            return createPacket(2);
        }

        private PacketPlayOutScoreboardTeam removeTeam() {
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            setField(packet, "a", this.name);
            setField(packet, "h", 1);
            this.first = true;
            return packet;
        }

        private Iterable<PacketPlayOutScoreboardTeam> sendLine() {
            List<PacketPlayOutScoreboardTeam> packets = new ArrayList<>();

            if (this.first) {
                packets.add(createTeam());
            } else if (this.prefixChanged || this.suffixChanged) {
                packets.add(updateTeam());
            }

            if (this.first) {
                packets.add(changePlayer());
            }

            if (this.first)
                this.first = false;

            return packets;
        }

        public void reset() {
            this.prefixChanged = false;
            this.suffixChanged = false;
        }

        private PacketPlayOutScoreboardTeam changePlayer() {
            return addOrRemovePlayer(3, this.currentPlayer);
        }

        private PacketPlayOutScoreboardTeam addOrRemovePlayer(int mode, String playerName) {
            PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
            setField(packet, "a", this.name);
            setField(packet, "h", mode);

            try {
                Field f = packet.getClass().getDeclaredField("g");
                f.setAccessible(true);
                ((List<String>) f.get(packet)).add(playerName);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            return packet;
        }

        public String getCurrentPlayer() {
            return this.currentPlayer;
        }

        public String getValue() {
            return getPrefix() + getCurrentPlayer() + getSuffix();
        }

        public void setValue(String value) {
            if (value.length() <= 16) {
                setPrefix(value);
                setSuffix("");
            } else if (value.length() <= 32) {
                String first = value.substring(0, 16);
                String second = value.substring(16);
                if (first.endsWith("§")) {
                    first = first.substring(0, 15);
                    second = "§" + second;
                }
                if (second.length() > 16) {
                    second = second.substring(16, 32);
                }
                setPrefix(first);
                setSuffix(second);
            } else {
                throw new IllegalArgumentException(
                        "Too long value ! Max 32 characters, value was " + value.length() + " !");
            }
        }
    }

}
