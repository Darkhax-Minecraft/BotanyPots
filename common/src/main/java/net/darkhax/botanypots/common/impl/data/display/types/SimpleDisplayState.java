package net.darkhax.botanypots.common.impl.data.display.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.api.data.display.types.AbstractDisplay;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.Helpers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleDisplayState extends AbstractDisplay<BasicOptions> {

    public static final ResourceLocation TYPE_ID = BotanyPotsMod.id("simple");
    public static final CachedSupplier<DisplayType<SimpleDisplayState>> TYPE = CachedSupplier.cache(() -> DisplayType.get(TYPE_ID));
    public static final MapCodec<SimpleDisplayState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MapCodecs.BLOCK_STATE_MAP_CODEC.fieldOf("block_state").forGetter(SimpleDisplayState::getState),
            BasicOptions.CODEC.optionalFieldOf("options", BasicOptions.ofDefault()).forGetter(SimpleDisplayState::renderOptions)
    ).apply(instance, SimpleDisplayState::new));
    public static final StreamCodec<FriendlyByteBuf, SimpleDisplayState> STREAM = StreamCodec.composite(Helpers.BLOCK_STATE_STREAM, SimpleDisplayState::getState, BasicOptions.STREAM, SimpleDisplayState::renderOptions, SimpleDisplayState::new);

    private final BlockState state;

    public SimpleDisplayState(BlockState state, BasicOptions options) {
        super(options);
        this.state = state;
    }

    public BlockState getState() {
        return this.state;
    }

    @Override
    public DisplayType<?> getType() {
        return TYPE.get();
    }
}