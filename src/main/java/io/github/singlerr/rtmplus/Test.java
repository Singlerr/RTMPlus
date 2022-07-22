package io.github.singlerr.rtmplus;

import jp.ngt.ngtlib.block.BlockContainerCustomWithMeta;
import jp.ngt.rtm.block.tileentity.TileEntityMachineBase;
import jp.ngt.rtm.modelpack.cfg.MachineConfig;
import jp.ngt.rtm.modelpack.modelset.ModelSetMachine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Test extends BlockContainerCustomWithMeta {
    protected Test(Material material) {
        super(material);
    }
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        Object obj = world.getTileEntity(pos);
        if(! (obj instanceof TileEntityMachineBase)) return super.getLightValue(state);
        TileEntityMachineBase tile = (TileEntityMachineBase)world.getTileEntity(pos);
        if (tile == null) {
            return 0;
        } else {
            MachineConfig cfg = (MachineConfig)((ModelSetMachine)tile.getResourceState().getResourceSet()).getConfig();
            return tile.isGettingPower ? cfg.brightness[1] : cfg.brightness[0];
        }
    }
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }
}
