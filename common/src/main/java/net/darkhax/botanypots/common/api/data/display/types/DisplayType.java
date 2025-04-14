package net.darkhax.botanypots.common.api.data.display.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a type of display. Handles the serialization of the display and is used to bind renderers. Display types
 * must be registered.
 *
 * @param typeId The ID of the type.
 * @param codec  A codec that serializes the display.
 * @param stream A stream codec that serializes the display.
 * @param <T>    The type of display.
 */
public record DisplayType<T extends Display>(ResourceLocation typeId, MapCodec<T> codec, StreamCodec<FriendlyByteBuf, T> stream) {

    private static final Map<ResourceLocation, DisplayType<? extends Display>> REGISTRY = new HashMap<>();
    public static final Codec<DisplayType<?>> TYPE_CODEC = ResourceLocation.CODEC.xmap(REGISTRY::get, DisplayType::typeId);
    public static final StreamCodec<ByteBuf, ? extends DisplayType<? extends Display>> TYPE_STREAM = ResourceLocation.STREAM_CODEC.map(REGISTRY::get, DisplayType::typeId);
    public static final Codec<Display> DISPLAY_STATE_CODEC = TYPE_CODEC.dispatch(Display::getType, DisplayType::codec);
    public static final StreamCodec<FriendlyByteBuf, Display> DISPLAY_STATE_STREAM = new StreamCodec<>() {
        @NotNull
        @Override
        public Display decode(@NotNull FriendlyByteBuf buf) {
            final ResourceLocation id = buf.readResourceLocation();
            if (!REGISTRY.containsKey(id)) {
                BotanyPotsMod.LOG.error("Display type {} does not exist!", id);
                throw new IllegalStateException("Display type " + id + " does not exist!");
            }
            return REGISTRY.get(id).stream.decode(buf);
        }

        @Override
        public void encode(@NotNull FriendlyByteBuf buf, @NotNull Display state) {
            buf.writeResourceLocation(state.getType().typeId());
            ((StreamCodec) state.getType().stream()).encode(buf, state);
        }
    };
    public static final Codec<List<Display>> LIST_CODEC = MapCodecs.flexibleList(DISPLAY_STATE_CODEC);

    /**
     * Gets a display type by its ID. Will be null if no type with the provided ID exists.
     *
     * @param id  The ID of the type to lookup.
     * @param <T> The type of the display.
     * @return The registered type or null if no type was found.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends Display> DisplayType<T> get(ResourceLocation id) {
        return (DisplayType<T>) REGISTRY.get(id);
    }

    /**
     * Registers a display type.
     *
     * @param id     The ID to register the type with.
     * @param codec  The codec for the type.
     * @param buffer The stream codec for the type.
     * @param <T>    The type of display represented by the type.
     * @return The registered display type.
     */
    public static <T extends Display> DisplayType<T> register(ResourceLocation id, MapCodec<T> codec, StreamCodec<FriendlyByteBuf, T> buffer) {
        final DisplayType<T> type = new DisplayType<>(id, codec, buffer);
        if (REGISTRY.containsKey(id)) {
            BotanyPotsMod.LOG.warn("Display type ID {} has already been assigned to {}. You can not set it to {}.", id, REGISTRY.get(id), type);
            throw new IllegalStateException("Display type " + id.toString() + " has already been registered!");
        }
        REGISTRY.put(id, type);
        return type;
    }
}