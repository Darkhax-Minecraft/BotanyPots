package net.darkhax.botanypots.data.recipes.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.darkhax.bookshelf.api.data.codecs.BookshelfCodecs;
import net.darkhax.botanypots.data.displaystate.DisplayTypes;
import net.darkhax.botanypots.data.displaystate.types.DisplayState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.List;
import java.util.Set;

public final class BasicCropSerializer implements RecipeSerializer<BasicCrop> {

    public static final Codec<BasicCrop> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BookshelfCodecs.INGREDIENT_NONEMPTY.get("seed", BasicCrop::getSeed),
            BookshelfCodecs.STRING.getSet("categories", BasicCrop::getSoilCategories),
            BookshelfCodecs.INT.get("growthTicks", BasicCrop::getGrowthTicks),
            HarvestEntry.CODEC.getList("drops", BasicCrop::getResults),
            DisplayTypes.DISPLAY_STATE_CODEC.getList("display", BasicCrop::getDisplayStates),
            BookshelfCodecs.INT.get("lightLevel", BasicCrop::getLightLevel, 0)
    ).apply(instance, BasicCrop::new));

    public static BasicCropSerializer SERIALIZER = new BasicCropSerializer();

    @Override
    public Codec<BasicCrop> codec() {

        return CODEC;
    }

    @Override
    public BasicCrop fromNetwork(FriendlyByteBuf buffer) {

        final Ingredient seed = BookshelfByteBufs.INGREDIENT.read(buffer);
        final Set<String> validSoils = BookshelfByteBufs.STRING.readSet(buffer);
        final int growthTicks = BookshelfByteBufs.INT.read(buffer);
        final List<HarvestEntry> results = HarvestEntry.BUFFER.readList(buffer);
        final List<DisplayState> displayStates = DisplayTypes.DISPLAY_STATE_BUFFER.readList(buffer);
        final int lightLevel = BookshelfByteBufs.INT.read(buffer);

        return new BasicCrop(seed, validSoils, growthTicks, results, displayStates, lightLevel);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, BasicCrop toWrite) {

        BookshelfByteBufs.INGREDIENT.write(buffer, toWrite.seed);
        BookshelfByteBufs.STRING.writeSet(buffer, toWrite.soilCategories);
        BookshelfByteBufs.INT.write(buffer, toWrite.growthTicks);
        HarvestEntry.BUFFER.writeList(buffer, toWrite.results);
        DisplayTypes.DISPLAY_STATE_BUFFER.writeList(buffer, toWrite.displayStates);
        BookshelfByteBufs.INT.write(buffer, toWrite.lightLevel);
    }
}
