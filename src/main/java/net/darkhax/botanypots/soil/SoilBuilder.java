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
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class SoilBuilder {
    private String group;
    private Item input;
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

    public SoilBuilder setInput(Block block) {
        this.input = block.asItem();
        return this;
    }

    public SoilBuilder setInput(Item item) {
        this.input = item;
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

    public void build(Consumer<IFinishedRecipe> consumer) {
        this.build(consumer, this.modid == null ? this.input.getRegistryName() : new ResourceLocation(this.modid, this.input.getRegistryName().getPath()));
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
        private final Item input;
        private final BlockState display;
        private final float growthModifier;
        private final List<String> categories;
        private final String group;

        private FinishedRecipe(ResourceLocation id, Item input, BlockState display, float growthModifier, List<String> categories, String group) {
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
            JsonObject input = new JsonObject();
            input.addProperty("item", this.input.getRegistryName().toString());
            JsonObject display = new JsonObject();
            display.addProperty("block", this.display.getBlock().asItem().getRegistryName().toString());
            if (this.display != this.display.getBlock().getDefaultState()) {
                display.add("properties", SerializerBlockState.SERIALIZER.write(this.display));
            }
            JsonArray categories = new JsonArray();
            for (String category : this.categories) {
                categories.add(category);
            }

            json.add("input", input);
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
