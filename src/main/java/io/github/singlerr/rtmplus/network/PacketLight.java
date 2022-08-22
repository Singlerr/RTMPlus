package io.github.singlerr.rtmplus.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.*;
import net.minecraftforge.fml.common.network.ByteBufUtils;


@Getter
@Setter
public final class PacketLight extends RTMPacket{

    private String world;
    private int x;
    private int y;
    private int z;


    public PacketLight(){
        super();
    }
    public PacketLight(String world, int x, int y,int z){
        super("");
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void writeBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf,world);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    @Override
    public void readBytes(ByteBuf buf) {
        world = ByteBufUtils.readUTF8String(buf);
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }
}
