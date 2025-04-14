package net.darkhax.botanypots.common.api.data.display.math;

import com.mojang.math.Axis;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    RotationAxis(Axis axis, Vector3f offsetA, Vector3f offsetB, Vector3f offsetC, Vector3f offsetD) {
        this.quaternions = new Quaternionf[]{axis.rotationDegrees(0), axis.rotationDegrees(90f), axis.rotationDegrees(180f), axis.rotationDegrees(270f)};
        this.offsets = new Vector3f[]{offsetA, offsetB, offsetC, offsetD};
    }

    /**
     * Gets the rotation quaternions for the specified degrees.
     *
     * @param index The degrees index. Must be 0-3. Represents 0, 90, 180, and 270.
     * @return The rotation quaternion for the specified degrees.
     */
    public Quaternionf getRotation(int index) {
        if (index < 0 || index > 3) {
            throw new IndexOutOfBoundsException("Rotation must be a number from 0 to 3.");
        }
        return quaternions[index];
    }

    /**
     * Gets the offset required to move back to the original position after a rotation has been applied.
     *
     * @param index The degrees index. Must be 0-3. Represents 0, 90, 180, and 270.
     * @return The offset to recenter after a rotation.
     */
    public Vector3f getOffset(int index) {
        if (index < 0 || index > 3) {
            throw new IndexOutOfBoundsException("Offset must be a number from 0 to 3.");
        }
        return offsets[index];
    }
}