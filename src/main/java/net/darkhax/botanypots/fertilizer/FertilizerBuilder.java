package net.darkhax.botanypots.fertilizer;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class FertilizerBuilder {
    private String group;
    private Ingredient fertilizer;
    private int minTicks;
    private int maxTicks;

    public static FertilizerBuilder create() {
        return new FertilizerBuilder();
    }

    public FertilizerBuilder setGroup(String group) {
        this.group = group;
        return this;
    }

    public FertilizerBuilder setFertilizer(IItemProvider item) {
        this.fertilizer = Ingredient.fromItems(item);
        return this;
    }

    public FertilizerBuilder setFertilizer(ITag<Item> tag) {
        this.fertilizer = Ingredient.fromTag(tag);
        return this;
    }

    public FertilizerBuilder setFertilizer(Ingredient ingredient) {
        this.fertilizer = ingredient;
        return this;
    }

    public FertilizerBuilder setMinTicks(int ticks) {
        this.minTicks = ticks;
        return this;
    }

    public FertilizerBuilder setMaxTicks(int ticks) {
        this.maxTicks = ticks;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        this.validate(id);
        consumer.accept(new FinishedRecipe(id, this.fertilizer, this.minTicks, this.maxTicks, this.group == null ? "" : this.group));
    }

    private void validate(ResourceLocation id) {
        if (this.fertilizer == null) {
            throw new IllegalArgumentException("No fertilizer set for " + id);
        }
        if (this.minTicks >= this.maxTicks) {
            throw new IllegalArgumentException("Max ticks needs to be higher than min ticks at " + id);
        }
    }

    private static class FinishedRecipe implements IFinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient fertilizer;
        private final int minTicks;
        private final int maxTicks;
        private final String group;

        private FinishedRecipe(ResourceLocation id, Ingredient fertilizer, int minTicks, int maxTicks, String group) {
            this.id = id;
            this.fertilizer = fertilizer;
            this.minTicks = minTicks;
            this.maxTicks = maxTicks;
            this.group = group;
        }

        @Override
        public void serialize(JsonObject json) {
            if (!this.group.isEmpty()) {
                json.addProperty("group", this.group);
            }
            json.add("fertilizer", this.fertilizer.serialize());
            json.addProperty("minTicks", this.minTicks);
            json.addProperty("maxTicks", this.maxTicks);
        }

        @Override
        public ResourceLocation getID() {
            return this.id;
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            return FertilizerSerializer.INSTANCE;
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
