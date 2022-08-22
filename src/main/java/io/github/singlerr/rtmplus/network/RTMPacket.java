package io.github.singlerr.rtmplus.network;

import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
public abstract class RTMPacket implements IMessage {
    private String sender;

    public RTMPacket(String sender) {
        this.sender = sender;
    }

    public abstract void writeBytes(ByteBuf buf);

    public abstract void readBytes(ByteBuf buf);

    @Override
    public void fromBytes(ByteBuf buf) {
        this.sender = ByteBufUtils.readUTF8String(buf);
        readBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, sender);
        writeBytes(buf);
    }
}
