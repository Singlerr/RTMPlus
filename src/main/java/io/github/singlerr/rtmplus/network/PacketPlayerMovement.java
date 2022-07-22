package io.github.singlerr.rtmplus.network;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public final class PacketPlayerMovement extends RTMPacket {
    private double moveStrafe;

    private double moveForward;

    public PacketPlayerMovement() {
        super();
    }
    public PacketPlayerMovement(String sender, double moveStrafe, double moveForward){
        super(sender);
        this.moveStrafe = moveStrafe;
        this.moveForward = moveForward;
    }

    @Override
    public void readBytes(ByteBuf buf) {
        moveStrafe = buf.readDouble();
        moveForward = buf.readDouble();
    }

    @Override
    public void writeBytes(ByteBuf buf) {
        buf.writeDouble(moveStrafe);
        buf.writeDouble(moveForward);
    }
}
