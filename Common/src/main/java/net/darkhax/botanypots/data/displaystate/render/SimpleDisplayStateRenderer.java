package net.darkhax.botanypots.data.displaystate.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.data.displaystate.SimpleDisplayState;
import net.darkhax.botanypots.data.displaystate.math.AxisAlignedRotation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class SimpleDisplayStateRenderer extends DisplayStateRenderer<SimpleDisplayState> {

    public static final SimpleDisplayStateRenderer RENDERER = new SimpleDisplayStateRenderer();

    private SimpleDisplayStateRenderer() {

    }

    @Override
    public void render(SimpleDisplayState displayState, PoseStack pose, Level level, BlockPos pos, MultiBufferSource bufferSource, int light, int overlay, float progress) {

        pose.pushPose();

        displayState.scale.ifPresent(scale -> pose.scale(scale.x(), scale.y(), scale.z()));
        displayState.offset.ifPresent(offset -> pose.translate(offset.x(), offset.y(), offset.z()));

        for (AxisAlignedRotation rotation : displayState.rotations) {

            // Applies the rotation to the render.
            pose.mulPose(rotation.rotation);

            // Realigns the render with the original axis-aligned position.
            pose.translate(rotation.offset.x(), rotation.offset.y(), rotation.offset.z());
        }

        final BlockState blockState = displayState.getRenderState(progress);

        // TODO color is wrong
        // TODO water does not render
        if (displayState.renderFluid) {

            final FluidState fluidState = blockState.getFluidState();

            if (fluidState != null && !fluidState.isEmpty()) {

                //Minecraft.getInstance().getBlockRenderer().renderLiquid(pos, )
            }
        }

        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockState, pose, bufferSource, light, OverlayTexture.NO_OVERLAY);

        pose.popPose();
    }
}
