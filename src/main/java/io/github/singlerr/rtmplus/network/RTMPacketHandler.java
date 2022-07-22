package io.github.singlerr.rtmplus.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public interface RTMPacketHandler<T extends RTMPacket> extends IMessageHandler<T, IMessage> {


}
