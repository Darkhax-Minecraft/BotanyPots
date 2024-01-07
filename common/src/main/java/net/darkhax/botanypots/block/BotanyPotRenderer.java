package net.darkhax.botanypots.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.data.displaystate.math.AxisAlignedRotation;
import net.darkhax.botanypots.data.displaystate.render.DisplayStateRenderer;
import net.darkhax.botanypots.data.displaystate.types.DisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BotanyPotRenderer implements BlockEntityRenderer<BlockEntityBotanyPot> {

    private final BlockEntityRendererProvider.Context renderContext;

    public BotanyPotRenderer(BlockEntityRendererProvider.Context ctx) {

        this.renderContext = ctx;
    }

    private static AxisAlignedRotation rotateFacing(Direction facing) {

        if (facing == Direction.EAST) {
            return AxisAlignedRotation.Y_90;
        }
        if (facing == Direction.NORTH) {
            return AxisAlignedRotation.Y_180;
        }
        if (facing == Direction.WEST) {
            return AxisAlignedRotation.Y_270;
        }
        return null;
    }

    public static void applyRotation(AxisAlignedRotation rotation, PoseStack pose) {

        // Applies the rotation to the render.
        pose.mulPose(rotation.rotation);

        // Realigns the render with the original axis-aligned position.
        pose.translate(rotation.offset.x(), rotation.offset.y(), rotation.offset.z());
    }

    @Override
    public void render(BlockEntityBotanyPot pot, float tickDelta, PoseStack pose, MultiBufferSource bufferSource, int light, int overlay) {

        if (pot.getSoil() != null) {

            final Level level = pot.getLevel();
            final BlockPos pos = pot.getBlockPos();

            final int maxGrowth = pot.getInventory().getRequiredGrowthTime();
            // We force this to 0 when the crop is done growing so it doesn't pulse in and out when it's done growing.
            final float partialOffset = pot.getGrowthTime() < maxGrowth ? tickDelta : 0f;
            final float growthProgress = Math.max((pot.getGrowthTime() + partialOffset) / maxGrowth, 0f);
            final AxisAlignedRotation potRotation = rotateFacing(pot.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));

            pose.pushPose();

            if (potRotation != null) {
                applyRotation(potRotation, pose);
            }

            pose.scale(0.625f, 0.375f, 0.625f);
            pose.translate(0.3, 0.0625, 0.3);

            DisplayStateRenderer.renderState(this.renderContext, pot.getSoil().getDisplayState(level, pos, pot), pose, level, pos, partialOffset, bufferSource, light, overlay, pot, growthProgress);

            pose.popPose();

            if (pot.getCrop() != null && BotanyPotHelper.canCropGrow(level, pos, pot, pot.getSoil(), pot.getCrop())) {

                pose.pushPose();

                if (potRotation != null) {
                    applyRotation(potRotation, pose);
                }

                pose.translate(0.5, 0.40625, 0.5);

                {
                    final float cropScale = Mth.clamp((0.40f + growthProgress * 0.60f) * 0.625f, 0.15625f, 0.625f);
                    pose.scale(cropScale, cropScale, cropScale);
                }

                pose.translate(-0.5, 0, -0.5);

                int previousBlocks = 0;

                for (DisplayState state : pot.getCrop().getDisplayState(level, pos, pot)) {

                    if (previousBlocks > 0) {

                        pose.translate(0, 1, 0);
                    }

                    pose.pushPose();
                    DisplayStateRenderer.renderState(this.renderContext, state, pose, pot.getLevel(), pot.getBlockPos(), partialOffset, bufferSource, light, overlay, pot, growthProgress);
                    pose.popPose();

                    previousBlocks++;
                }

                pose.popPose();
            }
        }
    }
}