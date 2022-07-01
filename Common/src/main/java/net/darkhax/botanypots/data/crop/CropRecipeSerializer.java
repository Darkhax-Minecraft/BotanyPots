package net.darkhax.botanypots.data.crop;

import com.google.gson.JsonObject;
import net.darkhax.botanypots.tempshelf.DisplayableBlockState;
import net.darkhax.bookshelf.api.data.recipes.IRecipeSerializer;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Set;

public final class CropRecipeSerializer extends IRecipeSerializer<CropInfo> {

    public static CropRecipeSerializer SERIALIZER = new CropRecipeSerializer();

    @Override
    public CropInfo fromJson(ResourceLocation id, JsonObject json) {

        final Ingredient seed = Serializers.INGREDIENT.fromJSON(json, "seed");
        final Set<String> validSoils = Serializers.STRING.fromJSONSet(json, "categories");
        final int growthTicks = Serializers.INT.fromJSON(json, "growthTicks");
        final List<HarvestEntry> results = SerializerHarvestEntry.SERIALIZER.fromJSONList(json, "drops");
        final int lightLevel = Serializers.INT.fromJSON(json, "lightLevel", 0);
        final DisplayableBlockState[] states = null; // TODO

        if (growthTicks <= 0) {

            throw new IllegalArgumentException("Crop " + id + " has an invalid growth tick rate. It must use a positive integer.");
        }

        return new CropInfo(id, seed, validSoils, growthTicks, results, states, lightLevel);
    }

    @Override
    public CropInfo fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

        final Ingredient seed = Serializers.INGREDIENT.fromByteBuf(buffer);
        final Set<String> validSoils = Serializers.STRING.readByteBufSet(buffer);
        final int growthTicks = Serializers.INT.fromByteBuf(buffer);
        final List<HarvestEntry> results = SerializerHarvestEntry.SERIALIZER.fromByteBufList(buffer);
        final DisplayableBlockState[] displayStates = null; // TODO
        final int lightLevel = Serializers.INT.fromByteBuf(buffer);

        return new CropInfo(id, seed, validSoils, growthTicks, results, displayStates, lightLevel);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, CropInfo toWrite) {

        Serializers.INGREDIENT.toByteBuf(buffer, toWrite.getSeed());
        Serializers.STRING.writeByteBufSet(buffer, toWrite.getSoilCategories());
        Serializers.INT.toByteBuf(buffer, toWrite.getGrowthTicks());
        SerializerHarvestEntry.SERIALIZER.toByteBufList(buffer, toWrite.getResults());
        // TOOD write block states
        Serializers.INT.toByteBuf(buffer, toWrite.getLightLevel());
    }
}
