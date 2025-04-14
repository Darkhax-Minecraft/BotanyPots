package net.darkhax.botanypots.common.api.data.display.math;

import com.mojang.serialization.Codec;
import net.darkhax.bookshelf.common.api.data.codecs.EnumStreamCodec;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecHelper;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * This enum contains rotational state data that can be used to rotate a render while retaining alignment with the world
 * axis.
 */
public enum AxisAlignedRotation {

    X_0(RotationAxis.X, 0),
    X_90(RotationAxis.X, 1),
    X_180(RotationAxis.X, 2),
    X_270(RotationAxis.X, 3),

    Y_0(RotationAxis.Y, 0),
    Y_90(RotationAxis.Y, 1),
    Y_180(RotationAxis.Y, 2),
    Y_270(RotationAxis.Y, 3),

    Z_0(RotationAxis.Z, 0),
    Z_90(RotationAxis.Z, 1),
    Z_180(RotationAxis.Z, 2),
    Z_270(RotationAxis.Z, 3);

    public static final Codec<AxisAlignedRotation> CODEC = MapCodecs.enumerable(AxisAlignedRotation.class);
    public static final MapCodecHelper<AxisAlignedRotation> CODEC_HELPER = new MapCodecHelper<>(CODEC);
    public static final EnumStreamCodec<AxisAlignedRotation> STREAM = new EnumStreamCodec<>(AxisAlignedRotation.class);

    /**
     * A Quaternion that contains the rotational information. In this case it represents a 0, 90, 180, or 270-degree
     * rotation along the X, Y, or Z axis.
     */
    public final Quaternionf rotation;

    /**
     * A predetermined offset that will realign the render when translated.
     */
    public final Vector3f offset;

    AxisAlignedRotation(RotationAxis axis, int amount) {
        if (amount < 0 || amount > 3) {
            throw new IllegalArgumentException("Rotation amount " + amount + " is out of bounds. Must be 0-3. 0 = 0 degrees. 1 = 90 degrees. 2 = 180 degrees. 3 = 270 degrees.");
        }
        this.rotation = axis.getRotation(amount);
        this.offset = axis.getOffset(amount);
    }
}
