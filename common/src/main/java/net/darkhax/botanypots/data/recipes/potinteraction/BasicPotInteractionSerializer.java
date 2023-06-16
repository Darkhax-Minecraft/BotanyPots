package net.darkhax.botanypots.data.recipes.potinteraction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.darkhax.bookshelf.api.data.sound.Sound;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;

public final class BasicPotInteractionSerializer implements RecipeSerializer<BasicPotInteraction> {

    public static final RecipeSerializer<?> SERIALIZER = new BasicPotInteractionSerializer();

    @Override
    public BasicPotInteraction fromJson(ResourceLocation id, JsonObject json) {

        final Ingredient heldTest = Serializers.INGREDIENT.fromJSON(json, "held_ingredient");
        final boolean damageHeld = Serializers.BOOLEAN.fromJSON(json, "damage_held", true);
        final Ingredient soilTest = Serializers.INGREDIENT.fromJSONNullable(json, "soil_ingredient");
        final Ingredient seedTest = Serializers.INGREDIENT.fromJSONNullable(json, "seed_ingredient");
        final ItemStack soilOutput = Serializers.ITEM_STACK.fromJSONNullable(json, "soil_output");
        final ItemStack seedOutput = Serializers.ITEM_STACK.fromJSONNullable(json, "seed_output");
        final Sound sound = Serializers.SOUND.fromJSONNullable(json, "sound");
        final List<ItemStack> extraDrops = Serializers.ITEM_STACK.fromJSONList(json, "drops", new ArrayList<>());

        if (soilTest == null && seedTest == null) {

            throw new JsonParseException("Recipe requires at least one soil or seed ingredient.");
        }

        if (soilOutput == null && seedOutput == null) {

            throw new JsonParseException("Recipe requires at least one output.");
        }

        return new BasicPotInteraction(id, heldTest, damageHeld, soilTest, seedTest, soilOutput, seedOutput, sound, extraDrops);
    }

    @Override
    public BasicPotInteraction fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

        final Ingredient heldTest = Serializers.INGREDIENT.fromByteBuf(buffer);
        final boolean damageHeld = Serializers.BOOLEAN.fromByteBuf(buffer);
        final Ingredient soilTest = Serializers.INGREDIENT.fromByteBufNullable(buffer);
        final Ingredient seedTest = Serializers.INGREDIENT.fromByteBufNullable(buffer);
        final ItemStack soilOutput = Serializers.ITEM_STACK.fromByteBufNullable(buffer);
        final ItemStack seedOutput = Serializers.ITEM_STACK.fromByteBufNullable(buffer);
        final Sound sound = Serializers.SOUND.fromByteBufNullable(buffer);
        final List<ItemStack> extraDrops = Serializers.ITEM_STACK.fromByteBufList(buffer);

        return new BasicPotInteraction(id, heldTest, damageHeld, soilTest, seedTest, soilOutput, seedOutput, sound, extraDrops);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, BasicPotInteraction toWrite) {

        Serializers.INGREDIENT.toByteBuf(buffer, toWrite.heldTest);
        Serializers.BOOLEAN.toByteBuf(buffer, toWrite.damageHeld);
        Serializers.INGREDIENT.toByteBufNullable(buffer, toWrite.soilTest);
        Serializers.INGREDIENT.toByteBufNullable(buffer, toWrite.seedTest);
        Serializers.ITEM_STACK.toByteBufNullable(buffer, toWrite.newSoilStack);
        Serializers.ITEM_STACK.toByteBufNullable(buffer, toWrite.newSeedStack);
        Serializers.SOUND.toByteBufNullable(buffer, toWrite.sound);
        Serializers.ITEM_STACK.toByteBufList(buffer, toWrite.extraDrops);
    }
}
