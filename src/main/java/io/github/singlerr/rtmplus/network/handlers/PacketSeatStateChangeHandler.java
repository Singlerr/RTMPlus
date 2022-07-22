package io.github.singlerr.rtmplus.network.handlers;

import io.github.singlerr.rtmplus.RTMPlus;
import io.github.singlerr.rtmplus.network.PacketResponse;
import io.github.singlerr.rtmplus.network.PacketSeatStateChange;
import io.github.singlerr.rtmplus.network.RTMPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class PacketSeatStateChangeHandler implements RTMPacketHandler<PacketSeatStateChange> {
    public PacketSeatStateChangeHandler(){}
    @Override
    public IMessage onMessage(PacketSeatStateChange message, MessageContext ctx) {
        RTMPlus.INSTANCE.setSeatLocked(message.isSeatLocked());
        return new PacketResponse(message.getSender(), 200,"Successfully switched " + Minecraft.getMinecraft().player.getName()   +"'s seat lock status to " + RTMPlus.INSTANCE.isSeatLocked());
    }
}
