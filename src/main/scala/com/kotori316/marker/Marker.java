package com.kotori316.marker;

import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.kotori316.marker.gui.GuiHandler;
import com.kotori316.marker.packet.PacketHandler;
import com.kotori316.marker.render.RenderMarker;

@Mod(modid = Marker.modID, name = Marker.ModName, version = "${version}", certificateFingerprint = "@FINGERPRINT@")
public class Marker {
    public static final String modID = "flexiblemarker";
    public static final String ModName = "FlexibleMarker";
    public static final BlockMarker blockMarker = new BlockMarker.BlockFlexMarker();
    private static final Marker instance;

    static {
        instance = new Marker();
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(getInstance());
        NetworkRegistry.INSTANCE.registerGuiHandler(getInstance(), new GuiHandler());
        PacketHandler.init();
        if (event.getSide() == Side.CLIENT) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileFlexMarker.class, RenderMarker.getInstance());
        }
    }

    @Mod.InstanceFactory
    public static Marker getInstance() {
        return instance;
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(blockMarker);
        TileEntity.register(modID + ":flexiblemarker", TileFlexMarker.class);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(blockMarker.itemBlock);
    }


    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(blockMarker.itemBlock, 0,
            new ModelResourceLocation(Objects.requireNonNull(blockMarker.getRegistryName()), "inventory"));
    }
}
