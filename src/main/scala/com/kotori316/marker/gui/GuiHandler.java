package com.kotori316.marker.gui;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.network.FMLPlayMessages;

import com.kotori316.marker.BlockMarker;
import com.kotori316.marker.Tile16Marker;
import com.kotori316.marker.TileFlexMarker;

public class GuiHandler implements IGuiHandler {

    public static final int Marker_ID = 0;
    public static final int Marker16_ID = 1;

    @Nullable
    @Override
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
        if (entity instanceof TileFlexMarker && ID == Marker_ID) {
            return new ContainerMarker(player);
        } else if (entity instanceof Tile16Marker && ID == Marker16_ID) {
            return new ContainerMarker(player);
        }
        return null;
    }

    @Nullable
    @Override
    public GuiScreen getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity entity = world.getTileEntity(new BlockPos(x, y, z));
        if (entity instanceof TileFlexMarker && ID == Marker_ID) {
            return new GuiMarker(player, (TileFlexMarker) entity);
        } else if (entity instanceof Tile16Marker && ID == Marker16_ID) {
            return new Gui16Marker(player, ((Tile16Marker) entity));
        }
        return null;
    }

    public static GuiScreen getGui(FMLPlayMessages.OpenContainer context) {
        BlockPos pos = context.getAdditionalData().readBlockPos();
        TileEntity tile = Minecraft.getInstance().world.getTileEntity(pos);
        EntityPlayerSP player = Minecraft.getInstance().player;
        if (BlockMarker.GUI_ID.equals(context.getId().toString()) && tile instanceof TileFlexMarker) {
            TileFlexMarker marker = (TileFlexMarker) tile;
            return new GuiMarker(player, marker);
        }
        if (BlockMarker.GUI_ID.equals(context.getId().toString()) && tile instanceof Tile16Marker) {
            Tile16Marker marker = (Tile16Marker) tile;
            return new Gui16Marker(player, marker);
        }
        return null;
    }
}
