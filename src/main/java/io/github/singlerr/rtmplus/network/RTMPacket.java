package io.github.singlerr.rtmplus.network;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@Getter
public abstract class RTMPacket implements IMessage {
    public RTMPacket() {
    }

    public abstract void writeBytes(ByteBuf buf);
    public abstract void readBytes(ByteBuf buf);

    private String sender;

    public RTMPacket(String sender){
        this.sender = sender;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.sender = ByteBufUtils.readUTF8String(buf);
        readBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf,sender);
        writeBytes(buf);
    }
}
