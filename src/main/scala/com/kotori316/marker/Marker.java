package com.kotori316.marker;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.kotori316.marker.gui.GuiHandler;
import com.kotori316.marker.packet.PacketHandler;
import com.kotori316.marker.render.RenderMarker;

@Mod(Marker.modID)
public class Marker {
    public static final String modID = "flexiblemarker";
    public static final String ModName = "FlexibleMarker";
    public static final BlockMarker blockMarker = new BlockMarker();
    public static final TileEntityType<TileFlexMarker> TYPE = TileEntityType.Builder.create(TileFlexMarker::new).build(null);

    public Marker() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void preInit(FMLCommonSetupEvent event) {
//        NetworkRegistry.INSTANCE.registerGuiHandler(getInstance(), new GuiHandler());
        PacketHandler.init();
    }

    @SubscribeEvent
    public void clientInit(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileFlexMarker.class, RenderMarker.getInstance());
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.GUIFACTORY, () -> GuiHandler::getGui);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(blockMarker);
    }

    @SubscribeEvent
    public void registerTile(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TYPE.setRegistryName(modID + ":flexiblemarker"));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(blockMarker.itemBlock);
    }

}
