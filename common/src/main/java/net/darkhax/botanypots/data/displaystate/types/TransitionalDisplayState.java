package net.darkhax.botanypots.data.displaystate.types;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.api.data.bytebuf.ByteBufHelper;
import net.darkhax.bookshelf.api.data.codecs.CodecHelper;
import net.darkhax.botanypots.data.displaystate.DisplayTypes;
import net.minecraft.network.FriendlyByteBuf;

import java.util.List;

public class TransitionalDisplayState extends PhasedDisplayState {

    public static final CodecHelper<TransitionalDisplayState> CODEC = new CodecHelper<>(RecordCodecBuilder.create(instance -> instance.group(
            DisplayTypes.DISPLAY_STATE_CODEC.getList("phases", TransitionalDisplayState::getDisplayPhases)
    ).apply(instance, TransitionalDisplayState::new)));
    public static final ByteBufHelper<TransitionalDisplayState> BUFFER = new ByteBufHelper<>(TransitionalDisplayState::readFromBuffer, TransitionalDisplayState::writeToBuffer);

    private final List<DisplayState> phases;

    public TransitionalDisplayState(List<DisplayState> phases) {

        this.phases = phases;
    }

    @Override
    public DisplayTypes.DisplayType<?> getType() {

        return DisplayTypes.TRANSITIONAL;
    }

    private static TransitionalDisplayState readFromBuffer(FriendlyByteBuf buffer) {

        final List<DisplayState> phases = DisplayTypes.DISPLAY_STATE_BUFFER.readList(buffer);
        return new TransitionalDisplayState(phases);
    }

    private static void writeToBuffer(FriendlyByteBuf buffer, TransitionalDisplayState toWrite) {

        DisplayTypes.DISPLAY_STATE_BUFFER.writeList(buffer, toWrite.getDisplayPhases());
    }

    @Override
    public List<DisplayState> getDisplayPhases() {

        return this.phases;
    }
}