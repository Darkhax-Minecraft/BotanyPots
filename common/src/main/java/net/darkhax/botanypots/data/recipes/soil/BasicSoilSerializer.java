package net.darkhax.botanypots.data.recipes.soil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.darkhax.bookshelf.api.data.codecs.BookshelfCodecs;
import net.darkhax.botanypots.data.displaystate.DisplayTypes;
import net.darkhax.botanypots.data.displaystate.types.DisplayState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public final class BasicSoilSerializer implements RecipeSerializer<BasicSoil> {

    public static BasicSoilSerializer SERIALIZER = new BasicSoilSerializer();

    public static Codec<BasicSoil> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BookshelfCodecs.INGREDIENT_NONEMPTY.get("input", BasicSoil::getIngredient),
            DisplayTypes.DISPLAY_STATE_CODEC.get("display", BasicSoil::getDisplayState),
            BookshelfCodecs.FLOAT.get("growthModifier", BasicSoil::getGrowthModifier, 1f),
            BookshelfCodecs.STRING.getSet("categories", BasicSoil::getCategories),
            BookshelfCodecs.INT.get("lightLevel", BasicSoil::getLightLevel, 0)
    ).apply(instance, BasicSoil::new));

    @Override
    public Codec<BasicSoil> codec() {
        return CODEC;
    }

    @Override
    public BasicSoil fromNetwork(FriendlyByteBuf buffer) {

        final Ingredient ingredient = BookshelfByteBufs.INGREDIENT.read(buffer);
        final DisplayState renderState = DisplayTypes.DISPLAY_STATE_BUFFER.read(buffer);
        final float growthModifier = BookshelfByteBufs.FLOAT.read(buffer);
        final Set<String> categories = new HashSet<>(BookshelfByteBufs.STRING.readList(buffer));
        final int lightLevel = BookshelfByteBufs.INT.read(buffer);

        return new BasicSoil(ingredient, renderState, growthModifier, categories, lightLevel);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, BasicSoil soilInfo) {

        BookshelfByteBufs.INGREDIENT.write(buffer, soilInfo.ingredient);
        DisplayTypes.DISPLAY_STATE_BUFFER.write(buffer, soilInfo.displayState);
        BookshelfByteBufs.FLOAT.write(buffer, soilInfo.growthModifier);
        BookshelfByteBufs.STRING.writeList(buffer, new ArrayList<>(soilInfo.categories));
        BookshelfByteBufs.INT.write(buffer, soilInfo.lightLevel);
    }
}