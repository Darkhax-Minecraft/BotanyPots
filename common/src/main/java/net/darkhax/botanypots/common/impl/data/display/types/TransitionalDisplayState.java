package net.darkhax.botanypots.common.impl.data.display.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class TransitionalDisplayState extends PhasedDisplayState {

    public static final ResourceLocation TYPE_ID = BotanyPotsMod.id("transitional");
    public static final CachedSupplier<DisplayType<TransitionalDisplayState>> TYPE = CachedSupplier.cache(() -> DisplayType.get(TYPE_ID));
    public static final MapCodec<TransitionalDisplayState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MapCodecs.flexibleList(DisplayType.DISPLAY_STATE_CODEC).fieldOf("phases").forGetter(TransitionalDisplayState::getDisplayPhases)
    ).apply(instance, TransitionalDisplayState::new));
    public static final StreamCodec<FriendlyByteBuf, TransitionalDisplayState> STREAM = DisplayType.DISPLAY_STATE_STREAM.apply(ByteBufCodecs.list()).map(TransitionalDisplayState::new, TransitionalDisplayState::getDisplayPhases);

    private final List<Display> phases;

    public TransitionalDisplayState(List<Display> phases) {
        this.phases = phases;
    }

    @Override
    public List<Display> getDisplayPhases() {
        return this.phases;
    }

    @Override
    public DisplayType<?> getType() {
        return TYPE.get();
    }
}