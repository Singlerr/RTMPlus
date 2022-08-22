package io.github.singlerr.rtmplus.network.handlers;

import io.github.singlerr.rtmplus.network.PacketLight;
import io.github.singlerr.rtmplus.network.RTMPacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class PacketLightHandler implements RTMPacketHandler<PacketLight> {
    /**
     * Called when a message is received of the appropriate type. You can optionally return a reply message, or null if no reply
     * is needed.
     *
     * @param message The message
     * @param ctx
     * @return an optional return message
     */
    @Override
    public IMessage onMessage(PacketLight message, MessageContext ctx) {
        //do nothing
        return null;
    }
}
