package io.github.singlerr.rtmplus.commands;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.singlerr.rtmplus.RTMPlus;
import io.github.singlerr.rtmplus.RTMReflectionUtils;
import io.github.singlerr.rtmplus.network.PacketResponse;
import io.github.singlerr.rtmplus.network.PacketSeatStateChange;
import io.github.singlerr.rtmplus.network.PacketTrainSlotPos;
import io.github.singlerr.rtmplus.network.async.PacketCallback;
import io.github.singlerr.rtmplus.registry.PassengerData;
import io.github.singlerr.rtmplus.registry.PassengerRegistry;
import io.github.singlerr.rtmplus.registry.TrainRegistry;
import jp.ngt.rtm.RTMResource;
import jp.ngt.rtm.entity.train.EntityTrainBase;
import jp.ngt.rtm.modelpack.ModelPackManager;
import jp.ngt.rtm.modelpack.cfg.VehicleBaseConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetTrain;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RTMPlusCommand extends CommandBase {

    private static double distance(double fX, double fY, double fZ, double tX, double tY, double tZ) {
        return Math.sqrt(Math.pow(fX - tX, 2) + Math.pow(fY - tY, 2) + Math.pow(fZ - tZ, 2));
    }

    @Override
    public String getName() {
        return "rtmplus";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/rtmplus";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("rtmp");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Entity entity = sender.getCommandSenderEntity();

        if (args.length > 0) {
            if (args.length > 1 && args[0].equalsIgnoreCase("register")) {
                List<EntityTrainBase> nearbyEntities = entity.world.getEntities(EntityTrainBase.class, e ->
                        distance(entity.posX, entity.posY, entity.posZ, e.posX, e.posY, e.posZ) < 10);
                if (nearbyEntities.isEmpty()) {
                    entity.sendMessage(new TextComponentString("주위 10칸 이내에 열차가 없습니다."));
                    return;
                }
                nearbyEntities.sort((a, b) -> distance(entity.posX, entity.posY, entity.posZ, a.posX, a.posY, a.posZ) < distance(entity.posX, entity.posY, entity.posZ, b.posX, b.posY, b.posZ) ? 1 : -1);

                EntityTrainBase nearest = nearbyEntities.get(0);

                TrainRegistry.registerTrain(args[1], nearest);

                entity.sendMessage(new TextComponentString("성공적으로 열차가 등록되었습니다."));
                return;
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (TrainRegistry.getTrains().isEmpty()) {
                    entity.sendMessage(new TextComponentString("등록된 열차 없음"));
                    return;
                }
                for (Map.Entry<String, EntityTrainBase> train : TrainRegistry.getTrains()) {
                    entity.sendMessage(new TextComponentString(String.format("<등록 이름> %s : <열차 위치> %s", train.getKey(), new Vec3d(train.getValue().posX, train.getValue().posY, train.getValue().posZ))));
                }
                return;
            }

            if (args[0].equalsIgnoreCase("lock")) {
                RTMPlus.INSTANCE.getRegisteredCallbacks().put(entity.getName(), (message, ctx) -> {
                    if (message.getResponseCode() == 200)
                        entity.sendMessage(new TextComponentString(message.getResponse()));
                });
                String[] targetPlayers = args[1].split(",");
                for (String targetPlayer : targetPlayers) {
                    EntityPlayerMP playerMP = server.getPlayerList().getPlayerByUsername(targetPlayer.replaceAll(" ", ""));
                    if (playerMP == null)
                        continue;
                    RTMPlus.INSTANCE.setSeatLocked(!RTMPlus.INSTANCE.isSeatLocked());
                    entity.sendMessage(new TextComponentString("Now seat lock status is " + RTMPlus.INSTANCE.isSeatLocked()));
                    RTMPlus.NETWORK_INSTANCE.sendTo(new PacketSeatStateChange(entity.getName(), RTMPlus.INSTANCE.isSeatLocked()), playerMP);
                }
            }
            if (args[0].equalsIgnoreCase("listen")) {
                RTMPlus.INSTANCE.getRegisteredCallbacks().remove(entity.getName());
                entity.sendMessage(new TextComponentString("Now not listening packets."));
            }
            if (args[0].equalsIgnoreCase("init")) {
                try {
                    HttpURLConnection con = (HttpURLConnection) new URL("https://raw.githubusercontent.com/Singlerr/ModStorage/main/slotPos.json").openConnection();
                    con.getResponseCode();
                    JsonParser parser = new JsonParser();
                    JsonElement element = parser.parse(new InputStreamReader(con.getInputStream()));

                    JsonObject obj = element.getAsJsonObject();
                    Gson gson = new Gson();


                    RTMPlus.INSTANCE.getRegisteredCallbacks().put(entity.getName(), (PacketCallback<PacketResponse>) (message, ctx) -> {
                        if (message.getResponseCode() == 200) {
                            entity.sendMessage(new TextComponentString(message.getResponse()));
                        } else {
                            entity.sendMessage(new TextComponentString(message.getResponse()));
                        }
                    });


                    for (Map.Entry<String, JsonElement> trainInfo : obj.entrySet()) {
                        PacketTrainSlotPos packet = new PacketTrainSlotPos(sender.getName(), trainInfo.getKey(), gson.fromJson(trainInfo.getValue().toString(), float[][].class));
                        VehicleBaseConfig config = ((ModelSetTrain) ModelPackManager.INSTANCE.getResourceSet(RTMResource.TRAIN_EC, packet.getTrainName())).getConfig();
                        RTMReflectionUtils.setData(config.getClass().getSuperclass(), config, "seatPosF", packet.getSlotPos());
                        RTMPlus.NETWORK_INSTANCE.sendToAll(packet);
                        entity.sendMessage(new TextComponentString("Replaced slot pos of " + trainInfo.getKey() + " to new slot pos."));
                    }
                } catch (Exception ex) {
                    entity.sendMessage(new TextComponentString(ex.getMessage()));
                }
                return;
            }
            if (args.length > 2 && args[0].equalsIgnoreCase("speed")) {
                String name = args[1];
                double speed = parseDouble(args[2]);
                if (!TrainRegistry.trainExists(name)) {
                    sender.sendMessage(new TextComponentString(String.format("%s 열차를 찾을 수 없습니다.", name)));
                    return;
                }
                TrainRegistry.getTrain(name).setSpeed((float) speed);
                sender.sendMessage(new TextComponentString(String.format("%s 열차의 속도를 %f로 변경했습니다.", name, speed)));
                return;
            }
            if (args[0].equalsIgnoreCase("start")) {
                if (PassengerRegistry.isPassenger(sender.getName())) {
                    PassengerRegistry.removePassenger(sender.getName());
                    return;
                }
                List<EntityTrainBase> nearbyEntities = entity.world.getEntities(EntityTrainBase.class, e ->
                        distance(entity.posX, entity.posY, entity.posZ, e.posX, e.posY, e.posZ) < 10);

                for (EntityTrainBase base : nearbyEntities) {
                    PassengerRegistry.registerPassenger(entity.getName(), new PassengerData(base, entity, base.rotationYaw, new Vec3d(entity.posX - base.posX, entity.posY - base.posY, entity.posZ - base.posZ)));
                    break;
                }
            }
        }

    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }
}
