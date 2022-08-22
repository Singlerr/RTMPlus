package io.github.singlerr.rtmplus.bridge;

import com.jcraft.jogg.Packet;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LightUtil {
    private static void relight(BlockPos pos, World world){
        world.checkLightFor(EnumSkyBlock.BLOCK, pos);
        world.checkLightFor(EnumSkyBlock.SKY, pos);
    }
    private static void setLight(BlockPos pos, World world){
        world.setLightFor(EnumSkyBlock.BLOCK, pos, 15);
        world.setLightFor(EnumSkyBlock.SKY, pos, 14);
    }
    public static void createLight(BlockPos pos, World world){
        setLight(pos,world);
        relight(pos,world);
        updateChunk(pos, world);
    }
    private static void updateChunk(BlockPos pos, World world){
        ArrayList<Chunk> chunks = new ArrayList<>();
        chunks.add(world.getChunk(pos));

        for(BlockFace direction : BlockFace.values())
            chunks.add(world.getChunk(getCardinalDistance(pos,direction, 15)));

        for(Chunk chunk : chunks){
            chunk.generateSkylightMap();
            chunk.setModified(true);
            SPacketChunkData packetIn = new SPacketChunkData(chunk,65535);
            chunk.read(packetIn.getReadBuffer(), packetIn.getExtractedSize(), packetIn.isFullChunk());
            world.markBlockRangeForRenderUpdate(packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15);
        }
    }
    public static void removeLight(BlockPos pos, World world){
        relight(pos,world);
        world.getChunk(getCardinalDistance(pos, BlockFace.NORTH, 15));
        updateChunk(pos,world);
    }
    private static BlockPos getCardinalDistance(BlockPos loc, BlockFace direction, float distance) {

        double dangle = distance * Math.sin(Math.toRadians(45));
        switch (direction) {
            case NORTH:
                loc = new BlockPos(loc.getX(), loc.getY(), loc.getZ() - distance);
                break;
            case NORTH_EAST:
                loc = new BlockPos(loc.getX() + dangle, loc.getY(), loc.getZ() - dangle);
                break;
            case EAST:
                loc = new BlockPos(loc.getX() + distance, loc.getY(), loc.getZ());
                break;
            case SOUTH_EAST:
                loc = new BlockPos(loc.getX() + dangle, loc.getY(), loc.getZ() + dangle);
                break;
            case SOUTH:
                loc = new BlockPos(loc.getX(), loc.getY(), loc.getZ() + distance);
                break;
            case SOUTH_WEST:
                loc = new BlockPos(loc.getX() - dangle, loc.getY(), loc.getZ() + dangle);
                break;
            case WEST:
                loc = new BlockPos(loc.getX() - distance, loc.getY(), loc.getZ());
                break;
            case NORTH_WEST:
                loc = new BlockPos(loc.getX() - dangle, loc.getY(), loc.getZ() - dangle);
                break;
        }
        return loc;
    }
}
