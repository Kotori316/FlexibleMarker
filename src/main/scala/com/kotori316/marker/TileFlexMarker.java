package com.kotori316.marker;

import buildcraft.api.tiles.ITileAreaProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

@net.minecraftforge.fml.common.Optional.Interface(modid = "BuildCraftAPI|core", iface = "buildcraft.api.tiles.ITileAreaProvider")
public class TileFlexMarker extends TileEntity implements ITileAreaProvider {

    private BlockPos min = BlockPos.ORIGIN;
    private BlockPos max = BlockPos.ORIGIN;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setLong("min", min.toLong());
        compound.setLong("max", max.toLong());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        min = BlockPos.fromLong(compound.getLong("min"));
        max = BlockPos.fromLong(compound.getLong("max"));
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "BuildCraftAPI|core")
    public BlockPos min() {
        return min == BlockPos.ORIGIN ? getPos() : min;
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "BuildCraftAPI|core")
    public BlockPos max() {
        return max == BlockPos.ORIGIN ? getPos() : max;
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "BuildCraftAPI|core")
    public void removeFromWorld() {
        if (!getWorld().isRemote) {
            getWorld().destroyBlock(getPos(), true);
        }
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "BuildCraftAPI|core")
    public boolean isValidFromLocation(BlockPos pos) {
        return false;
    }
}
