package com.kotori316.marker;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import buildcraft.api.tiles.IDebuggable;
import buildcraft.api.tiles.ITileAreaProvider;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

@net.minecraftforge.fml.common.Optional.Interface(modid = "BuildCraftAPI|core", iface = "buildcraft.api.tiles.ITileAreaProvider")
@net.minecraftforge.fml.common.Optional.Interface(modid = "BuildCraftAPI|tiles", iface = "buildcraft.api.tiles.IDebuggable")
public class TileFlexMarker extends TileEntity implements ITileAreaProvider, IDebuggable {

    private BlockPos min = BlockPos.ORIGIN;
    private BlockPos max = BlockPos.ORIGIN;
    public EnumFacing direction;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setLong("min", min.toLong());
        compound.setLong("max", max.toLong());
        compound.setString("direction", Optional.ofNullable(direction).map(EnumFacing::toString).orElse(""));
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        min = BlockPos.fromLong(compound.getLong("min"));
        max = BlockPos.fromLong(compound.getLong("max"));
        direction = EnumFacing.byName(compound.getString("direction"));
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

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing side) {
        String[] strings = {
            "Pos: x=" + pos.getX() + " y=" + pos.getY() + " z=" + pos.getZ(),
            "Facing: " + (direction == null ? "Unknown" : direction.toString())
        };
        left.addAll(Arrays.asList(strings));
    }
}
