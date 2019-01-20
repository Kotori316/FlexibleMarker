package com.kotori316.marker;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

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

    public void init(EnumFacing facing) {
        this.direction = facing;
        this.min = getPos().offset(direction);
        this.max = getPos().offset(direction);
        move(Movable.LEFT, 5);
        move(Movable.RIGHT, 5);
        move(Movable.FORWARD, 10);
    }

    public void move(Movable movable, int amount) {
        EnumFacing facing = movable.getActualFacing(direction);
        BlockPos offset = getPos().offset(direction);
        if (facing.getAxis() == EnumFacing.Axis.Y) {
            if (facing == EnumFacing.UP) {
                max = max.offset(facing, amount);
                int d = getDistance(max, offset, facing.getAxis());
                if (d > 64) {
                    max = getLimited(max, offset, facing, 64);
                } else if (d < 0) {
                    max = getLimited(max, offset, facing, 0);
                }
            } else {
                min = min.offset(facing, amount);
                int d = getDistance(offset, min, facing.getAxis());
                if (d > 64) {
                    min = getLimited(min, offset, facing, 64);
                } else if (d < 0) {
                    min = getLimited(min, offset, facing, 0);
                }
                if (min.getY() < 0) {
                    min = new BlockPos(min.getX(), 0, min.getZ());
                }
            }
        } else if (facing.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
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
        }
    }

    public Runnable setMinMax(BlockPos min, BlockPos max) {
        return () -> {
            this.min = min;
            this.max = max;
        };
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
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "BuildCraftAPI|core")
    public BlockPos min() {
        return getMin();
    }

    public BlockPos getMin() {
        return min == BlockPos.ORIGIN ? getPos().offset(direction) : min;
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "BuildCraftAPI|core")
    public BlockPos max() {
        return getMax();
    }

    public BlockPos getMax() {
        return max == BlockPos.ORIGIN ? getPos().offset(direction) : max;
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
            "Facing: " + (direction == null ? "Unknown" : direction.toString()),
            "Min: x=" + min.getX() + " y=" + min.getY() + " z=" + min.getZ(),
            "Max: x=" + max.getX() + " y=" + max.getY() + " z=" + max.getZ(),
        };
        left.addAll(Arrays.asList(strings));
    }

    public enum Movable {
        UP(facing -> EnumFacing.UP),
        LEFT(EnumFacing::rotateYCCW),
        FORWARD(UnaryOperator.identity()),
        RIGHT(EnumFacing::rotateY),
        DOWN(facing -> EnumFacing.DOWN);

        private UnaryOperator<EnumFacing> operator;

        Movable(UnaryOperator<EnumFacing> operator) {
            this.operator = operator;
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
