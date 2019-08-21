package com.kotori316.marker;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.kotori316.marker.gui.ContainerMarker;
import com.kotori316.marker.gui.Gui16Marker;
import com.kotori316.marker.gui.GuiMarker;
import com.kotori316.marker.packet.PacketHandler;
import com.kotori316.marker.render.Render16Marker;
import com.kotori316.marker.render.RenderMarker;

@Mod(Marker.modID)
public class Marker {
    public static final String modID = "flexiblemarker";
    public static final String ModName = "FlexibleMarker";
    public static final BlockMarker blockMarker = new BlockMarker.BlockFlexMarker();
    public static final BlockMarker block16Marker = new BlockMarker.Block16Marker();
    public static final TileEntityType<TileFlexMarker> TYPE = TileEntityType.Builder.create(TileFlexMarker::new, blockMarker).build(null);
    public static final TileEntityType<Tile16Marker> TYPE16 = TileEntityType.Builder.create(Tile16Marker::new, block16Marker).build(null);
    public static final ContainerType<ContainerMarker> CONTAINER_TYPE = IForgeContainerType.create((windowId, inv, data) ->
        new ContainerMarker(windowId, inv.player, data.readBlockPos(), Marker.CONTAINER_TYPE));
    public static final ContainerType<ContainerMarker> CONTAINER16_TYPE = IForgeContainerType.create((windowId, inv, data) ->
        new ContainerMarker(windowId, inv.player, data.readBlockPos(), Marker.CONTAINER16_TYPE));

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
        ClientRegistry.bindTileEntitySpecialRenderer(Tile16Marker.class, Render16Marker.getInstance());
        FMLJavaModLoadingContext.get().getModEventBus().register(RenderMarker.getInstance());
        ScreenManager.registerFactory(CONTAINER_TYPE, GuiMarker::new);
        ScreenManager.registerFactory(CONTAINER16_TYPE, Gui16Marker::new);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(blockMarker);
        event.getRegistry().register(block16Marker);
    }

    @SubscribeEvent
    public void registerTile(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TYPE.setRegistryName(modID + ":flexiblemarker"));
        event.getRegistry().register(TYPE16.setRegistryName(modID + ":marker16"));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(blockMarker.itemBlock);
        event.getRegistry().register(block16Marker.itemBlock);
    }

    @SubscribeEvent
    public void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(CONTAINER_TYPE.setRegistryName(BlockMarker.GUI_ID));
        event.getRegistry().register(CONTAINER16_TYPE.setRegistryName(BlockMarker.GUI16_ID));
    }
}
