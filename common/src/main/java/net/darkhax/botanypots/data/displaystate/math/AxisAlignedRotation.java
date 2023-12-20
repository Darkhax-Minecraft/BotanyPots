package net.darkhax.botanypots.data.displaystate.math;

import com.mojang.math.Axis;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.darkhax.bookshelf.api.data.bytebuf.ByteBufHelper;
import net.darkhax.bookshelf.api.data.codecs.BookshelfCodecs;
import net.darkhax.bookshelf.api.data.codecs.CodecHelper;
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

    public static final CodecHelper<AxisAlignedRotation> CODEC = new CodecHelper<>(BookshelfCodecs.enumerable(AxisAlignedRotation.class));
    public static final ByteBufHelper<AxisAlignedRotation> BUFFER = BookshelfByteBufs.enumerable(AxisAlignedRotation.class);

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

        this.rotation = axis.quaternions[amount];
        this.offset = axis.offsets[amount];
    }

    /**
     * An enum containing information about each rotational axis.
     */
    public enum RotationAxis {

        X(Axis.XP, new Vector3f(0, 0, 0), new Vector3f(0, 0, -1), new Vector3f(0, -1, -1), new Vector3f(0, -1, 0)),
        Y(Axis.YP, new Vector3f(0, 0, 0), new Vector3f(-1, 0, 0), new Vector3f(-1, 0, -1), new Vector3f(0, 0, -1)),
        Z(Axis.ZP, new Vector3f(0, 0, 0), new Vector3f(0, -1, 0), new Vector3f(-1, -1, 0), new Vector3f(-1, 0, 0));

        /**
         * The rotation quaternions for 0, 90, 180, and 270 degrees along the axis.
         */
        private final Quaternionf[] quaternions;

        /**
         * The translation offsets to snap the render back to the original axis aligned position.
         */
        private final Vector3f[] offsets;

        RotationAxis(Axis axisVect, Vector3f offsetA, Vector3f offsetB, Vector3f offsetC, Vector3f offsetD) {

            this.quaternions = new Quaternionf[]{axisVect.rotationDegrees(0), axisVect.rotationDegrees(90f), axisVect.rotationDegrees(180f), axisVect.rotationDegrees(270f)};
            this.offsets = new Vector3f[]{offsetA, offsetB, offsetC, offsetD};
        }
    }
}
