package com.kotori316.marker;

import java.util.Optional;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.kotori316.marker.gui.GuiHandler;

public class BlockMarker extends Block implements ITileEntityProvider {
    private static final AxisAlignedBB STANDING_AABB = new AxisAlignedBB(.35, 0, .35, .65, .65, .65);

    public final ItemBlock itemBlock;

    public BlockMarker() {
        super(Material.CIRCUITS);
        setRegistryName(Marker.modID, "marker");
        setUnlocalizedName("flexiblemarker");
        setCreativeTab(CreativeTabs.REDSTONE);
        this.hasTileEntity = true;
        this.itemBlock = new ItemBlock(this);
        itemBlock.setRegistryName(Marker.modID, "marker");
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.isSneaking()) {
            playerIn.openGui(Marker.getInstance(), GuiHandler.Marker_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
        return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Nullable
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return STANDING_AABB;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return side == EnumFacing.UP && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        dropItem(worldIn, pos);
    }

    private void dropItem(World worldIn, BlockPos pos) {
        if (!worldIn.isSideSolid(pos.down(), EnumFacing.UP)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        dropItem(worldIn, pos);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        Optional.ofNullable((TileFlexMarker) worldIn.getTileEntity(pos)).ifPresent(t -> t.direction = EnumFacing.fromAngle(placer.getRotationYawHead()));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileFlexMarker();
    }
}
