package io.github.singlerr.rtmplus;

import io.github.singlerr.rtmplus.commands.RTMPlusCommand;
import io.github.singlerr.rtmplus.network.*;
import io.github.singlerr.rtmplus.network.async.PacketCallback;
import io.github.singlerr.rtmplus.network.handlers.*;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.entity.train.parts.EntityFloor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

@Mod(
        modid = RTMPlus.MOD_ID,
        name = RTMPlus.MOD_NAME,
        version = RTMPlus.VERSION,
        dependencies = "required-after:rtm"
)
public class RTMPlus {
    public static final String MOD_ID = "rtmplus";
    public static final String MOD_NAME = "RTMPlus";
    public static final String VERSION = "1.0-SNAPSHOT";
    private static final String CHANNEL_NAME = "rtmpch";
    public static final SimpleNetworkWrapper NETWORK_INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(RTMPlus.CHANNEL_NAME);
    @Mod.Instance(MOD_ID)
    public static RTMPlus INSTANCE;
    private final HashMap<String, Vec3d> locHistory = new HashMap<>();
    @Getter
    private final HashMap<String, PacketCallback<? extends RTMPacket>> registeredCallbacks = new HashMap<>();
    @Getter
    @Setter
    private boolean seatLocked = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        NETWORK_INSTANCE.registerMessage(PacketPlayerMovementHandler.class, PacketPlayerMovement.class, 0, Side.SERVER);
        NETWORK_INSTANCE.registerMessage(PacketSeatStateChangeHandler.class, PacketSeatStateChange.class, 1, Side.SERVER);
        NETWORK_INSTANCE.registerMessage(PacketTrainSlotPosHandler.class, PacketTrainSlotPos.class, 2, Side.SERVER);
        NETWORK_INSTANCE.registerMessage(PacketTrainSlotPosHandler.class, PacketTrainSlotPos.class, 3, Side.CLIENT);
        NETWORK_INSTANCE.registerMessage(PacketResponseHandler.class, PacketResponse.class, 4, Side.SERVER);
        NETWORK_INSTANCE.registerMessage(PacketSeatStateChangeHandler.class, PacketSeatStateChange.class, 5, Side.CLIENT);
        NETWORK_INSTANCE.registerMessage(PacketLightHandler.class, PacketLight.class, 6, Side.CLIENT);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public void start(FMLServerStartingEvent event) {
        event.registerServerCommand(new RTMPlusCommand());
        MinecraftForge.EVENT_BUS.register(new PassengerEventHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Mod.EventBusSubscriber
    public static class PassengerEventHandler {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onDismount(EntityMountEvent e) {
            if (!(e.getEntityBeingMounted() instanceof EntityFloor))
                return;
            if (e.isMounting()) {
                RTMPlus.INSTANCE.locHistory.put(e.getEntityMounting().getName(), new Vec3d(e.getEntityBeingMounted().posX - e.getEntityMounting().posX, e.getEntityBeingMounted().posY - e.getEntityMounting().posY, e.getEntityBeingMounted().posZ - e.getEntityMounting().posZ));
                // e.getEntityMounting().sendMessage(new TextComponentString( new Vec3d(e.getEntityMounting().posX, e.getEntityMounting().posY, e.getEntityMounting().posZ).toString()));
            } else {
                if (RTMPlus.INSTANCE.isSeatLocked()) {
                    e.setCanceled(true);
                    return;
                }
                if (RTMPlus.INSTANCE.locHistory.containsKey(e.getEntityMounting().getName())) {
                    Vec3d loc = RTMPlus.INSTANCE.locHistory.get(e.getEntityMounting().getName());
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                sleep(300);
                                e.getEntityMounting().setPositionAndUpdate(e.getEntityMounting().posX + loc.x, e.getEntityMounting().posY + loc.y, e.getEntityMounting().posZ + loc.z);
                                interrupt();
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }.start();
                    RTMPlus.INSTANCE.locHistory.remove(e.getEntityMounting().getName());
                }
            }
        }

        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void onTick(TickEvent.ClientTickEvent event) {
            if (Minecraft.getMinecraft().player == null || Minecraft.getMinecraft().player.world == null)
                return;

            /*
            for (Entity entity : Minecraft.getMinecraft().player.world.loadedEntityList) {
                if (entity instanceof EntityTrainBase) {
                    EntityTrainBase train = (EntityTrainBase) entity;
                    for (VehicleBaseConfig.Light light : train.getResourceState().getResourceSet().getConfig().interiorLights) {
                        int x = (int) (train.posX + light.pos[0]);
                        int y = (int) (train.posY + light.pos[1]);
                        int z = (int) (train.posZ + light.pos[2]);

                        BlockPos pos = new BlockPos(x,y,z);
                        entity.world.getBlockState(pos).getBlock().setLightLevel(0.6F);
                    }
                }
            }

             */

            /*
            if (!PassengerRegistry.anyPassengerExists())
                return;
            for (Map.Entry<String, PassengerData> data : PassengerRegistry.getAllPassengers()) {
                PassengerData passengerData = data.getValue();

                EntityTrainBase base = passengerData.getTrain();

                Entity player = passengerData.getPassengerEntity();


                Vec3d vectorOffset = passengerData.getVectorOffset();

                if (player instanceof EntityPlayerMP) {
                    passengerData.setVectorOffset(vectorOffset.add(((EntityPlayerMP) player).moveStrafing, 0, ((EntityPlayerMP) player).moveForward));
                }

                double angle = Math.toRadians(-base.rotationYaw + passengerData.getRotationOffset());
                double newX = vectorOffset.x * Math.cos(angle) - vectorOffset.z * Math.sin(angle);
                double newZ = vectorOffset.z * Math.cos(angle) + vectorOffset.x * Math.sin(angle);

                Vec3d velocity = new Vec3d(base.posX + newX - player.posX, 0, base.posZ + newZ - player.posZ).scale(0.5);
                player.setVelocity(velocity.x, velocity.y, velocity.z);
                player.velocityChanged = true;
            }
             */
        }

        private Vec3d getMovementInput(EntityPlayer player) {
            return new Vec3d(player.moveStrafing, player.moveVertical, player.moveForward).scale(player.isSprinting() ? 0.4 : 0.2);
        }

        private Vec3d movePlayer(EntityPlayer player, EntityTrainBase train, Vec3d offset) {

            Vec3d movement = getMovementInput(player);
            player.sendMessage(new TextComponentString(movement.toString()));
            if (movement.length() < 0.1) {
                return offset;
            }

            movement = new Vec3d(movement.x, 0, movement.z);//.rotateYaw(   train.rotationYaw - player.getRotationYawHead());
            offset = offset.add(movement);

        /*
        if (this instanceof EntityCoupleableRollingStock) {
            EntityCoupleableRollingStock couplable = (EntityCoupleableRollingStock) this;

            boolean atFront = this.getDefinition().isAtFront(gauge, offset);
            boolean atBack = this.getDefinition().isAtRear(gauge, offset);
            // TODO config for strict doors
            boolean atDoor = isNearestDoorOpen(source);

            atFront &= atDoor;
            atBack &= atDoor;

            for (CouplerType coupler : CouplerType.values()) {
                boolean atCoupler = coupler == CouplerType.FRONT ? atFront : atBack;
                if (atCoupler && couplable.isCoupled(coupler)) {
                    EntityCoupleableRollingStock coupled = ((EntityCoupleableRollingStock) this).getCoupled(coupler);
                    if (coupled != null) {
                        if (((EntityRidableRollingStock)coupled).isNearestDoorOpen(source)) {
                            coupled.addPassenger(source);
                        }
                    } else if (this.getTickCount() > 20) {
                        ImmersiveRailroading.info(
                                "Tried to move between cars (%s, %s), but %s was not found",
                                this.getUUID(),
                                couplable.getCoupledUUID(coupler),
                                couplable.getCoupledUUID(coupler)
                        );
                        ((EntityCoupleableRollingStock) this).decouple(coupler);
                    }
                    return offset;
                }
            }
        }

         */
            //   offset = offset.add(0, Math.sin(Math.toRadians(player.rotationPitch)) * offset.z, 0);
            return offset;
        }
    /* EXAMPLE ITEM AND BLOCK - you probably want these in separate files
    public static class MySpecialItem extends Item {

    }

    public static class MySpecialBlock extends Block {

    }
    */
    }
}
