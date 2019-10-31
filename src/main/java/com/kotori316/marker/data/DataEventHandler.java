package com.kotori316.marker.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import com.kotori316.marker.Marker;

@Mod.EventBusSubscriber(modid = Marker.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataEventHandler {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
//        ExistingFileHelper helper = event.getExistingFileHelper();
        if (event.includeServer()) {
            generator.addProvider(new Recipe(generator));
            generator.addProvider(new Loot(generator));
        }
        if (event.includeClient()) {
            generator.addProvider(new Lang.LangUS(generator));
            generator.addProvider(new Lang.LangJP(generator));
        }
    }
}
