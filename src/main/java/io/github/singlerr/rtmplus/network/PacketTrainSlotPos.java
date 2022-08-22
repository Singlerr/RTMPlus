package io.github.singlerr.rtmplus.network;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.*;

@Getter
@Setter
public class PacketTrainSlotPos extends RTMPacket {

    private String trainName;
    private float[][] slotPos;

    public PacketTrainSlotPos() {
    }

    public PacketTrainSlotPos(String sender, String trainName, float[][] slotPos) {
        super(sender);
        this.trainName = trainName;
        this.slotPos = slotPos;
    }

    @Override
    public void readBytes(ByteBuf buf) {
        int length = buf.readInt();
        byte[] floatBytes = new byte[length];
        buf.readBytes(floatBytes);
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(floatBytes));
            slotPos = (float[][]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        trainName = ByteBufUtils.readUTF8String(buf);

    }

    @Override
    public void writeBytes(ByteBuf buf) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(slotPos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] bytes = bos.toByteArray();
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        ByteBufUtils.writeUTF8String(buf, trainName);
    }
}
