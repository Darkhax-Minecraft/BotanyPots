package net.darkhax.botanypots.data.soil;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.darkhax.botanypots.tempshelf.DisplayableBlockState;
import net.darkhax.bookshelf.api.data.recipes.IRecipeSerializer;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class SoilRecipeSerializer extends IRecipeSerializer<SoilInfo> {

    public static SoilRecipeSerializer SERIALIZER = new SoilRecipeSerializer();

    @Override
    public SoilInfo fromJson(ResourceLocation id, JsonObject json) {

        final Ingredient input = Serializers.INGREDIENT.fromJSON(json, "input");
        final DisplayableBlockState renderState = null; // TODO
        final float growthModifier = Serializers.FLOAT.fromJSON(json, "growthModifier", 1f);
        final Set<String> categories = Serializers.STRING.fromJSONSet(json, "categories");
        final int lightLevel = Serializers.INT.fromJSON(json, "lightLevel", 0);

        if (growthModifier <= -1) {

            throw new JsonParseException("Soil " + id + " has an invalid growth modifier. It must be greater than -1. Growth modifier was " + growthModifier);
        }

        if (lightLevel > 15 || lightLevel < 0) {

            throw new JsonParseException("Soil " + id + " has an invalid light level. Light levels must be between 0 and 15. Light level was " + lightLevel);
        }

        return new SoilInfo(id, input, renderState, growthModifier, categories, lightLevel);
    }

    @Override
    public SoilInfo fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

        final Ingredient ingredient = Serializers.INGREDIENT.fromByteBuf(buffer);
        final DisplayableBlockState renderState = null; // TODO
        final float growthModifier = Serializers.FLOAT.fromByteBuf(buffer);
        final Set<String> categories = new HashSet<>(Serializers.STRING.fromByteBufList(buffer));
        final int lightLevel = Serializers.INT.fromByteBuf(buffer);

        return new SoilInfo(id, ingredient, renderState, growthModifier, categories, lightLevel);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, SoilInfo soilInfo) {

        Serializers.INGREDIENT.toByteBuf(buffer, soilInfo.getIngredient());
        // TODO write block state
        Serializers.FLOAT.toByteBuf(buffer, soilInfo.getGrowthModifier());
        Serializers.STRING.toByteBufList(buffer, new ArrayList<>(soilInfo.getCategories()));
    }
}