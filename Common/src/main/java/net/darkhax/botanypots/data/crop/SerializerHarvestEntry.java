package net.darkhax.botanypots.data.crop;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.darkhax.bookshelf.api.serialization.ISerializer;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public final class SerializerHarvestEntry implements ISerializer<HarvestEntry> {

    public static ISerializer<HarvestEntry> SERIALIZER = new SerializerHarvestEntry();

    @Override
    public HarvestEntry fromJSON(JsonElement json) {

        if (json instanceof JsonObject obj) {

            final Float chance = Serializers.FLOAT.fromJSON(obj, "chance");
            final ItemStack output = Serializers.ITEM_STACK.fromJSON(obj, "output");
            final int minRolls = Serializers.INT.fromJSON(obj, "minRolls", 1);
            final int maxRolls = Serializers.INT.fromJSON(obj, "maxRolls", 1);
            return new HarvestEntry(chance, output, minRolls, maxRolls);
        }

        else {

            throw new JsonParseException("Expected harvest entry to be a JSON object.");
        }
    }

    @Override
    public JsonElement toJSON(HarvestEntry toWrite) {

        final JsonObject json = new JsonObject();
        Serializers.FLOAT.toJSON(json, "chance", toWrite.getChance());
        Serializers.ITEM_STACK.toJSON(json, "output", toWrite.getItem());
        Serializers.INT.toJSON(json, "minRolls", toWrite.getMinRolls());
        Serializers.INT.toJSON(json, "maxRolls", toWrite.getMaxRolls());
        return json;
    }

    @Override
    public HarvestEntry fromByteBuf(FriendlyByteBuf buffer) {

        final Float chance = Serializers.FLOAT.fromByteBuf(buffer);
        final ItemStack output = Serializers.ITEM_STACK.fromByteBuf(buffer);
        final int minRolls = Serializers.INT.fromByteBuf(buffer);
        final int maxRolls = Serializers.INT.fromByteBuf(buffer);
        return new HarvestEntry(chance, output, minRolls, maxRolls);
    }

    @Override
    public void toByteBuf(FriendlyByteBuf buffer, HarvestEntry toWrite) {

        Serializers.FLOAT.toByteBuf(buffer, toWrite.getChance());
        Serializers.ITEM_STACK.toByteBuf(buffer, toWrite.getItem());
        Serializers.INT.toByteBuf(buffer, toWrite.getMinRolls());
        Serializers.INT.toByteBuf(buffer, toWrite.getMaxRolls());
    }

    @Override
    public Tag toNBT(HarvestEntry toWrite) {

        final CompoundTag nbt = new CompoundTag();
        Serializers.FLOAT.toNBT(nbt, "chance", toWrite.getChance());
        Serializers.ITEM_STACK.toNBT(nbt, "output", toWrite.getItem());
        Serializers.INT.toNBT(nbt, "minRolls", toWrite.getMinRolls());
        Serializers.INT.toNBT(nbt, "maxRolls", toWrite.getMaxRolls());
        return nbt;
    }

    @Override
    public HarvestEntry fromNBT(Tag nbt) {

        if (nbt instanceof CompoundTag tag) {

            final Float chance = Serializers.FLOAT.fromNBT(tag, "chance");
            final ItemStack output = Serializers.ITEM_STACK.fromNBT(tag, "output");
            final int minRolls = Serializers.INT.fromNBT(tag, "minRolls");
            final int maxRolls = Serializers.INT.fromNBT(tag, "maxRolls");
            return new HarvestEntry(chance, output, minRolls, maxRolls);
        }

        else {

            throw new JsonParseException("Expected harvest entry to be a JSON object.");
        }
    }
}