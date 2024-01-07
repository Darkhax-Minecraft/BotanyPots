package net.darkhax.botanypots.data.displaystate.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.darkhax.bookshelf.api.data.bytebuf.ByteBufHelper;
import net.darkhax.bookshelf.api.data.codecs.BookshelfCodecs;
import net.darkhax.bookshelf.api.data.codecs.CodecHelper;
import net.darkhax.botanypots.data.displaystate.DisplayTypes;
import net.darkhax.botanypots.data.displaystate.math.AxisAlignedRotation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SimpleDisplayState extends AbstractSimpleDisplayState {

    public static final CodecHelper<SimpleDisplayState> CODEC = new CodecHelper<>(RecordCodecBuilder.create(instance -> instance.group(
            BookshelfCodecs.BLOCK.get("block", state -> state.getState().getBlock()),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("properties").forGetter(state -> DisplayState.encodeProperties(state.getState())),
            BookshelfCodecs.VECTOR_3F.getOptional("scale", SimpleDisplayState::getScale),
            BookshelfCodecs.VECTOR_3F.getOptional("offset", SimpleDisplayState::getOffset),
            AxisAlignedRotation.CODEC.getList("rotation", SimpleDisplayState::getRotations, new ArrayList<>()),
            BookshelfCodecs.BOOLEAN.get("renderFluid", SimpleDisplayState::shouldRenderFluid, false)
    ).apply(instance, SimpleDisplayState::new)));
    public static final ByteBufHelper<SimpleDisplayState> BUFFER = new ByteBufHelper<>(SimpleDisplayState::readFromBuffer, SimpleDisplayState::writeToBuffer);

    private final BlockState state;

    public SimpleDisplayState(Block block, Optional<Map<String, String>> properties, Optional<Vector3f> scale, Optional<Vector3f> offset, List<AxisAlignedRotation> rotations, boolean renderFluid) {

        this(decodeBlockState(block, properties), scale, offset, rotations, renderFluid);
    }

    public SimpleDisplayState(BlockState state, Optional<Vector3f> scale, Optional<Vector3f> offset, List<AxisAlignedRotation> rotations, boolean renderFluid) {

        super(scale, offset, rotations, renderFluid);
        this.state = state;
    }

    @Override
    public DisplayTypes.DisplayType<?> getType() {

        return DisplayTypes.SIMPLE;
    }

    public BlockState getState() {

        return this.state;
    }

    private static SimpleDisplayState readFromBuffer(FriendlyByteBuf buffer) {

        final BlockState state = BookshelfByteBufs.BLOCK_STATE.read(buffer);
        final Optional<Vector3f> scale = BookshelfByteBufs.VECTOR_3F.readOptional(buffer);
        final Optional<Vector3f> offset = BookshelfByteBufs.VECTOR_3F.readOptional(buffer);
        final List<AxisAlignedRotation> rotations = AxisAlignedRotation.BUFFER.readList(buffer);
        final boolean renderFluid = BookshelfByteBufs.BOOLEAN.read(buffer);
        return new SimpleDisplayState(state, scale, offset, rotations, renderFluid);
    }

    private static void writeToBuffer(FriendlyByteBuf buffer, SimpleDisplayState toWrite) {

        BookshelfByteBufs.BLOCK_STATE.write(buffer, toWrite.getState());
        BookshelfByteBufs.VECTOR_3F.writeOptional(buffer, toWrite.getScale());
        BookshelfByteBufs.VECTOR_3F.writeOptional(buffer, toWrite.getOffset());
        AxisAlignedRotation.BUFFER.writeList(buffer, toWrite.getRotations());
        BookshelfByteBufs.BOOLEAN.write(buffer, toWrite.shouldRenderFluid());
    }
}