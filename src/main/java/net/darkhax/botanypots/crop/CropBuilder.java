package net.darkhax.botanypots.crop;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.darkhax.bookshelf.serialization.SerializerBlockState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CropBuilder {
    private String group;
    private Ingredient seed;
    private final List<String> categories = Lists.newArrayList();
    private int growthTicks;
    private final List<BlockState> display = Lists.newArrayList();
    private final List<HarvestEntry> results = Lists.newArrayList();
    private String modid;

    public static CropBuilder create() {
        return new CropBuilder();
    }

    public CropBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    public CropBuilder setSeed(IItemProvider item) {
        this.seed = Ingredient.fromItems(item);
        return this;
    }

    public CropBuilder setSeed(ITag<Item> tag) {
        this.seed = Ingredient.fromTag(tag);
        return this;
    }

    public CropBuilder setSeed(Ingredient ingredient) {
        this.seed = ingredient;
        return this;
    }

    public CropBuilder addVisibleBlocks(Block block) {
        this.display.add(block.getDefaultState());
        return this;
    }

    public CropBuilder addVisibleBlocks(Block... blocks) {
        for (Block block : blocks) {
            this.display.add(block.getDefaultState());
        }
        return this;
    }

    public CropBuilder addVisibleBlocks(BlockState state) {
        this.display.add(state);
        return this;
    }

    public CropBuilder addVisibleBlocks(BlockState... states) {
        this.display.addAll(Arrays.asList(states));
        return this;
    }

    public CropBuilder setGrowthTicks(int ticks) {
        this.growthTicks = ticks;
        return this;
    }

    public CropBuilder addCategories(String... categories) {
        for (String category : categories) {
            this.addCategories(category);
        }
        return this;
    }

    public CropBuilder addCategories(String category) {
        this.categories.add(category);
        return this;
    }

    public CropBuilder addResults(HarvestEntry... categories) {
        for (HarvestEntry category : categories) {
            this.addResults(category);
        }
        return this;
    }

    public CropBuilder addResults(HarvestEntry category) {
        this.results.add(category);
        return this;
    }

    public CropBuilder setModID(String modid) {
        this.modid = modid;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        this.validate(id);
        consumer.accept(new CropBuilder.FinishedRecipe(id, this.seed, this.categories, this.growthTicks, this.display,
                this.results, this.group == null ? "" : this.group));
    }

    private void validate(ResourceLocation id) {
        if (this.seed == null) {
            throw new IllegalArgumentException("No seed set for " + id);
        }
        if (this.display.isEmpty()) {
            throw new IllegalArgumentException("No display block set for " + id);
        }
        if (this.growthTicks <= 0) {
            throw new IllegalArgumentException("No growth time set at " + id);
        }
        if (this.results.isEmpty()) {
            throw new IllegalArgumentException("No results set in " + id);
        }
    }

    private static class FinishedRecipe implements IFinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient seed;
        private final List<String> categories;
        private final int growthTicks;
        private final List<BlockState> display;
        private final List<HarvestEntry> results;
        private final String group;

        private FinishedRecipe(ResourceLocation id, Ingredient seed, List<String> categories, int ticks, List<BlockState> display, List<HarvestEntry> results, String group) {
            this.id = id;
            this.seed = seed;
            this.categories = categories;
            this.growthTicks = ticks;
            this.display = display;
            this.results = results;
            this.group = group;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            json.add("seed", this.seed.serialize());
            JsonArray categories = new JsonArray();
            for (String category : this.categories) {
                categories.add(category);
            }
            if (this.display.size() == 1) {
                json.add("display", SerializerBlockState.SERIALIZER.write(this.display.get(0)));
            } else {
                JsonArray displays = new JsonArray();
                for (BlockState state : this.display) {
                    displays.add(SerializerBlockState.SERIALIZER.write(state));
                }
                json.add("display", displays);
            }
            JsonArray results = new JsonArray();
            for (HarvestEntry entry : this.results) {
                JsonObject jsonEntry = new JsonObject();
                jsonEntry.addProperty("chance", entry.getChance());
                jsonEntry.add("output", serializeStack(entry.getItem()));
                jsonEntry.addProperty("minRolls", entry.getMinRolls());
                jsonEntry.addProperty("maxRolls", entry.getMaxRolls());
                results.add(jsonEntry);
            }

            json.add("categories", categories);
            json.addProperty("growthTicks", this.growthTicks);
            json.add("results", results);
        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return CropSerializer.INSTANCE;
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

        private static JsonObject serializeStack(ItemStack stack) {
            CompoundNBT nbt = stack.write(new CompoundNBT());
            byte count = nbt.getByte("Count");
            if (count != 1) {
                nbt.putByte("count", count);
            }
            nbt.remove("Count");
            renameTag(nbt, "id", "item");
            renameTag(nbt, "tag", "nbt");
            Dynamic<INBT> dyn = new Dynamic<>(NBTDynamicOps.INSTANCE, nbt);
            return dyn.convert(JsonOps.INSTANCE).getValue().getAsJsonObject();
        }

        private static void renameTag(CompoundNBT nbt, String oldName, String newName) {
            INBT tag = nbt.get(oldName);
            if (tag != null) {
                nbt.remove(oldName);
                nbt.put(newName, tag);
            }
        }
    }
}
