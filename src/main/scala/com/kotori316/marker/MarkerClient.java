package com.kotori316.marker;

import java.util.Objects;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.kotori316.marker.render.Render16Marker;
import com.kotori316.marker.render.RenderMarker;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Marker.modID, value = Side.CLIENT)
public class MarkerClient {

    @Mod.EventHandler
    @SideOnly(value = Side.CLIENT)
    public static void preClientInit(FMLPreInitializationEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileFlexMarker.class, RenderMarker.getInstance());
        ClientRegistry.bindTileEntitySpecialRenderer(Tile16Marker.class, Render16Marker.getInstance());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Marker.blockMarker.itemBlock, 0,
            new ModelResourceLocation(Objects.requireNonNull(Marker.blockMarker.getRegistryName()), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Marker.block16Marker.itemBlock, 0,
            new ModelResourceLocation(Objects.requireNonNull(Marker.block16Marker.getRegistryName()), "inventory"));
    }
}
