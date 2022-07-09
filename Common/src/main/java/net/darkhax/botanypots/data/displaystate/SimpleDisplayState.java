package net.darkhax.botanypots.data.displaystate;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Vector3f;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.data.displaystate.math.AxisAlignedRotation;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleDisplayState extends AbstractSimpleDisplayState {

    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "simple");
    public static final DisplayStateSerializer<SimpleDisplayState> SERIALIZER = new Serializer();

    private BlockState state;

    public SimpleDisplayState(BlockState state) {

        this(state, Optional.empty(), Optional.empty(), new ArrayList<>(), false);
    }

    public SimpleDisplayState(BlockState state, Optional<Vector3f> scale, Optional<Vector3f> offset, List<AxisAlignedRotation> rotations, boolean renderFluid) {

        super(scale, offset, rotations, renderFluid);
        this.state = state;
    }

    @Override
    public BlockState getRenderState(float progress) {

        return this.state;
    }

    @Override
    public DisplayStateSerializer<?> getSerializer() {

        return SERIALIZER;
    }

    public static class Serializer implements DisplayStateSerializer<SimpleDisplayState> {

        @Override
        public SimpleDisplayState fromJSON(JsonElement json) {

            if (json instanceof JsonObject obj) {

                final BlockState state = Serializers.BLOCK_STATE.fromJSON(obj);
                final Optional<Vector3f> scale = Serializers.VECTOR_3F.fromJSONOptional(obj, "scale");
                final Optional<Vector3f> offset = Serializers.VECTOR_3F.fromJSONOptional(obj, "offset");
                final List<AxisAlignedRotation> rotations = AxisAlignedRotation.SERIALIZER.fromJSONList(obj, "rotation", new ArrayList<>());
                final boolean renderFluid = Serializers.BOOLEAN.fromJSON(obj, "renderFluid", false);
                return new SimpleDisplayState(state, scale, offset, rotations, renderFluid);
            }

            throw new JsonParseException("Expected a JSON object.");
        }

        @Override
        public JsonElement toJSON(SimpleDisplayState toWrite) {

            final JsonObject obj = new JsonObject();
            Serializers.BLOCK_STATE.toJSON(obj, "state", toWrite.state);
            Serializers.VECTOR_3F.toJSONOptional(obj, "scale", toWrite.scale);
            Serializers.VECTOR_3F.toJSONOptional(obj, "offset", toWrite.offset);
            AxisAlignedRotation.SERIALIZER.toJSONList(obj, "rotation", toWrite.rotations);
            Serializers.BOOLEAN.toJSON(obj, "renderFluid", toWrite.renderFluid);
            return obj;
        }

        @Override
        public SimpleDisplayState fromByteBuf(FriendlyByteBuf buffer) {

            final BlockState state = Serializers.BLOCK_STATE.fromByteBuf(buffer);
            final Optional<Vector3f> scale = Serializers.VECTOR_3F.fromByteBufOptional(buffer);
            final Optional<Vector3f> offset = Serializers.VECTOR_3F.fromByteBufOptional(buffer);
            final List<AxisAlignedRotation> rotations = AxisAlignedRotation.SERIALIZER.fromByteBufList(buffer);
            final boolean renderFluid = Serializers.BOOLEAN.fromByteBuf(buffer);
            return new SimpleDisplayState(state, scale, offset, rotations, renderFluid);
        }

        @Override
        public void toByteBuf(FriendlyByteBuf buffer, SimpleDisplayState toWrite) {

            Serializers.BLOCK_STATE.toByteBuf(buffer, toWrite.state);
            Serializers.VECTOR_3F.toByteBufOptional(buffer, toWrite.scale);
            Serializers.VECTOR_3F.toByteBufOptional(buffer, toWrite.offset);
            AxisAlignedRotation.SERIALIZER.toByteBufList(buffer, toWrite.rotations);
            Serializers.BOOLEAN.toByteBuf(buffer, toWrite.renderFluid);
        }

        @Override
        public Tag toNBT(SimpleDisplayState toWrite) {

            // TODO NBT doesn't support optional yet
            return null;
        }

        @Override
        public SimpleDisplayState fromNBT(Tag nbt) {

            return null;
        }

        @Override
        public ResourceLocation getId() {

            return ID;
        }
    }
}