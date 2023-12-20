package net.darkhax.botanypots.data.displaystate.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.darkhax.bookshelf.api.data.bytebuf.ByteBufHelper;
import net.darkhax.bookshelf.api.data.codecs.BookshelfCodecs;
import net.darkhax.bookshelf.api.data.codecs.CodecHelper;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.data.displaystate.DisplayTypes;
import net.darkhax.botanypots.data.displaystate.math.AxisAlignedRotation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AgingDisplayState extends PhasedDisplayState {

    public static final CodecHelper<AgingDisplayState> CODEC = new CodecHelper<>(RecordCodecBuilder.create(instance -> instance.group(
            BookshelfCodecs.BLOCK.get("block", state -> state.defaultState.getBlock()),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("properties").forGetter(state -> DisplayState.encodeProperties(state.defaultState)),
            BookshelfCodecs.VECTOR_3F.getOptional("scale", AgingDisplayState::getScale),
            BookshelfCodecs.VECTOR_3F.getOptional("offset", AgingDisplayState::getOffset),
            AxisAlignedRotation.CODEC.getList("rotation", AgingDisplayState::getRotations, new ArrayList<>()),
            BookshelfCodecs.BOOLEAN.get("renderFluid", AgingDisplayState::shouldRenderFluid, false)
    ).apply(instance, AgingDisplayState::new)));
    public static final ByteBufHelper<AgingDisplayState> BUFFER = new ByteBufHelper<>(AgingDisplayState::readFromBuffer, AgingDisplayState::writeToBuffer);

    private final BlockState defaultState;
    private final Optional<Vector3f> scale;
    private final Optional<Vector3f> offset;
    private final List<AxisAlignedRotation> rotations;
    private final boolean renderFluid;

    private final List<DisplayState> phases;

    public AgingDisplayState(Block block, Optional<Map<String, String>> properties, Optional<Vector3f> scale, Optional<Vector3f> offset, List<AxisAlignedRotation> rotations, boolean renderFluid) {

        this(decodeBlockState(block, properties), scale, offset, rotations, renderFluid);
    }

    public AgingDisplayState(BlockState defaultState, Optional<Vector3f> scale, Optional<Vector3f> offset, List<AxisAlignedRotation> rotations, boolean renderFluid) {

        this.defaultState = defaultState;
        this.scale = scale;
        this.offset = offset;
        this.rotations = rotations;
        this.renderFluid = renderFluid;
        this.phases = calculatePhases(this.defaultState);
    }

    private BlockState getDefaultState() {

        return this.defaultState;
    }

    public Optional<Vector3f> getScale() {
        return scale;
    }

    public Optional<Vector3f> getOffset() {
        return offset;
    }

    public List<AxisAlignedRotation> getRotations() {
        return rotations;
    }

    public boolean shouldRenderFluid() {
        return renderFluid;
    }

    private SimpleDisplayState buildPhase(BlockState state) {

        return new SimpleDisplayState(state, this.getScale(), this.getOffset(), this.getRotations(), this.shouldRenderFluid());
    }

    private List<DisplayState> calculatePhases(BlockState defaultState) {

        final Block block = defaultState.getBlock();
        final List<DisplayState> phases = new ArrayList<>();

        if (block instanceof CropBlock crop) {

            for (int i = 0; i < crop.getMaxAge(); i++) {

                try {

                    final BlockState agedState = crop.getStateForAge(i);

                    if (agedState != null) {

                        phases.add(this.buildPhase(agedState));
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

                    phases.add(this.buildPhase(defaultState.setValue(intProperty, age)));
                }
            }

            else {

                phases.add(this.buildPhase(defaultState));
            }
        }

        return phases;
    }

    @Override
    public DisplayTypes.DisplayType<?> getType() {

        return DisplayTypes.AGING;
    }

    private static AgingDisplayState readFromBuffer(FriendlyByteBuf buffer) {

        final BlockState defaultState = BookshelfByteBufs.BLOCK_STATE.read(buffer);
        final Optional<Vector3f> scale = BookshelfByteBufs.VECTOR_3F.readOptional(buffer);
        final Optional<Vector3f> offset = BookshelfByteBufs.VECTOR_3F.readOptional(buffer);
        final List<AxisAlignedRotation> rotations = AxisAlignedRotation.BUFFER.readList(buffer);
        final boolean renderFluid = BookshelfByteBufs.BOOLEAN.read(buffer);
        return new AgingDisplayState(defaultState, scale, offset, rotations, renderFluid);
    }

    private static void writeToBuffer(FriendlyByteBuf buffer, AgingDisplayState toWrite) {

        BookshelfByteBufs.BLOCK_STATE.write(buffer, toWrite.getDefaultState());
        BookshelfByteBufs.VECTOR_3F.writeOptional(buffer, toWrite.getScale());
        BookshelfByteBufs.VECTOR_3F.writeOptional(buffer, toWrite.getOffset());
        AxisAlignedRotation.BUFFER.writeList(buffer, toWrite.getRotations());
        BookshelfByteBufs.BOOLEAN.write(buffer, toWrite.shouldRenderFluid());
    }

    @Override
    public List<DisplayState> getDisplayPhases() {

        return this.phases;
    }
}