package net.darkhax.botanypots.tempshelf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class DisplayableBlockState implements IDisplayableBlockstate {

    private BlockState state;
    private Optional<Vector3f> scale;
    private Optional<Vector3f> offset;
    private boolean renderFluid;

    public DisplayableBlockState(BlockState state) {

        this(state, Optional.empty(), Optional.empty(), false);
    }

    public DisplayableBlockState(BlockState state, Optional<Vector3f> scale, Optional<Vector3f> offset, boolean renderFluid) {

        this.state = state;
        this.scale = scale;
        this.offset = offset;
        this.renderFluid = renderFluid;
    }

    @Override
    public void render(PoseStack stack, Level level, BlockPos pos, VertexConsumer buffer, int light, int overlay, int frame, Direction... visibleFaces) {

        // TODO unimplemented
    }

    public BlockState getState () {

        return this.state;
    }

    public void setState (BlockState state) {

        this.state = state;
    }

    public Optional<Vector3f> getScale () {

        return this.scale;
    }

    public void setScale (Vector3f scale) {

        this.setScale(Optional.of(scale));
    }

    public void setScale (Optional<Vector3f> scale) {

        this.scale = scale;
    }

    public Optional<Vector3f> getOffset () {

        return this.offset;
    }

    public void setOffset (Vector3f offset) {

        this.setOffset(Optional.of(offset));
    }

    public void setOffset (Optional<Vector3f> offset) {

        this.offset = offset;
    }

    public boolean isRenderFluid () {

        return this.renderFluid;
    }

    public void setRenderFluid (boolean renderFluid) {

        this.renderFluid = renderFluid;
    }
}