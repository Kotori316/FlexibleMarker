package com.kotori316.marker;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModList;

import com.kotori316.marker.render.Box;

import static com.kotori316.marker.TileFlexMarker.BC_TILE_ID;

public class Tile16Marker extends TileEntity implements /*ITileAreaProvider, IDebuggable, IMarker,*/ IAreaConfigurable {
    private BlockPos min = BlockPos.ZERO;
    private BlockPos max = BlockPos.ZERO;
    @Nullable
    public Box[] boxes;
    private boolean bcLoaded = ModList.get().isLoaded(BC_TILE_ID);
    private Direction.AxisDirection xDirection = Direction.AxisDirection.NEGATIVE, zDirection = Direction.AxisDirection.POSITIVE;
    private int size = 16;

    public Tile16Marker() {
        super(Marker.TYPE16);
    }

    public void init(Direction.AxisDirection xDirection, Direction.AxisDirection zDirection) {
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
        assert world != null;
        if (!world.isRemote)
            return;

        int flag = 0;
        final double a = 0.5d, b = 10d / 16d, c = 6d / 16d;
        int xMin = min.getX();
        int yMin = min.getY();
        int zMin = min.getZ();
        int xMax = max.getX();
        int yMax = max.getY();
        int zMax = max.getZ();
        if (xMin != xMax)
            flag |= 1;
        if (yMin != yMax)
            flag |= 2;
        if (zMin != zMax)
            flag |= 4;
        AxisAlignedBB[] lineBoxes = new AxisAlignedBB[12];
        if ((flag & 1) == 1) {//x
            lineBoxes[0] = new AxisAlignedBB(xMin + b, yMin + a, zMin + a, xMax + c, yMin + a, zMin + a);
        }
        if ((flag & 2) == 2) {//y
            lineBoxes[4] = new AxisAlignedBB(xMin + a, yMin + b, zMin + a, xMin + a, yMax + c, zMin + a);
        }
        if ((flag & 4) == 4) {//z
            lineBoxes[8] = new AxisAlignedBB(xMin + a, yMin + a, zMin + b, xMin + a, yMin + a, zMax + c);
        }
        if ((flag & 3) == 3) {//xy
            lineBoxes[2] = new AxisAlignedBB(xMin + b, yMax + a, zMin + a, xMax + c, yMax + a, zMin + a);
            lineBoxes[6] = new AxisAlignedBB(xMax + a, yMin + b, zMin + a, xMax + a, yMax + c, zMin + a);
        }
        if ((flag & 5) == 5) {//xz
            lineBoxes[1] = new AxisAlignedBB(xMin + b, yMin + a, zMax + a, xMax + c, yMin + a, zMax + a);
            lineBoxes[9] = new AxisAlignedBB(xMax + a, yMin + a, zMin + b, xMax + a, yMin + a, zMax + c);
        }
        if ((flag & 6) == 6) {//yz
            lineBoxes[5] = new AxisAlignedBB(xMin + a, yMin + b, zMax + a, xMin + a, yMax + c, zMax + a);
            lineBoxes[10] = new AxisAlignedBB(xMin + a, yMax + a, zMin + b, xMin + a, yMax + a, zMax + c);
        }
        if ((flag & 7) == 7) {//xyz
            lineBoxes[3] = new AxisAlignedBB(xMin + b, yMax + a, zMax + a, xMax + c, yMax + a, zMax + a);
            lineBoxes[7] = new AxisAlignedBB(xMax + a, yMin + b, zMax + a, xMax + a, yMax + c, zMax + a);
            lineBoxes[11] = new AxisAlignedBB(xMax + a, yMax + a, zMin + b, xMax + a, yMax + a, zMax + c);
        }

        boxes = Arrays.stream(lineBoxes).filter(Objects::nonNull)
            .map(range -> Box.apply(range, 1d / 8d, 1d / 8d, 1d / 8d, false, false))
            .toArray(Box[]::new);
    }

    @OnlyIn(Dist.CLIENT)
    public int getSize() {
        return size;
    }

    // TileEntity overrides
    @Override
    public CompoundNBT getUpdateTag() {
        return super.serializeNBT();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 128 * 128 * 2;
    }

    @Override
    public boolean hasFastRenderer() {
        return true;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        min = BlockPos.fromLong(compound.getLong("min"));
        max = BlockPos.fromLong(compound.getLong("max"));
        xDirection = compound.getBoolean("x") ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        zDirection = compound.getBoolean("z") ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        size = compound.getInt("size");
        if (hasWorld()) {
            setRender();
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putLong("min", min.toLong());
        compound.putLong("max", max.toLong());
        compound.putBoolean("x", xDirection == Direction.AxisDirection.POSITIVE);
        compound.putBoolean("z", zDirection == Direction.AxisDirection.POSITIVE);
        compound.putInt("size", size);
        return super.write(compound);
    }

    // Interface implementations
//    @Override
    public boolean hasLink() {
        return true;
    }

    //    @Override
    public BlockPos min() {
        return min == BlockPos.ZERO ? getPos() : min;
    }

    //    @Override
    public BlockPos max() {
        return max == BlockPos.ZERO ? getPos() : max;
    }

    //    @Override
    public List<ItemStack> removeFromWorldWithItem() {
        List<ItemStack> drops = Block.getDrops(getWorld().getBlockState(getPos()), (ServerWorld) getWorld(), getPos(), this);
        getWorld().removeBlock(getPos(), false);
        return drops;
    }

    /*
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
        public void getDebugInfo(List<String> left, List<String> right, Direction side) {
            String[] strings = {
                "Pos: x=" + pos.getX() + " y=" + pos.getY() + " z=" + pos.getZ(),
                "Facing: " + (xDirection == null ? "Unknown" : Direction.getFacingFromAxis(xDirection, Direction.Axis.X)) +
                    ", " + (zDirection == null ? "Unknown" : Direction.getFacingFromAxis(zDirection, Direction.Axis.Z)),
                "Min: x=" + min.getX() + " y=" + min.getY() + " z=" + min.getZ(),
                "Max: x=" + max.getX() + " y=" + max.getY() + " z=" + max.getZ(),
            };
            left.addAll(Arrays.asList(strings));
        }
    */
    @Override
    @OnlyIn(Dist.CLIENT)
    public Runnable setMinMax(BlockPos min, BlockPos max) {
        return () -> {
            this.max = max;
            this.min = min;
            setRender();
            size = (max.getX() - min.getX() - 1);
        };
    }
}
