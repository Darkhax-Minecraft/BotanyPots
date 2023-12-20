package net.darkhax.botanypots.data.recipes.potinteraction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.darkhax.bookshelf.api.data.codecs.BookshelfCodecs;
import net.darkhax.bookshelf.api.data.sound.Sound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BasicPotInteractionSerializer implements RecipeSerializer<BasicPotInteraction> {

    public static final RecipeSerializer<?> INSTANCE = new BasicPotInteractionSerializer();

    public static final Codec<BasicPotInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BookshelfCodecs.INGREDIENT.get("held_ingredient", BasicPotInteraction::getHeldTest),
            BookshelfCodecs.BOOLEAN.get("damage_held", BasicPotInteraction::isDamageHeld, true),
            BookshelfCodecs.INGREDIENT.getOptional("soil_ingredient", BasicPotInteraction::getSoilTest),
            BookshelfCodecs.INGREDIENT.getOptional("seed_ingredient", BasicPotInteraction::getSeedTest),
            BookshelfCodecs.ITEM_STACK_FLEXIBLE.getOptional("soil_output", BasicPotInteraction::getNewSoilStack),
            BookshelfCodecs.ITEM_STACK_FLEXIBLE.getOptional("seed_output", BasicPotInteraction::getNewSeedStack),
            BookshelfCodecs.SOUND.getOptional("sound", BasicPotInteraction::getSound),
            BookshelfCodecs.ITEM_STACK_FLEXIBLE.getList("drops", BasicPotInteraction::getExtraDrops, new ArrayList<>())
    ).apply(instance, BasicPotInteraction::new));

    @Override
    public Codec<BasicPotInteraction> codec() {

        return CODEC;
    }

    @Override
    public BasicPotInteraction fromNetwork(FriendlyByteBuf buffer) {

        final Ingredient heldTest = BookshelfByteBufs.INGREDIENT.read(buffer);
        final boolean damageHeld = BookshelfByteBufs.BOOLEAN.read(buffer);
        final Optional<Ingredient> soilTest = BookshelfByteBufs.INGREDIENT.readOptional(buffer);
        final Optional<Ingredient> seedTest = BookshelfByteBufs.INGREDIENT.readOptional(buffer);
        final Optional<ItemStack> soilOutput = BookshelfByteBufs.ITEM_STACK.readOptional(buffer);
        final Optional<ItemStack> seedOutput = BookshelfByteBufs.ITEM_STACK.readOptional(buffer);
        final Optional<Sound> sound = BookshelfByteBufs.SOUND.readOptional(buffer);
        final List<ItemStack> extraDrops = BookshelfByteBufs.ITEM_STACK.readList(buffer);

        return new BasicPotInteraction(heldTest, damageHeld, soilTest, seedTest, soilOutput, seedOutput, sound, extraDrops);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, BasicPotInteraction toWrite) {

        BookshelfByteBufs.INGREDIENT.write(buffer, toWrite.heldTest);
        BookshelfByteBufs.BOOLEAN.write(buffer, toWrite.damageHeld);
        BookshelfByteBufs.INGREDIENT.writeOptional(buffer, toWrite.soilTest);
        BookshelfByteBufs.INGREDIENT.writeOptional(buffer, toWrite.seedTest);
        BookshelfByteBufs.ITEM_STACK.writeOptional(buffer, toWrite.newSoilStack);
        BookshelfByteBufs.ITEM_STACK.writeOptional(buffer, toWrite.newSeedStack);
        BookshelfByteBufs.SOUND.writeOptional(buffer, toWrite.sound);
        BookshelfByteBufs.ITEM_STACK.writeList(buffer, toWrite.extraDrops);
    }
}