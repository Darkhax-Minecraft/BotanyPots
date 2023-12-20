package net.darkhax.botanypots.data.displaystate.types;

import net.darkhax.botanypots.data.displaystate.math.AxisAlignedRotation;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public abstract class AbstractSimpleDisplayState extends DisplayState {

    private final Optional<Vector3f> scale;
    private final Optional<Vector3f> offset;
    private final List<AxisAlignedRotation> rotations;
    private final boolean renderFluid;

    public AbstractSimpleDisplayState(Optional<Vector3f> scale, Optional<Vector3f> offset, List<AxisAlignedRotation> rotations, boolean renderFluid) {

        this.scale = scale;
        this.offset = offset;
        this.rotations = rotations;
        this.renderFluid = renderFluid;
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
}