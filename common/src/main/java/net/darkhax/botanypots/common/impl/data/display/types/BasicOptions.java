package net.darkhax.botanypots.common.impl.data.display.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.darkhax.botanypots.common.api.data.display.math.AxisAlignedRotation;
import net.darkhax.botanypots.common.api.data.display.math.TintColor;
import net.darkhax.botanypots.common.api.data.display.types.RenderOptions;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A basic implementation of the render options.
 */
public class BasicOptions implements RenderOptions {

    /**
     * The default set of faces that should not be culled. All faces are preserved by default.
     */
    public static Set<Direction> DEFAULT_FACES = Set.of(Direction.values());

    /**
     * The default scale to apply to displays. The default is 62.5% which is what we determined looked good at 100%
     * growth.
     */
    public static Vector3f DEFAULT_SCALE = new Vector3f(0.625f, 0.625f, 0.625f);

    /**
     * The default offset, which does nothing.
     */
    public static Vector3f DEFAULT_OFFSET = new Vector3f(0f, 0f, 0f);

    public static final Codec<BasicOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.VECTOR3F.optionalFieldOf("scale", DEFAULT_SCALE).forGetter(BasicOptions::getScale),
            ExtraCodecs.VECTOR3F.optionalFieldOf("offset", DEFAULT_OFFSET).forGetter(BasicOptions::getOffset),
            AxisAlignedRotation.CODEC_HELPER.getList("rotation", BasicOptions::getRotations, new ArrayList<>()),
            MapCodecs.BOOLEAN.get("render_fluid", BasicOptions::shouldRenderFluid, false),
            TintColor.CODEC.optionalFieldOf("color").forGetter(BasicOptions::getColor),
            MapCodecs.flexibleSet(Direction.CODEC).optionalFieldOf("faces", DEFAULT_FACES).forGetter(BasicOptions::getFaces)
    ).apply(instance, BasicOptions::new));

    public static final StreamCodec<FriendlyByteBuf, BasicOptions> STREAM = new StreamCodec<>() {
        @Override
        public void encode(@NotNull FriendlyByteBuf buf, @NotNull BasicOptions state) {
            ByteBufCodecs.VECTOR3F.encode(buf, state.scale);
            ByteBufCodecs.VECTOR3F.encode(buf, state.offset);
            buf.writeCollection(state.rotations, AxisAlignedRotation.STREAM);
            buf.writeBoolean(state.renderFluid);
            buf.writeOptional(state.tintColor, TintColor.STREAM);
            buf.writeInt(state.faces.size());
            for (Direction face : state.faces) {
                buf.writeEnum(face);
            }
        }

        @NotNull
        @Override
        public BasicOptions decode(@NotNull FriendlyByteBuf buf) {
            final Vector3f scale = ByteBufCodecs.VECTOR3F.decode(buf);
            final Vector3f offset = ByteBufCodecs.VECTOR3F.decode(buf);
            final List<AxisAlignedRotation> rotations = buf.readList(AxisAlignedRotation.STREAM);
            final boolean renderFluid = buf.readBoolean();
            final Optional<TintColor> tintColor = buf.readOptional(TintColor.STREAM);
            final Set<Direction> faces = new HashSet<>();
            final int faceSize = buf.readInt();
            for (int i = 0; i < faceSize; i++) {
                faces.add(buf.readEnum(Direction.class));
            }
            return new BasicOptions(scale, offset, rotations, renderFluid, tintColor, faces);
        }
    };

    private final Vector3f scale;
    private final Vector3f offset;
    private final List<AxisAlignedRotation> rotations;
    private final boolean renderFluid;
    private final Optional<TintColor> tintColor;
    private final Set<Direction> faces;

    public BasicOptions(Vector3f scale, Vector3f offset, List<AxisAlignedRotation> rotations, boolean renderFluid, Optional<TintColor> tintColor, Set<Direction> faces) {
        this.scale = scale;
        this.offset = offset;
        this.rotations = rotations;
        this.renderFluid = renderFluid;
        this.tintColor = tintColor;
        this.faces = faces;
    }

    @Override
    public Vector3f getScale() {
        return scale;
    }

    @Override
    public Vector3f getOffset() {
        return offset;
    }

    @Override
    public List<AxisAlignedRotation> getRotations() {
        return rotations;
    }

    @Override
    public boolean shouldRenderFluid() {
        return renderFluid;
    }

    @Override
    public Optional<TintColor> getColor() {
        return this.tintColor;
    }

    @Override
    public Set<Direction> getFaces() {
        return this.faces;
    }

    public static BasicOptions ofDefault() {
        return ofDefault(DEFAULT_FACES);
    }

    public static BasicOptions ofDefault(Set<Direction> faces) {
        return new BasicOptions(DEFAULT_SCALE, DEFAULT_OFFSET, List.of(), false, Optional.empty(), faces);
    }
}
