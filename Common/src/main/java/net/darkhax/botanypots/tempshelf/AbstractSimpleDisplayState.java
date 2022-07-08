package net.darkhax.botanypots.tempshelf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.darkhax.botanypots.tempshelf.math.AxisAlignedRotation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Optional;

public abstract class AbstractSimpleDisplayState extends DisplayState {

    protected Optional<Vector3f> scale;
    protected Optional<Vector3f> offset;
    protected List<AxisAlignedRotation> rotations;
    protected boolean renderFluid;

    public AbstractSimpleDisplayState(Optional<Vector3f> scale, Optional<Vector3f> offset, List<AxisAlignedRotation> rotations, boolean renderFluid) {

        this.scale = scale;
        this.offset = offset;
        this.rotations = rotations;
        this.renderFluid = renderFluid;
    }

    @Override
    public void render(PoseStack pose, Level level, BlockPos pos, MultiBufferSource bufferSource, int light, int overlay, float progress) {

        pose.pushPose();

        this.scale.ifPresent(scale -> pose.scale(scale.x(), scale.y(), scale.z()));
        this.offset.ifPresent(offset -> pose.translate(offset.x(), offset.y(), offset.z()));

        for (AxisAlignedRotation rotation : rotations) {

            rotation.apply(pose);
        }

        // TODO color is wrong
        // TODO water does not render
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(this.getRenderState(progress), pose, bufferSource, light, overlay);

        pose.popPose();
    }

    public abstract BlockState getRenderState(float progress);
}
