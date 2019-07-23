package fr.roro.islandwars.gui.sign;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * The MIT License (MIT)
 * Created on 07/06/2018.
 * Copyright (c) 2018 roro1506_HD
 */
public abstract class SignGui {

    static final Map<UUID, SignGui> signs = Maps.newHashMap();

    private final Player   player;
    private final Location location;
    private final String[] defaultLines;

    public SignGui(Player player, String[] defaultLines) {
        if (signs.containsKey(player.getUniqueId()))
            throw new IllegalStateException("Player already has an opened sign");
        this.player = player;
        this.location = player.getLocation().add(0, 30, 0);
        if (this.location.getY() > 250)
            this.location.setY(250);
        this.defaultLines = defaultLines;
        signs.put(player.getUniqueId(), this);
        open();
    }

    public abstract void onEdit(String[] lines);

    void destroy() {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition blockPosition = new BlockPosition(location.getX(), location.getY(), location.getZ());
        // Set normal block
        PacketPlayOutBlockChange blockChangePacket = new PacketPlayOutBlockChange(world, blockPosition);
        sendPacket(blockChangePacket);
    }

    private void open() {
        World world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition blockPosition = new BlockPosition(location.getX(), location.getY(), location.getZ());
        // Set sign block
        PacketPlayOutBlockChange blockChangePacket = new PacketPlayOutBlockChange(world, blockPosition);
        blockChangePacket.block = Blocks.STANDING_SIGN.getBlockData();
        sendPacket(blockChangePacket);
        // Update sign block
        PacketPlayOutUpdateSign updateSignPacket = new PacketPlayOutUpdateSign(world, blockPosition, CraftSign.sanitizeLines(this.defaultLines));
        sendPacket(updateSignPacket);
        // Open sign
        PacketPlayOutOpenSignEditor openSignEditorPacket = new PacketPlayOutOpenSignEditor(blockPosition);
        sendPacket(openSignEditorPacket);
    }

    private void sendPacket(Packet packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

}
