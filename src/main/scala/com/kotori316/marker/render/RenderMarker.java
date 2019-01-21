package com.kotori316.marker.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.kotori316.marker.Marker;
import com.kotori316.marker.TileFlexMarker;

public class RenderMarker extends FastTESR<TileFlexMarker> {
    private static RenderMarker ourInstance = new RenderMarker();
    private TextureAtlasSprite spriteRed, spriteBlue;

    public static RenderMarker getInstance() {
        return ourInstance;
    }

    private RenderMarker() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void renderTileEntityFast(TileFlexMarker te, double x, double y, double z,
                                     float partialTicks, int destroyStage, float partial, BufferBuilder buffer) {
        Minecraft.getMinecraft().mcProfiler.startSection("marker");
        BlockPos pos = te.getPos();
        buffer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
        if (te.boxes != null) {
            for (Box box : te.boxes) {
                box.render(buffer, spriteRed);
            }
        }
        if (te.directionBox != null) {
            te.directionBox.render(buffer, spriteBlue);
        }
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    @SubscribeEvent
    public void registerTexture(TextureStitchEvent.Pre event) {
        spriteRed = event.getMap().registerSprite(new ResourceLocation(Marker.modID, "blocks/red"));
        spriteBlue = event.getMap().registerSprite(new ResourceLocation(Marker.modID, "blocks/blue"));
    }
}
