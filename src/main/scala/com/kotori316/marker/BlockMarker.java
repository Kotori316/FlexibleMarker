package com.kotori316.marker;

import java.util.Optional;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import com.kotori316.marker.gui.ContainerMarker;

public abstract class BlockMarker extends Block {
    private static final VoxelShape STANDING_Shape = VoxelShapes.create(.35, 0, .35, .65, .65, .65);

    public final ItemBlock itemBlock;

    public BlockMarker(String name) {
        super(Block.Properties.create(Material.CIRCUITS));
        setRegistryName(Marker.modID, name);
        this.itemBlock = new ItemBlock(this, new Item.Properties().group(ItemGroup.REDSTONE));
        itemBlock.setRegistryName(Marker.modID, name);
    }

    protected abstract boolean openGUI(World worldIn, BlockPos pos, EntityPlayer playerIn);

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking()) {
            if (!worldIn.isRemote) {
                openGUI(worldIn, pos, player);
            }
            return true;
        }
        return super.onBlockActivated(state, worldIn, pos, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("deprecation")
    public boolean isSideInvisible(IBlockState state, IBlockState adjacentBlockState, EnumFacing side) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
        return STANDING_Shape;
    }

    /**
     * Just copied from {@link net.minecraft.block.BlockTorchWall}.
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
        EnumFacing enumfacing = EnumFacing.UP;
        BlockPos blockpos = pos.offset(enumfacing.getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape(worldIn, blockpos, enumfacing) == BlockFaceShape.SOLID;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
    }

    @Override
    public abstract void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack);

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public abstract TileEntity createTileEntity(IBlockState state, IBlockReader world);


    public static class BlockFlexMarker extends BlockMarker {
        public static final String NAME = "marker";

        public BlockFlexMarker() {
            super(NAME);
        }

        @Override
        public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
            return Marker.TYPE.create();
        }

        @Override
        protected boolean openGUI(World worldIn, BlockPos pos, EntityPlayer playerIn) {
            NetworkHooks.openGui(((EntityPlayerMP) playerIn), new InteractionObject(), pos);
            return true;
        }

        @Override
        public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
            Optional.ofNullable((TileFlexMarker) worldIn.getTileEntity(pos)).ifPresent(t -> t.init(EnumFacing.fromAngle(placer.getRotationYawHead())));
        }

    }

    public static class Block16Marker extends BlockMarker {
        private static final Range RANGE = new Range(0, 360);

        public Block16Marker() {
            super("marker16");
        }

        @Override
        protected boolean openGUI(World worldIn, BlockPos pos, EntityPlayer playerIn) {
            NetworkHooks.openGui(((EntityPlayerMP) playerIn), new InteractionObject(), pos);
            return true;
        }

        @Override
        public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
            float angle = RANGE.convert(placer.getRotationYawHead());
            EnumFacing.AxisDirection z = angle < 90 || angle >= 270 ? EnumFacing.AxisDirection.POSITIVE : EnumFacing.AxisDirection.NEGATIVE;
            EnumFacing.AxisDirection x = angle > 180 ? EnumFacing.AxisDirection.POSITIVE : EnumFacing.AxisDirection.NEGATIVE;
            Optional.ofNullable((Tile16Marker) worldIn.getTileEntity(pos)).ifPresent(t -> t.init(x, z));
        }

        @Override
        public TileEntity createTileEntity(IBlockState state, IBlockReader world) {
            return Marker.TYPE16.create();
        }
    }

    public static final String GUI_ID = Marker.modID + ":gui_" + "marker";

    private static class InteractionObject implements IInteractionObject {

        @Override
        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
            return new ContainerMarker(playerIn);
        }

        @Override
        public String getGuiID() {
            return GUI_ID;
        }

        @SuppressWarnings("NoTranslation")
        @Override
        public ITextComponent getName() {
            return new TextComponentTranslation("block.flexiblemarker.marker");
        }

        @Override
        public boolean hasCustomName() {
            return false;
        }

        @Nullable
        @Override
        public ITextComponent getCustomName() {
            return null;
        }
    }
}
