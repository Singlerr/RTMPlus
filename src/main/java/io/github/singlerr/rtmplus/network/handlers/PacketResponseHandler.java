package io.github.singlerr.rtmplus.network.handlers;

import io.github.singlerr.rtmplus.RTMPlus;
import io.github.singlerr.rtmplus.network.PacketResponse;
import io.github.singlerr.rtmplus.network.RTMPacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class PacketResponseHandler implements RTMPacketHandler<PacketResponse> {

    @Override
    public IMessage onMessage(PacketResponse message, MessageContext ctx) {
        if(RTMPlus.INSTANCE.getRegisteredCallbacks().containsKey(message.getSender()))
            RTMPlus.INSTANCE.getRegisteredCallbacks().get(message.getSender()).onMessage(message,ctx);
        return null;
    }
}
