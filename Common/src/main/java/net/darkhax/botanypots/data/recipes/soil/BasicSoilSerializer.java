package net.darkhax.botanypots.data.recipes.soil;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.darkhax.bookshelf.api.data.recipes.IRecipeSerializer;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class BasicSoilSerializer extends IRecipeSerializer<BasicSoil> {

    public static BasicSoilSerializer SERIALIZER = new BasicSoilSerializer();

    @Override
    public BasicSoil fromJson(ResourceLocation id, JsonObject json) {

        if (id.getNamespace().equalsIgnoreCase("farmersdelight")) {

            Constants.LOG.warn("Soil {} has been disabled by the BotanyPot devs to improve compatibility.", id);
            return null;
        }

        final Ingredient input = Serializers.INGREDIENT.fromJSON(json, "input");
        final DisplayState renderState = DisplayState.SERIALIZER.fromJSON(json, "display");
        final float growthModifier = Serializers.FLOAT.fromJSON(json, "growthModifier", 1f);
        final Set<String> categories = Serializers.STRING.fromJSONSet(json, "categories");
        final int lightLevel = Serializers.INT.fromJSON(json, "lightLevel", 0);

        if (growthModifier <= -1) {

            throw new JsonParseException("Soil " + id + " has an invalid growth modifier. It must be greater than -1. Growth modifier was " + growthModifier);
        }

        if (lightLevel > 15 || lightLevel < 0) {

            throw new JsonParseException("Soil " + id + " has an invalid light level. Light levels must be between 0 and 15. Light level was " + lightLevel);
        }

        return new BasicSoil(id, input, renderState, growthModifier, categories, lightLevel);
    }

    @Override
    public BasicSoil fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

        final Ingredient ingredient = Serializers.INGREDIENT.fromByteBuf(buffer);
        final DisplayState renderState = DisplayState.SERIALIZER.fromByteBuf(buffer);
        final float growthModifier = Serializers.FLOAT.fromByteBuf(buffer);
        final Set<String> categories = new HashSet<>(Serializers.STRING.fromByteBufList(buffer));
        final int lightLevel = Serializers.INT.fromByteBuf(buffer);

        return new BasicSoil(id, ingredient, renderState, growthModifier, categories, lightLevel);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, BasicSoil soilInfo) {

        Serializers.INGREDIENT.toByteBuf(buffer, soilInfo.ingredient);
        DisplayState.SERIALIZER.toByteBuf(buffer, soilInfo.displayState);
        Serializers.FLOAT.toByteBuf(buffer, soilInfo.growthModifier);
        Serializers.STRING.toByteBufList(buffer, new ArrayList<>(soilInfo.categories));
        Serializers.INT.toByteBuf(buffer, soilInfo.lightLevel);
    }
}