package com.kotori316.marker;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

import buildcraft.api.tiles.IDebuggable;
import buildcraft.api.tiles.ITileAreaProvider;
import buildcraft.api.tiles.TilesAPI;
import com.yogpc.qp.tile.IMarker;
import javax.annotation.Nullable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.kotori316.marker.render.Box;

@net.minecraftforge.fml.common.Optional.Interface(modid = TileFlexMarker.BC_TILE_ID, iface = "buildcraft.api.tiles.ITileAreaProvider")
@net.minecraftforge.fml.common.Optional.Interface(modid = TileFlexMarker.BC_TILE_ID, iface = "buildcraft.api.tiles.IDebuggable")
@net.minecraftforge.fml.common.Optional.Interface(modid = "quarryplus", iface = "com.yogpc.qp.tile.IMarker")
public class TileFlexMarker extends TileEntity implements ITileAreaProvider, IDebuggable, IMarker, IAreaConfigurable {

    public static final String BC_CORE_ID = "buildcraftlib"; // BuildCraftAPI|core - buildcraftapi_core
    public static final String BC_TILE_ID = "buildcraftlib"; // BuildCraftAPI|tiles - buildcraftapi_tiles

    private BlockPos min = BlockPos.ORIGIN;
    private BlockPos max = BlockPos.ORIGIN;
    @Nullable
    public Box[] boxes;
    @Nullable
    public Box directionBox;
    public EnumFacing direction;
    private boolean bcLoaded = Loader.isModLoaded(BC_TILE_ID); // ModAPIManager.INSTANCE.hasAPI("buildcraftapi_tiles");

    public void init(EnumFacing facing) {
        this.direction = facing;
        this.min = getPos();
        this.max = getPos();
        move(Movable.LEFT, 5);
        move(Movable.RIGHT, 5);
        move(Movable.FORWARD, 10);
        setRender();
    }

    @SuppressWarnings("Duplicates")
    public void move(Movable movable, int amount) {
        EnumFacing facing = movable.getActualFacing(direction);
        BlockPos offset = getPos();
        if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
            max = max.offset(facing, amount);
            int d = getDistance(max, offset, facing.getAxis());
            if (d > 64) {
                max = getLimited(max, offset, facing, 64);
            } else if (d < 1) {
                max = getLimited(max, offset, facing, 1);
            }
        } else {
            min = min.offset(facing, amount);
            int d = getDistance(offset, min, facing.getAxis());
            if (d > 64) {
                min = getLimited(min, offset, facing, 64);
            } else if (d < 1) {
                min = getLimited(min, offset, facing, 1);
            }
            if (facing == EnumFacing.DOWN && min.getY() < 0) {
                min = new BlockPos(min.getX(), 0, min.getZ());
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Runnable setMinMax(BlockPos min, BlockPos max) {
        return () -> {
            this.min = min;
            this.max = max;
            setRender();
        };
    }

    private void setRender() {
        if (!world.isRemote)
            return;
        boxes = IAreaConfigurable.Util.getRenderBox(this.min, this.max);
        AxisAlignedBB bb;
        if (direction.getAxis() == EnumFacing.Axis.X) {
            bb = new AxisAlignedBB(getPos().getX() - c + a, getPos().getY() + a, getPos().getZ() + a,
                getPos().getX() + c + a, getPos().getY() + a, getPos().getZ() + a);
        } else {
            bb = new AxisAlignedBB(getPos().getX() + a, getPos().getY() + a, getPos().getZ() - c + a,
                getPos().getX() + a, getPos().getY() + a, getPos().getZ() + c + a);
        }
        directionBox = Box.apply(bb.offset(new Vec3d(direction.getDirectionVec()).scale(a)), 1d / 8d, 1d / 8d, 1d / 8d, true, true);
    }

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
        if (hasWorld()) {
            setRender();
        }
    }

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
    public boolean hasLink() {
        // There must always be area.
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
        Marker.block16Marker.getDrops(list, getWorld(), getPos(), getWorld().getBlockState(getPos()), 0);
        getWorld().setBlockToAir(getPos());
        return list;
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = TileFlexMarker.BC_CORE_ID)
    public void removeFromWorld() {
        if (!getWorld().isRemote) {
            getWorld().destroyBlock(getPos(), true);
        }
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = TileFlexMarker.BC_CORE_ID)
    public boolean isValidFromLocation(BlockPos pos) {
        return false;
    }

    @Override
    public void getDebugInfo(List<String> left, List<String> right, EnumFacing side) {
        String[] strings = {
            "Pos: x=" + pos.getX() + " y=" + pos.getY() + " z=" + pos.getZ(),
            "Facing: " + (direction == null ? "Unknown" : direction.toString()),
            "Min: x=" + min.getX() + " y=" + min.getY() + " z=" + min.getZ(),
            "Max: x=" + max.getX() + " y=" + max.getY() + " z=" + max.getZ(),
        };
        left.addAll(Arrays.asList(strings));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (bcLoaded) {
            if (capability == TilesAPI.CAP_TILE_AREA_PROVIDER) {
                return true;
            }
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (bcLoaded) {
            if (capability == TilesAPI.CAP_TILE_AREA_PROVIDER) {
                return TilesAPI.CAP_TILE_AREA_PROVIDER.cast(this);
            }
        }
        return super.getCapability(capability, facing);
    }

    public enum Movable {
        UP(facing -> EnumFacing.UP),
        LEFT(EnumFacing::rotateYCCW),
        FORWARD(UnaryOperator.identity()),
        RIGHT(EnumFacing::rotateY),
        DOWN(facing -> EnumFacing.DOWN);

        private UnaryOperator<EnumFacing> operator;
        public final String transName;

        Movable(UnaryOperator<EnumFacing> operator) {
            this.operator = operator;
            this.transName = "gui." + name().toLowerCase(Locale.US);
        }

        public EnumFacing getActualFacing(EnumFacing facing) {
            return this.operator.apply(facing);
        }

        private static final Movable[] v;

        public static Movable valueOf(int i) {
            return v[i];
        }

        static {
            v = values();
        }
    }

    public static int getDistance(BlockPos to, BlockPos from, EnumFacing.Axis axis) {
        switch (axis) {
            case X:
                return to.getX() - from.getX();
            case Y:
                return to.getY() - from.getY();
            case Z:
                return to.getZ() - from.getZ();
        }
        throw new IllegalStateException(String.format("Other axis? Axis=%s, from=%s, to=%s", axis, from, to));
    }

    public static BlockPos getLimited(BlockPos to, BlockPos from, EnumFacing facing, int limit) {
        switch (facing.getAxis()) {
            case X:
                return new BlockPos(from.getX(), to.getY(), to.getZ()).offset(facing, limit);
            case Y:
                return new BlockPos(to.getX(), from.getY(), to.getZ()).offset(facing, limit);
            case Z:
                return new BlockPos(to.getX(), to.getY(), from.getZ()).offset(facing, limit);
        }
        throw new IllegalStateException(String.format("Other axis? Facing=%s, from=%s, to=%s, limit=%d", facing, from, to, limit));
    }
}
