package io.github.singlerr.rtmplus.network.async;

import io.github.singlerr.rtmplus.network.PacketResponse;
import io.github.singlerr.rtmplus.network.RTMPacket;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface PacketCallback<T extends RTMPacket> {
    void onMessage(PacketResponse message, MessageContext ctx);
}
