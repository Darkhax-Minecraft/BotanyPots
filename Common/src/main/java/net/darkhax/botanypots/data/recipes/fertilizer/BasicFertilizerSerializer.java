package net.darkhax.botanypots.data.recipes.fertilizer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.darkhax.bookshelf.api.data.recipes.IRecipeSerializer;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public final class BasicFertilizerSerializer extends IRecipeSerializer<BasicFertilizer> {

    public static final IRecipeSerializer<?> SERIALIZER = new BasicFertilizerSerializer();

    @Override
    public BasicFertilizer fromJson(ResourceLocation id, JsonObject json) {

        final Ingredient ingredient = Serializers.INGREDIENT.fromJSON(json, "ingredient");
        final Ingredient cropIngredient = Serializers.INGREDIENT.fromJSONNullable(json, "crop_ingredient");
        final Ingredient soilIngredient = Serializers.INGREDIENT.fromJSONNullable(json, "soil_ingredient");
        final int minTicks = Serializers.INT.fromJSON(json, "min_growth");
        final int maxTicks = Serializers.INT.fromJSON(json, "max_growth");

        if (minTicks < 0 || maxTicks < 0) {

            throw new JsonParseException("Growth ticks must be greater than 0! min=" + minTicks + " max=" + maxTicks);
        }

        if (minTicks > maxTicks) {

            throw new JsonParseException("Min growth ticks must not be greater than max ticks.  min=" + minTicks + " max=" + maxTicks);
        }

        return new BasicFertilizer(id, ingredient, cropIngredient, soilIngredient, minTicks, maxTicks);
    }

    @Override
    public BasicFertilizer fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

        final Ingredient ingredient = Serializers.INGREDIENT.fromByteBuf(buffer);
        final Ingredient cropIngredient = Serializers.INGREDIENT.fromByteBufNullable(buffer);
        final Ingredient soilIngredient = Serializers.INGREDIENT.fromByteBufNullable(buffer);
        final int minTicks = Serializers.INT.fromByteBuf(buffer);
        final int maxTicks = Serializers.INT.fromByteBuf(buffer);

        return new BasicFertilizer(id, ingredient, cropIngredient, soilIngredient, minTicks, maxTicks);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, BasicFertilizer toWrite) {

        Serializers.INGREDIENT.toByteBuf(buffer, toWrite.ingredient);
        Serializers.INGREDIENT.toByteBufNullable(buffer, toWrite.cropIngredient);
        Serializers.INGREDIENT.toByteBufNullable(buffer, toWrite.soilIngredient);
        Serializers.INT.toByteBuf(buffer, toWrite.minTicks);
        Serializers.INT.toByteBuf(buffer, toWrite.maxTicks);
    }
}
