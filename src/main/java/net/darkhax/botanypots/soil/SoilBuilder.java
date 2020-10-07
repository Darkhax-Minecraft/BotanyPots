package net.darkhax.botanypots.soil;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.darkhax.bookshelf.serialization.SerializerBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class SoilBuilder {
    private String group;
    private Ingredient input;
    private BlockState display;
    private final List<String> categories = Lists.newArrayList();
    private float growthModifier;
    private String modid;

    public static SoilBuilder create() {
        return new SoilBuilder();
    }

    public SoilBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    public SoilBuilder setInput(IItemProvider item) {
        this.input = Ingredient.fromItems(item.asItem());
        return this;
    }

    public SoilBuilder setInput(ITag<Item> tag) {
        this.input = Ingredient.fromTag(tag);
        return this;
    }

    public SoilBuilder setInput(Ingredient ingredient) {
        this.input = ingredient;
        return this;
    }

    public SoilBuilder setVisibleBlock(Block block) {
        this.display = block.getDefaultState();
        return this;
    }

    public SoilBuilder setVisibleBlock(BlockState state) {
        this.display = state;
        return this;
    }

    public SoilBuilder addCategory(String... categories) {
        for (String category : categories) {
            this.addCategory(category);
        }
        return this;
    }

    public SoilBuilder addCategory(String category) {
        this.categories.add(category);
        return this;
    }

    public SoilBuilder setGrowthModifier(float modifier) {
        this.growthModifier = modifier;
        return this;
    }

    public SoilBuilder setModID(String modid) {
        this.modid = modid;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        this.validate(id);
        consumer.accept(new FinishedRecipe(id, this.input, this.display, this.growthModifier, this.categories, this.group == null ? "" : this.group));
    }

    private void validate(ResourceLocation id) {
        if (this.input == null) {
            throw new IllegalArgumentException("No input set for " + id);
        }
        if (this.display == null) {
            throw new IllegalArgumentException("No display block set for " + id);
        }
        if (this.growthModifier <= -1) {
            throw new IllegalArgumentException("Soil " + id + " has an invalid growth modifier. It must be greater than -1.");
        }
    }

    private static class FinishedRecipe implements IFinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient input;
        private final BlockState display;
        private final float growthModifier;
        private final List<String> categories;
        private final String group;

        private FinishedRecipe(ResourceLocation id, Ingredient input, BlockState display, float growthModifier, List<String> categories, String group) {
            this.id = id;
            this.input = input;
            this.display = display;
            this.growthModifier = growthModifier;
            this.categories = categories;
            this.group = group;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            json.add("input", this.input.serialize());
            JsonObject display = new JsonObject();
            display.add("properties", SerializerBlockState.SERIALIZER.write(this.display));
            JsonArray categories = new JsonArray();
            for (String category : this.categories) {
                categories.add(category);
            }

            json.add("display", display);
            json.add("categories", categories);
            json.addProperty("growthModifier", this.growthModifier);
        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return SoilSerializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject getAdvancementJson() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementID() {
            return null;
        }
    }
}
