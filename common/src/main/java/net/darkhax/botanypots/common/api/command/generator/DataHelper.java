package net.darkhax.botanypots.common.api.command.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class DataHelper {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final DefaultedRegistry<Block> BLOCKS = BuiltInRegistries.BLOCK;
    public static final DefaultedRegistry<Item> ITEMS = BuiltInRegistries.ITEM;

    public static JsonObject requiresBlock(Block block) {
        return object("{\"type\":\"bookshelf:block_exists\",\"values\": [\"" + BLOCKS.getKey(block) + "\"]}");
    }

    public static JsonObject requiresItem(Item item) {
        return object("{\"type\":\"bookshelf:item_exists\",\"values\": [\"" + ITEMS.getKey(item) + "\"]}");
    }

    public static JsonObject item(Item item) {
        return object("{\"item\":\"" + ITEMS.getKey(item) + "\"}");
    }

    public static JsonObject tag(TagKey<?> tag) {
        return object("{\"tag\":\"" + tag.location() + "\"}");
    }

    public static JsonElement stack(ItemStack stack) {
        return encode(ItemStack.CODEC, stack);
    }

    public static JsonElement ingredient(Ingredient ingredient) {
        return encode(Ingredient.CODEC, ingredient);
    }

    public static JsonObject simpleDisplay(Block block) {
        return simpleDisplay(block, false);
    }

    public static JsonObject simpleDisplay(Block block, boolean fluid) {
        final JsonObject out = new JsonObject();
        out.addProperty("type", "botanypots:simple");
        out.add("block_state", object("{\"block\":\"" + BLOCKS.getKey(block) + "\"}"));
        if (fluid) {
            out.add("options", object("{\"render_fluid\":true}"));
        }
        return out;
    }

    public static JsonArray array(JsonElement... elements) {
        final JsonArray array = new JsonArray();
        for (JsonElement element : elements) {
            array.add(element);
        }
        return array;
    }

    public static JsonObject object(String raw) {
        return GSON.fromJson(raw, JsonObject.class);
    }

    public static <T> JsonElement encode(Codec<T> codec, T value) {
        return codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
    }
}