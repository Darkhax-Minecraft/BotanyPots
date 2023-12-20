package net.darkhax.botanypots.data.recipes.fertilizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public final class BasicFertilizerSerializer implements RecipeSerializer<BasicFertilizer> {

    public static final RecipeSerializer<?> SERIALIZER = new BasicFertilizerSerializer();

    private static final Codec<BasicFertilizer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(BasicFertilizer::getIngredient),
            Ingredient.CODEC_NONEMPTY.optionalFieldOf("crop_ingredient").forGetter(BasicFertilizer::getCropIngredient),
            Ingredient.CODEC_NONEMPTY.optionalFieldOf("soil_ingredient").forGetter(BasicFertilizer::getSoilIngredient),
            Codec.INT.fieldOf("min_growth").forGetter(BasicFertilizer::getMinTicks),
            Codec.INT.fieldOf("max_growth").forGetter(BasicFertilizer::getMaxTicks)
    ).apply(instance, BasicFertilizer::new));

    @Override
    public Codec<BasicFertilizer> codec() {

        return CODEC;
    }

    @Override
    public BasicFertilizer fromNetwork(FriendlyByteBuf buffer) {

        final Ingredient ingredient = BookshelfByteBufs.INGREDIENT.read(buffer);
        final Optional<Ingredient> cropIngredient = BookshelfByteBufs.INGREDIENT.readOptional(buffer);
        final Optional<Ingredient> soilIngredient = BookshelfByteBufs.INGREDIENT.readOptional(buffer);
        final int minTicks = BookshelfByteBufs.INT.read(buffer);
        final int maxTicks = BookshelfByteBufs.INT.read(buffer);

        return new BasicFertilizer(ingredient, cropIngredient, soilIngredient, minTicks, maxTicks);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, BasicFertilizer toWrite) {

        BookshelfByteBufs.INGREDIENT.write(buffer, toWrite.ingredient);
        BookshelfByteBufs.INGREDIENT.writeOptional(buffer, toWrite.cropIngredient);
        BookshelfByteBufs.INGREDIENT.writeOptional(buffer, toWrite.soilIngredient);
        BookshelfByteBufs.INT.write(buffer, toWrite.minTicks);
        BookshelfByteBufs.INT.write(buffer, toWrite.maxTicks);
    }
}
