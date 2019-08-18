package com.kotori316.marker.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.FastTESR;

import com.kotori316.marker.Tile16Marker;

public class Render16Marker extends FastTESR<Tile16Marker> {
    private static Render16Marker ourInstance = new Render16Marker();

    public static Render16Marker getInstance() {
        return ourInstance;
    }

    private Render16Marker() {
    }

    @Override
    public void renderTileEntityFast(Tile16Marker te, double x, double y, double z,
                                     float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {
        Minecraft.getMinecraft().mcProfiler.startSection("marker");
        BlockPos pos = te.getPos();
        buffer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
        if (te.boxes != null) {
            for (Box box : te.boxes) {
                box.render(buffer, RenderMarker.getInstance().spriteRed);
            }
        }
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    @Override
    public boolean isGlobalRenderer(Tile16Marker te) {
        return true;
    }
}
