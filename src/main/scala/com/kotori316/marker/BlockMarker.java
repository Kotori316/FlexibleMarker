package com.kotori316.marker;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import com.kotori316.marker.gui.ContainerMarker;

public class BlockMarker extends Block {
    private static final VoxelShape STANDING_Shape = VoxelShapes.create(.35, 0, .35, .65, .65, .65);
    public static final String NAME = "marker";

    public final BlockItem itemBlock;

    public BlockMarker() {
        super(Block.Properties.create(Material.MISCELLANEOUS));
        setRegistryName(Marker.modID, NAME);
        this.itemBlock = new BlockItem(this, new Item.Properties().group(ItemGroup.REDSTONE));
        itemBlock.setRegistryName(Marker.modID, NAME);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos,
                                    PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!player.isSneaking()) {
            if (!worldIn.isRemote) {
                NetworkHooks.openGui(((ServerPlayerEntity) player), new InteractionObject(pos), pos);
            }
            return true;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("deprecation")
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return STANDING_Shape;
    }

    /**
     * Just copied from {@link WallTorchBlock}.
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        Direction direction = Direction.UP;
        BlockPos blockpos = pos.offset(direction.getOpposite());
        BlockState floorState = worldIn.getBlockState(blockpos);
        return floorState.func_224755_d(worldIn, blockpos, direction);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        Optional.ofNullable((TileFlexMarker) worldIn.getTileEntity(pos)).ifPresent(t -> t.init(Direction.fromAngle(placer.getRotationYawHead())));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return Marker.TILE_TYPE.create();
    }

    public static final String GUI_ID = Marker.modID + ":gui_" + NAME;

    private static class InteractionObject implements INamedContainerProvider {
        private final BlockPos pos;

        public InteractionObject(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public ITextComponent getDisplayName() {
            return new TranslationTextComponent("block.flexiblemarker.marker");
        }

        @Override
        public Container createMenu(int id, PlayerInventory p_createMenu_2_, PlayerEntity playerIn) {
            return new ContainerMarker(id, playerIn, this.pos);
        }
    }
}
