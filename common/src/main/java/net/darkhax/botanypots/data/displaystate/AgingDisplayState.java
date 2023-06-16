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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;

public class AgingDisplayState extends TransitionalDisplayState {

    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "aging");
    public static final DisplayStateSerializer<AgingDisplayState> SERIALIZER = new AgingDisplayState.Serializer();

    private final BlockState defaultState;

    public AgingDisplayState(BlockState defaultState) {

        super(calculatePhases(defaultState));
        this.defaultState = defaultState;
    }

    @Override
    public DisplayStateSerializer<?> getSerializer() {

        return SERIALIZER;
    }

    private static List<DisplayState> calculatePhases(BlockState defaultState) {

        final Block block = defaultState.getBlock();
        final List<DisplayState> phases = new ArrayList<>();

        if (block instanceof CropBlock crop) {

            for (int i = 0; i < crop.getMaxAge(); i++) {

                try {

                    final BlockState agedState = crop.getStateForAge(i);

                    if (agedState != null) {

                        phases.add(new SimpleDisplayState(agedState));
                    }
                }

                catch (Exception e) {

                    Constants.LOG.error("Invalid crop age found! state={} age={}", defaultState, i);
                    Constants.LOG.error("Error: ", e);
                }
            }
        }

        else {

            final Property<?> ageProperty = block.getStateDefinition().getProperty("age");

            if (ageProperty instanceof IntegerProperty intProperty) {

                for (int age : intProperty.getPossibleValues()) {

                    phases.add(new SimpleDisplayState(defaultState.setValue(intProperty, age)));
                }
            }

            else {

                phases.add(new SimpleDisplayState(defaultState));
            }
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

                final BlockState defaultState = Serializers.BLOCK_STATE.fromJSON(obj, "block");

                if (defaultState == null) {

                    throw new JsonParseException("Could not read block! " + obj.get("block"));
                }

                return new AgingDisplayState(defaultState);
            }

            throw new JsonParseException("Expected AgingDisplayState to be a JSON object.");
        }

        @Override
        public JsonElement toJSON(AgingDisplayState toWrite) {

            final JsonObject json = new JsonObject();
            Serializers.BLOCK_STATE.toJSON(json, "block", toWrite.defaultState);
            return json;
        }

        @Override
        public AgingDisplayState fromByteBuf(FriendlyByteBuf buffer) {

            final BlockState block = Serializers.BLOCK_STATE.fromByteBuf(buffer);
            return new AgingDisplayState(block);
        }

        @Override
        public void toByteBuf(FriendlyByteBuf buffer, AgingDisplayState toWrite) {

            Serializers.BLOCK_STATE.toByteBuf(buffer, toWrite.defaultState);
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