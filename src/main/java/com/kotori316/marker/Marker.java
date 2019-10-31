package com.kotori316.marker;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, modID);
    public static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, modID);
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, modID);

    public static final RegistryObject<BlockMarker> blockMarker = BLOCKS.register(BlockMarker.BlockFlexMarker.NAME, BlockMarker.BlockFlexMarker::new);
    public static final RegistryObject<BlockMarker> block16Marker = BLOCKS.register(BlockMarker.Block16Marker.NAME, BlockMarker.Block16Marker::new);
    public static final RegistryObject<TileEntityType<TileFlexMarker>> TYPE = TILES.register("flexiblemarker", () -> TileEntityType.Builder.create(TileFlexMarker::new, blockMarker.get()).build(null));
    public static final RegistryObject<TileEntityType<Tile16Marker>> TYPE16 = TILES.register(BlockMarker.Block16Marker.NAME, () -> TileEntityType.Builder.create(Tile16Marker::new, block16Marker.get()).build(null));
    public static final ContainerType<ContainerMarker> CONTAINER_TYPE = IForgeContainerType.create((windowId, inv, data) ->
        new ContainerMarker(windowId, inv.player, data.readBlockPos(), Marker.CONTAINER_TYPE));
    public static final ContainerType<ContainerMarker> CONTAINER16_TYPE = IForgeContainerType.create((windowId, inv, data) ->
        new ContainerMarker(windowId, inv.player, data.readBlockPos(), Marker.CONTAINER16_TYPE));

    static {
        ITEMS.register(BlockMarker.BlockFlexMarker.NAME, () -> new BlockItem(Objects.requireNonNull(blockMarker.get()), new Item.Properties().group(ItemGroup.REDSTONE)));
        ITEMS.register(BlockMarker.Block16Marker.NAME, () -> new BlockItem(Objects.requireNonNull(block16Marker.get()), new Item.Properties().group(ItemGroup.REDSTONE)));
    }

    public Marker() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);
        BLOCKS.register(modEventBus);
        TILES.register(modEventBus);
        ITEMS.register(modEventBus);
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
        ScreenManager.registerFactory(CONTAINER_TYPE, GuiMarker::new);
        ScreenManager.registerFactory(CONTAINER16_TYPE, Gui16Marker::new);
    }

    @SubscribeEvent
    public void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(CONTAINER_TYPE.setRegistryName(BlockMarker.GUI_ID));
        event.getRegistry().register(CONTAINER16_TYPE.setRegistryName(BlockMarker.GUI16_ID));
    }
}
