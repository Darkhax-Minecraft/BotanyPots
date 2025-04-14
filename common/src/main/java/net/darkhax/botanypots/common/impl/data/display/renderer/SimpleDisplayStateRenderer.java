package net.darkhax.botanypots.common.impl.data.display.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.darkhax.bookshelf.common.api.util.IRenderHelper;
import net.darkhax.botanypots.common.api.data.display.math.TintColor;
import net.darkhax.botanypots.common.api.data.display.render.AbstractDisplayRenderer;
import net.darkhax.botanypots.common.api.data.display.types.RenderOptions;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.darkhax.botanypots.common.impl.data.display.types.SimpleDisplayState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class SimpleDisplayStateRenderer extends AbstractDisplayRenderer<SimpleDisplayState, RenderOptions> {

    public static final SimpleDisplayStateRenderer RENDERER = new SimpleDisplayStateRenderer();

    @Override
    public RenderOptions getRenderOptions(SimpleDisplayState display) {
        return display.renderOptions();
    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, SimpleDisplayState display, RenderOptions renderOptions, PoseStack pose, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BotanyPotBlockEntity pot, float progress, float growthScale, float heightOffset) {
        final BlockState blockState = display.getState();
        if (display.renderOptions().shouldRenderFluid()) {
            final FluidState fluidState = blockState.getFluidState();
            if (!fluidState.isEmpty()) {
                IRenderHelper.GET.renderFluidBox(pose, fluidState, level, pos, bufferSource, light, OverlayTexture.NO_OVERLAY);
            }
        }
        renderBlockState(blockState, context, pose, level, pos, bufferSource, display.renderOptions().getColor(), light, overlay, renderOptions.getFaces());
    }

    public static void renderBlockState(BlockState blockState, BlockEntityRendererProvider.Context context, PoseStack pose, Level level, BlockPos pos, MultiBufferSource bufferSource, Optional<TintColor> tintColor, int light, int overlay, Set<Direction> faces) {
        if (blockState.getRenderShape() == RenderShape.MODEL) {
            final BakedModel blockModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(blockState);
            final VertexConsumer builder = bufferSource.getBuffer(RenderType.cutout());
            renderModel(blockState, level, pos, pose.last(), builder, blockModel, tintColor, light, OverlayTexture.NO_OVERLAY, faces);
        }
    }

    private static void renderModel(BlockState blockState, Level level, BlockPos pos, PoseStack.Pose pose, VertexConsumer consumer, BakedModel model, Optional<TintColor> tintColor, int packedLight, int packedOverlay, Set<Direction> faces) {
        RandomSource randomsource = RandomSource.create();
        for (Direction direction : faces) {
            randomsource.setSeed(42);
            renderQuadList(blockState, level, pos, pose, consumer, tintColor, model.getQuads(blockState, direction, randomsource), packedLight, packedOverlay);
        }
        randomsource.setSeed(42);
        renderQuadList(blockState, level, pos, pose, consumer, tintColor, model.getQuads(blockState, null, randomsource), packedLight, packedOverlay);
    }

    private static void renderQuadList(BlockState blockState, Level level, BlockPos pos, PoseStack.Pose pose, VertexConsumer consumer, Optional<TintColor> tintColor, List<BakedQuad> quads, int packedLight, int packedOverlay) {
        for (BakedQuad bakedQuad : quads) {
            float[] argb = {1f, 1f, 1f, 1f};
            if (tintColor.isPresent()) {
                argb = tintColor.get().asPercents();
            }
            else if (bakedQuad.isTinted()) {
                final int worldTint = Minecraft.getInstance().getBlockColors().getColor(blockState, level, pos, bakedQuad.getTintIndex());
                argb = new float[]{1f, (float) (worldTint >> 16 & 0xFF) / 255f, (float) (worldTint >> 8 & 0xFF) / 255f, (float) (worldTint & 0xFF) / 255f};
            }
            consumer.putBulkData(pose, bakedQuad, argb[1], argb[2], argb[3], argb[0], packedLight, packedOverlay);
        }
    }
}