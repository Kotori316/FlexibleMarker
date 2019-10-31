package com.kotori316.marker.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;

import com.kotori316.marker.Marker;

class Recipe extends RecipeProvider {
    public Recipe(DataGenerator p_i48262_1_) {
        super(p_i48262_1_);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> p_200404_1_) {
        IFinishedRecipe marker = new RecipeWrpper(getConsumerValue(ShapedRecipeBuilder.shapedRecipe(Objects.requireNonNull(Marker.blockMarker.get()))
            .patternLine("E")
            .patternLine("T")
            .key('E', Tags.Items.GEMS_EMERALD)
            .key('T', Items.REDSTONE_TORCH)
            .addCriterion("has_rs_torch", hasItem(Items.REDSTONE_TORCH))))
            .addCondition(new NotCondition(new ModLoadedCondition("buildcraftcore")));
        p_200404_1_.accept(marker);
        ShapedRecipeBuilder.shapedRecipe(Objects.requireNonNull(Marker.block16Marker.get()))
            .patternLine("E")
            .patternLine("T")
            .key('E', Tags.Items.DUSTS_REDSTONE)
            .key('T', Marker.blockMarker.get())
            .addCriterion("has_marker", hasItem(Marker.blockMarker.get()))
            .build(p_200404_1_);
    }

    private static IFinishedRecipe getConsumerValue(ShapedRecipeBuilder builder) {
        AtomicReference<IFinishedRecipe> recipe = new AtomicReference<>();
        builder.build(recipe::set);
        return recipe.get();
    }

    private static class RecipeWrpper implements IFinishedRecipe {
        private final IFinishedRecipe recipe;
        private final List<ICondition> conditions = new ArrayList<>();

        private RecipeWrpper(IFinishedRecipe recipe) {
            this.recipe = recipe;
        }

        @Override
        public void serialize(JsonObject jsonObject) {
            recipe.serialize(jsonObject);
        }

        @Override
        public JsonObject getRecipeJson() {
            JsonObject object = recipe.getRecipeJson();
            if (!conditions.isEmpty()) {
                JsonArray conds = conditions.stream().map(CraftingHelper::serialize)
                    .reduce(new JsonArray(),
                        (a, o) -> {
                            a.add(o);
                            return a;
                        },
                        (a1, a2) -> {
                            a1.addAll(a2);
                            return a1;
                        });
                object.add("conditions", conds);
            }
            return object;
        }

        @Override
        public ResourceLocation getID() {
            return recipe.getID();
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return recipe.getSerializer();
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return recipe.getAdvancementJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return recipe.getAdvancementID();
        }

        public RecipeWrpper addCondition(ICondition condition) {
            this.conditions.add(condition);
            return this;
        }
    }
}
