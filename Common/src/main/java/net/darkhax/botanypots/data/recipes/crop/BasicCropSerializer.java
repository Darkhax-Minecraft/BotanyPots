package net.darkhax.botanypots.data.recipes.crop;

import com.google.gson.JsonObject;
import net.darkhax.bookshelf.api.data.recipes.IRecipeSerializer;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Set;

public final class BasicCropSerializer extends IRecipeSerializer<BasicCrop> {

    public static BasicCropSerializer SERIALIZER = new BasicCropSerializer();

    @Override
    public BasicCrop fromJson(ResourceLocation id, JsonObject json) {

        if (id.getNamespace().equalsIgnoreCase("farmersdelight")) {

            Constants.LOG.warn("Crop {} has been disabled by the BotanyPot devs to improve compatibility.", id);
            return null;
        }

        final Ingredient seed = Serializers.INGREDIENT.fromJSON(json, "seed");
        final Set<String> validSoils = Serializers.STRING.fromJSONSet(json, "categories");
        final int growthTicks = Serializers.INT.fromJSON(json, "growthTicks");
        final List<HarvestEntry> results = SerializerHarvestEntry.SERIALIZER.fromJSONList(json, "drops");
        final int lightLevel = Serializers.INT.fromJSON(json, "lightLevel", 0);
        final List<DisplayState> states = DisplayState.SERIALIZER.fromJSONList(json, "display");

        if (growthTicks <= 0) {

            throw new IllegalArgumentException("Crop " + id + " has an invalid growth tick rate. It must use a positive integer.");
        }

        return new BasicCrop(id, seed, validSoils, growthTicks, results, states, lightLevel);
    }

    @Override
    public BasicCrop fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

        final Ingredient seed = Serializers.INGREDIENT.fromByteBuf(buffer);
        final Set<String> validSoils = Serializers.STRING.readByteBufSet(buffer);
        final int growthTicks = Serializers.INT.fromByteBuf(buffer);
        final List<HarvestEntry> results = SerializerHarvestEntry.SERIALIZER.fromByteBufList(buffer);
        final List<DisplayState> displayStates = DisplayState.SERIALIZER.fromByteBufList(buffer);
        final int lightLevel = Serializers.INT.fromByteBuf(buffer);

        return new BasicCrop(id, seed, validSoils, growthTicks, results, displayStates, lightLevel);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, BasicCrop toWrite) {

        Serializers.INGREDIENT.toByteBuf(buffer, toWrite.seed);
        Serializers.STRING.writeByteBufSet(buffer, toWrite.soilCategories);
        Serializers.INT.toByteBuf(buffer, toWrite.growthTicks);
        SerializerHarvestEntry.SERIALIZER.toByteBufList(buffer, toWrite.results);
        DisplayState.SERIALIZER.toByteBufList(buffer, toWrite.displayStates);
        Serializers.INT.toByteBuf(buffer, toWrite.lightLevel);
    }
}
