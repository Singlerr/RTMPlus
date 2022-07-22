package io.github.singlerr.rtmplus.network;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.common.network.ByteBufUtils;

@Setter
@Getter
public final class PacketResponse extends RTMPacket {
    private String response;
    private int responseCode;
    public PacketResponse(){
        super();
    }
    public PacketResponse(String sender, int responseCode, String response){
        super(sender);
        this.response = response;
        this.responseCode = responseCode;
    }
    @Override
    public void readBytes(ByteBuf buf) {
        response = ByteBufUtils.readUTF8String(buf);
        responseCode = buf.readInt();
    }

    @Override
    public void writeBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf,response);
        buf.writeInt(responseCode);
    }
}
