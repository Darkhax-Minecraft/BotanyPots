package net.darkhax.botanypots.data.displaystate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.darkhax.bookshelf.api.serialization.ISerializer;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class DisplayState {

    private static final Map<ResourceLocation, ISerializer<? extends DisplayState>> DISPLAY_TYPES = new HashMap<>();
    public static final ISerializer<DisplayState> SERIALIZER = new Serializer();

    @Nullable
    public static ISerializer<? extends DisplayState> getSerializer(ResourceLocation id) {

        return DISPLAY_TYPES.get(id);
    }

    public abstract DisplayStateSerializer<?> getSerializer();

    public static void init() {

        DISPLAY_TYPES.put(SimpleDisplayState.ID, SimpleDisplayState.SERIALIZER);
        DISPLAY_TYPES.put(TransitionalDisplayState.ID, TransitionalDisplayState.SERIALIZER);
        DISPLAY_TYPES.put(AgingDisplayState.ID, AgingDisplayState.SERIALIZER);
    }

    public static class Serializer implements ISerializer<DisplayState> {

        @Override
        public DisplayState fromJSON(JsonElement json) {

            if (json instanceof JsonObject obj) {

                final ResourceLocation id = Serializers.RESOURCE_LOCATION.fromJSON(obj, "type", SimpleDisplayState.ID);
                final ISerializer<? extends DisplayState> serializer = DISPLAY_TYPES.get(id);

                if (serializer != null) {

                    return serializer.fromJSON(json);
                }

                else {

                    throw new JsonParseException("The serializer " + id.toString() + " is unknown.");
                }
            }

            throw new JsonParseException("Expected display state to be a JSON object.");
        }

        @Override
        public JsonElement toJSON(DisplayState toWrite) {

            JsonElement json = ((DisplayStateSerializer<DisplayState>) toWrite.getSerializer()).toJSON(toWrite);
            if (json instanceof JsonObject obj) {

               Serializers.RESOURCE_LOCATION.toJSON(obj, "type", toWrite.getSerializer().getId());
            }

            return json;
        }

        @Override
        public DisplayState fromByteBuf(FriendlyByteBuf buffer) {

            final ResourceLocation id = Serializers.RESOURCE_LOCATION.fromByteBuf(buffer);
            final ISerializer<? extends DisplayState> serializer = DISPLAY_TYPES.get(id);

            if (serializer != null) {

                return serializer.fromByteBuf(buffer);
            }

            else {

                throw new JsonParseException("The serializer " + id.toString() + " is unknown.");
            }
        }

        @Override
        public void toByteBuf(FriendlyByteBuf buffer, DisplayState toWrite) {

            Serializers.RESOURCE_LOCATION.toByteBuf(buffer, toWrite.getSerializer().getId());
            ((DisplayStateSerializer<DisplayState>) toWrite.getSerializer()).toByteBuf(buffer, toWrite);
        }

        @Override
        public Tag toNBT(DisplayState toWrite) {

            return null;
        }

        @Override
        public DisplayState fromNBT(Tag nbt) {

            return null;
        }
    }
}