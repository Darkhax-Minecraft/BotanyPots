package net.darkhax.botanypots.common.api.data.display.types;

import net.darkhax.botanypots.common.api.data.display.math.AxisAlignedRotation;
import net.darkhax.botanypots.common.api.data.display.math.TintColor;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a configuration for a display renderer. These options control basic properties such as scale, rotation,
 * and offsets.
 */
public interface RenderOptions {

    /**
     * Gets the scale of the render. Each component of the vector represents a percentage based scale for that axis.
     *
     * @return The amount to scale the render.
     */
    Vector3f getScale();

    /**
     * Gets the offset of the render. Each component of the vector represents an offset for that axis.
     *
     * @return The amount to offset the render.
     */
    Vector3f getOffset();

    /**
     * Gets a list of rotations to apply to the render.
     *
     * @return The rotations to apply to the render.
     */
    List<AxisAlignedRotation> getRotations();

    /**
     * Check if fluid layers should be included in the rendered display.
     *
     * @return If fluid layers should be rendered.
     */
    boolean shouldRenderFluid();

    /**
     * Gets an optional tint color to apply to any table elements of the display.
     *
     * @return An optional tint to apply during rendering.
     */
    Optional<TintColor> getColor();

    /**
     * A set of faces that should not be culled when rendering.
     *
     * @return The faces that should not be culled.
     */
    Set<Direction> getFaces();
}
