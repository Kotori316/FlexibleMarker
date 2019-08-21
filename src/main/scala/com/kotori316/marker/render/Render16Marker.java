package com.kotori316.marker.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.TileEntityRendererFast;

import com.kotori316.marker.Tile16Marker;

public class Render16Marker extends TileEntityRendererFast<Tile16Marker> {
    private static Render16Marker ourInstance = new Render16Marker();

    public static Render16Marker getInstance() {
        return ourInstance;
    }

    private Render16Marker() {
    }

    @Override
    public void renderTileEntityFast(Tile16Marker te, double x, double y, double z,
                                     float partialTicks, int destroyStage, BufferBuilder buffer) {
        Minecraft.getInstance().getProfiler().startSection("marker");
        BlockPos pos = te.getPos();
        buffer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
        if (te.boxes != null) {
            for (Box box : te.boxes) {
                box.render(buffer, RenderMarker.getInstance().spriteWhite, ColorBox.redColor());
            }
        }
        Minecraft.getInstance().getProfiler().endSection();
    }

    @Override
    public boolean isGlobalRenderer(Tile16Marker te) {
        return true;
    }
}
