package io.github.singlerr.rtmplus.network;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PacketSeatStateChange extends RTMPacket{

    private boolean seatLocked;

    public PacketSeatStateChange() {
    }
    public PacketSeatStateChange(String sender, boolean seatLocked){
        super(sender);
        this.seatLocked = seatLocked;
    }
    @Override
    public void readBytes(ByteBuf buf) {
        seatLocked = buf.readBoolean();
    }

    @Override
    public void writeBytes(ByteBuf buf) {
        buf.writeBoolean(seatLocked);
    }
}
