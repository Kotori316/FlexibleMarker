package com.kotori316.marker;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.kotori316.marker.gui.ContainerMarker;
import com.kotori316.marker.gui.GuiMarker;
import com.kotori316.marker.packet.PacketHandler;
import com.kotori316.marker.render.RenderMarker;

@Mod(Marker.modID)
public class Marker {
    public static final String modID = "flexiblemarker";
    public static final String ModName = "FlexibleMarker";
    public static final BlockMarker blockMarker = new BlockMarker();
    public static final TileEntityType<TileFlexMarker> TILE_TYPE = TileEntityType.Builder.create(TileFlexMarker::new, blockMarker).build(null);
    public static final ContainerType<ContainerMarker> CONTAINER_TYPE = IForgeContainerType.create((windowId, inv, data) ->
        new ContainerMarker(windowId, inv.player, data.readBlockPos()));

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
        FMLJavaModLoadingContext.get().getModEventBus().register(RenderMarker.getInstance());
        ScreenManager.registerFactory(CONTAINER_TYPE, GuiMarker::new);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(blockMarker);
    }

    @SubscribeEvent
    public void registerTile(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TILE_TYPE.setRegistryName(modID + ":flexiblemarker"));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(blockMarker.itemBlock);
    }

    @SubscribeEvent
    public void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(CONTAINER_TYPE.setRegistryName(BlockMarker.GUI_ID));
    }
}
