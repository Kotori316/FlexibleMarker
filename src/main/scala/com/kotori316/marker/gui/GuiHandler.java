package com.kotori316.marker.gui;

import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import com.kotori316.marker.TileFlexMarker;

public class GuiHandler implements IGuiHandler {

    public static final int Marker_ID = 0;

    @Nullable
    @Override
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
        if (entity instanceof TileFlexMarker && ID == Marker_ID) {
            return new ContainerMarker(player);
        }
        return null;
    }

    @Nullable
    @Override
    public GuiScreen getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
        if (entity instanceof TileFlexMarker && ID == Marker_ID) {
            return new GuiMarker(player);
        }
        return null;
    }
}
