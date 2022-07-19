package net.darkhax.botanypots.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.darkhax.botanypots.data.displaystate.render.DisplayStateRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;

public class BotanyPotRenderer implements BlockEntityRenderer<BlockEntityBotanyPot> {

    public BotanyPotRenderer(BlockEntityRendererProvider.Context ctx) {

    }

    @Override
    public void render(BlockEntityBotanyPot pot, float tickDelta, PoseStack pose, MultiBufferSource bufferSource, int light, int overlay) {

        if (pot.getSoilInfo() != null) {

            final int maxGrowth = pot.getInventory().getRequiredGrowthTime();
            final float partialOffset = pot.getGrowthTime() < maxGrowth ? tickDelta : 0f;
            final float growthProgress = Math.max((pot.getGrowthTime() + partialOffset) / maxGrowth, 0f);

            pose.pushPose();

            pose.scale(0.625f, 0.375f, 0.625f);
            pose.translate(0.3, 0.0625, 0.3);

            DisplayStateRenderer.renderState(pot.getSoilInfo().getRenderState(), pose, pot.getLevel(), pot.getBlockPos(), bufferSource, light, overlay, growthProgress);

            pose.popPose();

            if (pot.getCropInfo() != null && BotanyPotHelper.isSoilValidForCrop(pot.getSoilInfo(), pot.getCropInfo())) {

                pose.pushPose();

                pose.translate(0.5, 0.40625, 0.5);

                {
                    final float cropScale = Mth.clamp((0.25f + growthProgress * 0.75f) * 0.625f, 0.15625f, 0.625f);
                    pose.scale(cropScale, cropScale, cropScale);
                }

                pose.translate(-0.5, 0, -0.5);

                int previousBlocks = 0;

                for (DisplayState state : pot.getCropInfo().getDisplayState()) {

                    if (previousBlocks > 0) {

                        pose.translate(0, 1, 0);
                    }

                    pose.pushPose();
                    DisplayStateRenderer.renderState(state, pose, pot.getLevel(), pot.getBlockPos(), bufferSource, light, overlay, growthProgress);
                    pose.popPose();

                    previousBlocks++;
                }

                pose.popPose();
            }
        }
    }
}
