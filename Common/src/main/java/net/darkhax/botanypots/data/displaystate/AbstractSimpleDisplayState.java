package net.darkhax.botanypots.data.displaystate;

import com.mojang.math.Vector3f;
import net.darkhax.botanypots.data.displaystate.math.AxisAlignedRotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public abstract class AbstractSimpleDisplayState extends DisplayState {

    public final Optional<Vector3f> scale;
    public final Optional<Vector3f> offset;
    public final List<AxisAlignedRotation> rotations;
    public final boolean renderFluid;

    public AbstractSimpleDisplayState(Optional<Vector3f> scale, Optional<Vector3f> offset, List<AxisAlignedRotation> rotations, boolean renderFluid) {

        this.scale = scale;
        this.offset = offset;
        this.rotations = rotations;
        this.renderFluid = renderFluid;
    }

    public abstract BlockState getRenderState(float progress);
}
