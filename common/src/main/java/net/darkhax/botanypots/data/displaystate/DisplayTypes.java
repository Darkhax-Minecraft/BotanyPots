package net.darkhax.botanypots.data.displaystate;

import com.mojang.serialization.Codec;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.darkhax.bookshelf.api.data.bytebuf.ByteBufHelper;
import net.darkhax.bookshelf.api.data.codecs.BookshelfCodecs;
import net.darkhax.bookshelf.api.data.codecs.CodecHelper;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.data.displaystate.types.AgingDisplayState;
import net.darkhax.botanypots.data.displaystate.types.DisplayState;
import net.darkhax.botanypots.data.displaystate.types.EntityDisplayState;
import net.darkhax.botanypots.data.displaystate.types.SimpleDisplayState;
import net.darkhax.botanypots.data.displaystate.types.TransitionalDisplayState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class DisplayTypes {

    private static final Map<ResourceLocation, DisplayType<? extends DisplayState>> DISPLAY_TYPES = new HashMap<>();
    public static final CodecHelper<DisplayType<? extends DisplayState>> DISPLAY_TYPE_CODEC = new CodecHelper<>(ResourceLocation.CODEC.xmap(id -> DISPLAY_TYPES.containsKey(id) ? DISPLAY_TYPES.get(id) : DISPLAY_TYPES.get(Constants.id("aging")), DisplayType::id));
    public static final ByteBufHelper<DisplayType<?>> DISPLAY_TYPE_BUFFER = new ByteBufHelper<>(DisplayType::readFromBuffer, DisplayType::writeToBuffer);

    public static final Codec<DisplayState> DISPLAY_STATE_DISPATCH = BookshelfCodecs.dispatchFallback(DISPLAY_TYPE_CODEC.get(), DisplayState::getType, displayType -> (Codec<DisplayState>) displayType.getCodec(), () -> (Codec<DisplayState>) DISPLAY_TYPES.get(Constants.id("aging")).codec.get());
    public static final CodecHelper<DisplayState> DISPLAY_STATE_CODEC = new CodecHelper<>(DISPLAY_STATE_DISPATCH);
    public static final ByteBufHelper<DisplayState> DISPLAY_STATE_BUFFER = new ByteBufHelper<>(DisplayTypes::readFromBuffer, DisplayTypes::writeToBuffer);

    public static final DisplayType<SimpleDisplayState> SIMPLE = register(Constants.id("simple"), SimpleDisplayState.CODEC, SimpleDisplayState.BUFFER);
    public static final DisplayType<TransitionalDisplayState> TRANSITIONAL = register(Constants.id("transitional"), TransitionalDisplayState.CODEC, TransitionalDisplayState.BUFFER);
    public static final DisplayType<AgingDisplayState> AGING = register(Constants.id("aging"), AgingDisplayState.CODEC, AgingDisplayState.BUFFER);
    public static final DisplayType<EntityDisplayState> ENTITY = register(Constants.id("entity"), EntityDisplayState.CODEC, EntityDisplayState.BUFFER);

    public static <T extends DisplayState> DisplayType<T> register(ResourceLocation id, CodecHelper<T> codec, ByteBufHelper<T> buffer) {

        final DisplayType<T> type = new DisplayType<>(id, codec, buffer);

        if (DISPLAY_TYPES.containsKey(id)) {

            Constants.LOG.warn("Display type ID {} has already been assigned to {}. Replacing with {}.", id, DISPLAY_TYPES.get(id), type);
        }

        DISPLAY_TYPES.put(id, type);
        return type;
    }

    private static DisplayState readFromBuffer(FriendlyByteBuf buffer) {

        final DisplayType<?> type = DISPLAY_TYPE_BUFFER.read(buffer);
        return type.buffer.read(buffer);
    }

    private static void writeToBuffer(FriendlyByteBuf buffer, DisplayState toWrite) {

        DISPLAY_TYPE_BUFFER.write(buffer, toWrite.getType());
        ((ByteBufHelper) toWrite.getType().buffer).write(buffer, toWrite);
    }

    public record DisplayType<T extends DisplayState>(ResourceLocation id, CodecHelper<T> codec,
                                                      ByteBufHelper<T> buffer) {

        @Override
        public String toString() {

            return "DisplayType{id=" + id + ", codec=" + codec + ", buffer=" + buffer + '}';
        }

        public Codec<T> getCodec() {

            return this.codec.get();
        }

        private static DisplayType<?> readFromBuffer(FriendlyByteBuf buffer) {

            final ResourceLocation id = BookshelfByteBufs.RESOURCE_LOCATION.read(buffer);
            return DISPLAY_TYPES.get(id);
        }

        private static void writeToBuffer(FriendlyByteBuf buffer, DisplayType<?> toWrite) {

            BookshelfByteBufs.RESOURCE_LOCATION.write(buffer, toWrite.id);
        }
    }
}