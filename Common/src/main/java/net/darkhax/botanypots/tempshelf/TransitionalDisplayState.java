package net.darkhax.botanypots.tempshelf;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.Constants;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

import java.util.List;

public class TransitionalDisplayState extends DisplayState {

    public static final ResourceLocation ID = new ResourceLocation(Constants.MOD_ID, "transitional");
    public static final DisplayStateSerializer<TransitionalDisplayState> SERIALIZER = new TransitionalDisplayState.Serializer();

    private final List<DisplayState> phases;

    public TransitionalDisplayState(List<DisplayState> phases) {

        this.phases = phases;
    }

    @Override
    public void render(PoseStack stack, Level level, BlockPos pos, MultiBufferSource bufferSource, int light, int overlay, float progress) {

        phases.get(Math.min(Mth.floor(phases.size() * progress), phases.size() - 1)).render(stack, level, pos, bufferSource, light, overlay, progress);
    }

    @Override
    public DisplayStateSerializer<?> getSerializer() {

        return SERIALIZER;
    }

    public static class Serializer implements DisplayStateSerializer<TransitionalDisplayState> {

        @Override
        public TransitionalDisplayState fromJSON(JsonElement json) {

            if (json instanceof JsonObject obj) {

                return new TransitionalDisplayState(DisplayState.SERIALIZER.fromJSONList(obj, "phases"));
            }

            throw new JsonParseException("Expected a JSON object.");
        }

        @Override
        public JsonElement toJSON(TransitionalDisplayState toWrite) {

            final JsonObject obj = new JsonObject();
            DisplayState.SERIALIZER.toJSONList(obj, "phases", toWrite.phases);
            return obj;
        }

        @Override
        public TransitionalDisplayState fromByteBuf(FriendlyByteBuf buffer) {

            return new TransitionalDisplayState(DisplayState.SERIALIZER.fromByteBufList(buffer));
        }

        @Override
        public void toByteBuf(FriendlyByteBuf buffer, TransitionalDisplayState toWrite) {

            DisplayState.SERIALIZER.toByteBufList(buffer, toWrite.phases);
        }

        @Override
        public Tag toNBT(TransitionalDisplayState toWrite) {

            // TODO NBT doesn't support optional yet
            return null;
        }

        @Override
        public TransitionalDisplayState fromNBT(Tag nbt) {

            return null;
        }

        @Override
        public ResourceLocation getId() {

            return ID;
        }
    }
}