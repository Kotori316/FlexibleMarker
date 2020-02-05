package com.kotori316.marker;

import com.mojang.datafixers.DSL;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    public static final Group ITEM_GROUP = new Group();

    public Marker() {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
    }

    @SubscribeEvent
    public void preInit(FMLCommonSetupEvent event) {
//        NetworkRegistry.INSTANCE.registerGuiHandler(getInstance(), new GuiHandler());
        PacketHandler.init();
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientInit(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(TileFlexMarker.class, RenderMarker.getInstance());
        ClientRegistry.bindTileEntitySpecialRenderer(Tile16Marker.class, Render16Marker.getInstance());
        FMLJavaModLoadingContext.get().getModEventBus().register(RenderMarker.getInstance());
        ScreenManager.registerFactory(ModObjects.CONTAINER_TYPE, GuiMarker::new);
        ScreenManager.registerFactory(ModObjects.CONTAINER16_TYPE, Gui16Marker::new);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ModObjects.blockMarker);
        event.getRegistry().register(ModObjects.block16Marker);
    }

    @SubscribeEvent
    public void registerTile(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(ModObjects.TYPE.setRegistryName(modID + ":flexiblemarker"));
        event.getRegistry().register(ModObjects.TYPE16.setRegistryName(modID + ":marker16"));
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ModObjects.blockMarker.itemBlock);
        event.getRegistry().register(ModObjects.block16Marker.itemBlock);
        event.getRegistry().register(ModObjects.remoteControlItem);
    }

    @SubscribeEvent
    public void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(ModObjects.CONTAINER_TYPE.setRegistryName(BlockMarker.GUI_ID));
        event.getRegistry().register(ModObjects.CONTAINER16_TYPE.setRegistryName(BlockMarker.GUI16_ID));
    }

    public static final class ModObjects {
        public static final BlockMarker blockMarker = new BlockMarker.BlockFlexMarker();
        public static final TileEntityType<TileFlexMarker> TYPE = TileEntityType.Builder.create(TileFlexMarker::new, blockMarker).build(DSL.nilType());
        public static final BlockMarker block16Marker = new BlockMarker.Block16Marker();
        public static final TileEntityType<Tile16Marker> TYPE16 = TileEntityType.Builder.create(Tile16Marker::new, block16Marker).build(DSL.nilType());
        public static final ContainerType<ContainerMarker> CONTAINER_TYPE = IForgeContainerType.create((windowId, inv, data) ->
            new ContainerMarker(windowId, inv.player, data.readBlockPos(), ModObjects.CONTAINER_TYPE));
        public static final ContainerType<ContainerMarker> CONTAINER16_TYPE = IForgeContainerType.create((windowId, inv, data) ->
            new ContainerMarker(windowId, inv.player, data.readBlockPos(), ModObjects.CONTAINER16_TYPE));
        public static final RemoteControlItem remoteControlItem = new RemoteControlItem();
    }

    public static final class Group extends ItemGroup {
        public Group() {
            super(modID);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModObjects.blockMarker);
        }
    }
}
