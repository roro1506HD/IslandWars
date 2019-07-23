package fr.roro.islandwars.gui.sign;

import fr.roro.islandwars.IslandWars;
import fr.roro.islandwars.util.protocol.TinyProtocol;
import io.netty.channel.Channel;
import net.minecraft.server.v1_8_R3.EnumChatFormat;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import org.bukkit.entity.Player;

/**
 * The MIT License (MIT)
 * Created on 07/06/2018.
 * Copyright (c) 2018 roro1506_HD
 */
public class SignPacketListener extends TinyProtocol {

    public SignPacketListener() {
        super(IslandWars.getInstance());
    }

    @Override
    public Object onPacketInAsync(Player player, Channel channel, Object packet) {
        if (packet.getClass().isAssignableFrom(PacketPlayInUpdateSign.class)) {
            if (!SignGui.signs.containsKey(player.getUniqueId()))
                return super.onPacketInAsync(player, channel, packet);
            SignGui gui = SignGui.signs.get(player.getUniqueId());
            IChatBaseComponent[] nmsLines = ((PacketPlayInUpdateSign) packet).b();
            String[] lines = new String[4];
            for (int i = 0; i < nmsLines.length; i++) {
                lines[i] = EnumChatFormat.a(nmsLines[i].c());
            }
            gui.onEdit(lines);
            gui.destroy();
            SignGui.signs.remove(player.getUniqueId());
            return null;
        }
        return super.onPacketInAsync(player, channel, packet);
    }
}
