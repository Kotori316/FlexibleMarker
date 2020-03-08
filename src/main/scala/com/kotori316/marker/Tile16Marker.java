package com.kotori316.marker;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import buildcraft.api.tiles.IDebuggable;
import buildcraft.api.tiles.ITileAreaProvider;
import com.yogpc.qp.tile.IMarker;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.kotori316.marker.render.Box;

import static com.kotori316.marker.TileFlexMarker.BC_TILE_ID;

@net.minecraftforge.fml.common.Optional.Interface(modid = TileFlexMarker.BC_TILE_ID, iface = "buildcraft.api.tiles.ITileAreaProvider")
@net.minecraftforge.fml.common.Optional.Interface(modid = TileFlexMarker.BC_TILE_ID, iface = "buildcraft.api.tiles.IDebuggable")
@net.minecraftforge.fml.common.Optional.Interface(modid = "quarryplus", iface = "com.yogpc.qp.tile.IMarker")
public class Tile16Marker extends TileEntity implements ITileAreaProvider, IDebuggable, IMarker, IAreaConfigurable {
    private BlockPos min = BlockPos.ORIGIN;
    private BlockPos max = BlockPos.ORIGIN;
    @Nullable
    public Box[] boxes;
    private boolean bcLoaded = Loader.isModLoaded(BC_TILE_ID);
    private EnumFacing.AxisDirection xDirection = EnumFacing.AxisDirection.NEGATIVE, zDirection = EnumFacing.AxisDirection.POSITIVE;
    private int size = 16;

    public void init(EnumFacing.AxisDirection xDirection, EnumFacing.AxisDirection zDirection) {
        this.xDirection = Objects.requireNonNull(xDirection);
        this.zDirection = Objects.requireNonNull(zDirection);
        changeSize(this.size);
    }

    public void changeSize(int size) {
        this.size = size;
        BlockPos edge1 = getPos().add(xDirection.getOffset() * (size + 1), 0, zDirection.getOffset() * (size + 1));
        BlockPos edge2 = getPos();
        min = new BlockPos(Math.min(edge1.getX(), edge2.getX()), edge2.getY(), Math.min(edge1.getZ(), edge2.getZ()));
        max = new BlockPos(Math.max(edge1.getX(), edge2.getX()), edge2.getY(), Math.max(edge1.getZ(), edge2.getZ()));
        setRender();
    }

    private void setRender() {
        if (!world.isRemote)
            return;
        boxes = IAreaConfigurable.Util.getRenderBox(this.min, this.max);
    }

    @SideOnly(Side.CLIENT)
    public int getSize() {
        return size;
    }

    // TileEntity overrides
    @Override
    public NBTTagCompound getUpdateTag() {
        return super.serializeNBT();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 128 * 128 * 2;
    }

    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        min = BlockPos.fromLong(compound.getLong("min"));
        max = BlockPos.fromLong(compound.getLong("max"));
        xDirection = compound.getBoolean("x") ? EnumFacing.AxisDirection.POSITIVE : EnumFacing.AxisDirection.NEGATIVE;
        zDirection = compound.getBoolean("z") ? EnumFacing.AxisDirection.POSITIVE : EnumFacing.AxisDirection.NEGATIVE;
        size = compound.getInteger("size");
        if (hasWorld()) {
            setRender();
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setLong("min", min.toLong());
        compound.setLong("max", max.toLong());
        compound.setBoolean("x", xDirection == EnumFacing.AxisDirection.POSITIVE);
        compound.setBoolean("z", zDirection == EnumFacing.AxisDirection.POSITIVE);
        compound.setInteger("size", size);
        return super.writeToNBT(compound);
    }

    // Interface implementations
    @Override
    public boolean hasLink() {
        return true;
    }

    @Override
    public BlockPos min() {
        return min == BlockPos.ORIGIN ? getPos() : min;
    }

    @Override
    public BlockPos max() {
        return max == BlockPos.ORIGIN ? getPos() : max;
    }

    @Override
    public List<ItemStack> removeFromWorldWithItem() {
        NonNullList<ItemStack> list = NonNullList.create();
        Marker.blockMarker.getDrops(list, getWorld(), getPos(), getWorld().getBlockState(getPos()), 0);
        getWorld().setBlockToAir(getPos());
        return list;
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = TileFlexMarker.BC_CORE_ID)
    public boolean isValidFromLocation(BlockPos pos) {
        return false;
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = TileFlexMarker.BC_CORE_ID)
    public void removeFromWorld() {
        if (!getWorld().isRemote) {
            getWorld().destroyBlock(getPos(), true);
        }
    }

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing side) {
        String[] strings = {
            "Pos: x=" + pos.getX() + " y=" + pos.getY() + " z=" + pos.getZ(),
            "Facing: " + (xDirection == null ? "Unknown" : EnumFacing.getFacingFromAxis(xDirection, EnumFacing.Axis.X)) +
                ", " + (zDirection == null ? "Unknown" : EnumFacing.getFacingFromAxis(zDirection, EnumFacing.Axis.Z)),
            "Min: x=" + min.getX() + " y=" + min.getY() + " z=" + min.getZ(),
            "Max: x=" + max.getX() + " y=" + max.getY() + " z=" + max.getZ(),
        };
        left.addAll(Arrays.asList(strings));
    }

    @Override
    public Runnable setMinMax(BlockPos min, BlockPos max) {
        return () -> {
            this.max = max;
            this.min = min;
            setRender();
            size = (max.getX() - min.getX() - 1);
        };
    }
}
