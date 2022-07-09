package net.darkhax.botanypots.data.displaystate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.darkhax.botanypots.Constants;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;

public class AgingDisplayState extends TransitionalDisplayState {

    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "aging");
    public static final DisplayStateSerializer<AgingDisplayState> SERIALIZER = new AgingDisplayState.Serializer();

    private final Block block;

    public AgingDisplayState(Block block) {

        super(calculatePhases(block));
        this.block = block;
    }

    @Override
    public DisplayStateSerializer<?> getSerializer() {

        return SERIALIZER;
    }

    private static IntegerProperty getAgeProperty(Block block) {

        if (block instanceof CropBlock crop) {

            return crop.getAgeProperty();
        }

        final Property<?> ageProperty = block.getStateDefinition().getProperty("age");

        if (ageProperty instanceof IntegerProperty intProp) {

            return intProp;
        }

        return null;
    }

    private static List<DisplayState> calculatePhases(Block block) {

        final List<DisplayState> phases = new ArrayList<>();

        final IntegerProperty ageProp = getAgeProperty(block);

        if (ageProp != null) {

            for (int age : ageProp.getPossibleValues()) {

                phases.add(new SimpleDisplayState(block.defaultBlockState().setValue(ageProp, age)));
            }
        }

        else {

            phases.add(new SimpleDisplayState(block.defaultBlockState()));
        }

        return phases;
    }

    public static class Serializer implements DisplayStateSerializer<AgingDisplayState> {

        @Override
        public ResourceLocation getId() {

            return ID;
        }

        @Override
        public AgingDisplayState fromJSON(JsonElement json) {

            if (json instanceof JsonObject obj) {

                final Block block = Serializers.BLOCK.fromJSON(obj, "block");
                return new AgingDisplayState(block);
            }

            throw new JsonParseException("Expected AgingDisplayState to be a JSON object.");
        }

        @Override
        public JsonElement toJSON(AgingDisplayState toWrite) {

            final JsonObject json = new JsonObject();
            Serializers.BLOCK.toJSON(json, "block", toWrite.block);
            return json;
        }

        @Override
        public AgingDisplayState fromByteBuf(FriendlyByteBuf buffer) {

            final Block block = Serializers.BLOCK.fromByteBuf(buffer);
            return new AgingDisplayState(block);
        }

        @Override
        public void toByteBuf(FriendlyByteBuf buffer, AgingDisplayState toWrite) {

            Serializers.BLOCK.toByteBuf(buffer, toWrite.block);
        }

        @Override
        public Tag toNBT(AgingDisplayState toWrite) {

            // TODO
            return null;
        }

        @Override
        public AgingDisplayState fromNBT(Tag nbt) {

            // TODO
            return null;
        }
    }
}