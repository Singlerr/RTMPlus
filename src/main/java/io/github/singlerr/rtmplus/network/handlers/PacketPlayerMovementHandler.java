package io.github.singlerr.rtmplus.network.handlers;

import io.github.singlerr.rtmplus.network.PacketPlayerMovement;
import io.github.singlerr.rtmplus.network.RTMPacketHandler;
import io.github.singlerr.rtmplus.registry.PassengerData;
import io.github.singlerr.rtmplus.registry.PassengerRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public final class PacketPlayerMovementHandler implements RTMPacketHandler<PacketPlayerMovement> {


    @Override
    public IMessage onMessage(PacketPlayerMovement message, MessageContext ctx) {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (PassengerRegistry.isPassenger(player.getName())) {
                PassengerData data = PassengerRegistry.getPassengerData(player.getName());
                data.setVectorOffset(data.getVectorOffset().add(message.getMoveForward(), 0, -message.getMoveStrafe()));
            }
        });
        return null;
    }
}
