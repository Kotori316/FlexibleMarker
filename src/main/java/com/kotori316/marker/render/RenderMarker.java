package com.kotori316.marker.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.animation.TileEntityRendererFast;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.kotori316.marker.Marker;
import com.kotori316.marker.TileFlexMarker;

public class RenderMarker extends TileEntityRendererFast<TileFlexMarker> {
    private static RenderMarker ourInstance = new RenderMarker();
    public TextureAtlasSprite spriteWhite;

    public static RenderMarker getInstance() {
        return ourInstance;
    }

    private RenderMarker() {
//        MinecraftForge.EVENT_BUS.register(this);
        // Register to mod event bus in Main class
    }

    @Override
    public void renderTileEntityFast(TileFlexMarker te, double x, double y, double z,
                                     float partialTicks, int destroyStage, BufferBuilder buffer) {
        Minecraft.getInstance().getProfiler().startSection("marker");
        BlockPos pos = te.getPos();
        buffer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());
        if (te.boxes != null) {
            for (Box box : te.boxes) {
                box.render(buffer, spriteWhite, ColorBox.redColor);
            }
        }
        if (te.directionBox != null) {
            te.directionBox.render(buffer, spriteWhite, ColorBox.blueColor);
        }
        Minecraft.getInstance().getProfiler().endSection();
    }

    @SubscribeEvent
    public void registerTexture(TextureStitchEvent.Pre event) {
        if ("textures".equals(event.getMap().getBasePath())) {
            event.addSprite(new ResourceLocation(Marker.modID, "blocks/white"));
        }
    }

    @SubscribeEvent
    public void putTexture(TextureStitchEvent.Post event) {
        if ("textures".equals(event.getMap().getBasePath())) {
            spriteWhite = event.getMap().getSprite(new ResourceLocation(Marker.modID, "blocks/white"));
        }
    }

    @Override
    public boolean isGlobalRenderer(TileFlexMarker te) {
        return true;
    }
}
