package io.github.singlerr.rtmplus.network.handlers;

import io.github.singlerr.rtmplus.RTMReflectionUtils;
import io.github.singlerr.rtmplus.network.PacketResponse;
import io.github.singlerr.rtmplus.network.PacketTrainSlotPos;
import io.github.singlerr.rtmplus.network.RTMPacketHandler;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.VehicleBaseConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetTrain;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public final class PacketTrainSlotPosHandler implements RTMPacketHandler<PacketTrainSlotPos> {
    public PacketTrainSlotPosHandler(){}
    @Override
    public IMessage onMessage(PacketTrainSlotPos message, MessageContext ctx) {
        VehicleBaseConfig config = ((ModelSetTrain) ModelPackManager.INSTANCE.getResourceSet(RTMResource.TRAIN_EC, message.getTrainName())).getConfig();
        try {
            RTMReflectionUtils.setData(config.getClass().getSuperclass(), config, "seatPosF", message.getSlotPos());
            return new PacketResponse(message.getSender(), 200, "Successfully initialized "+Minecraft.getMinecraft().player.getName() + "'s " + message.getTrainName());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return new PacketResponse(message.getSender(), 100,e.getMessage());
        }

    }
}
