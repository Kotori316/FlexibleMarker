package com.kotori316.marker.data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootParameterSet;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraft.world.storage.loot.ValidationResults;

import com.kotori316.marker.Marker;

public class Loot extends LootTableProvider {
    public Loot(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(
            Pair.of(Blocks::new, LootParameterSets.BLOCK)
        );
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationResults validationresults) {
        map.forEach((name, table) -> LootTableManager.func_215302_a(validationresults, name, table, map::get));
    }

    private static class Blocks extends BlockLootTables {
        @Override
        protected void addTables() {
            this.func_218492_c(Marker.blockMarker);
            this.func_218492_c(Marker.block16Marker);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Arrays.asList(Marker.blockMarker, Marker.block16Marker);
        }
    }
}
