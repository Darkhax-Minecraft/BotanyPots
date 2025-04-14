package net.darkhax.botanypots.common.impl.data.display.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.bookshelf.common.api.util.IRenderHelper;
import net.darkhax.botanypots.common.api.data.display.math.TintColor;
import net.darkhax.botanypots.common.api.data.display.render.AbstractDisplayRenderer;
import net.darkhax.botanypots.common.api.data.display.types.RenderOptions;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.darkhax.botanypots.common.impl.data.display.types.TexturedCubeDisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class TexturedCubeStateRenderer extends AbstractDisplayRenderer<TexturedCubeDisplayState, RenderOptions> {

    private static final int[] WHITE = {255, 255, 255, 255};
    public static final TexturedCubeStateRenderer RENDERER = new TexturedCubeStateRenderer();

    @Override
    public RenderOptions getRenderOptions(TexturedCubeDisplayState display) {
        return display.renderOptions();
    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, TexturedCubeDisplayState display, RenderOptions renderOptions, PoseStack pose, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BotanyPotBlockEntity pot, float progress, float growthScale, float heightOffset) {
        IRenderHelper.GET.renderBox(bufferSource.getBuffer(RenderType.translucent()), pose, IRenderHelper.GET.blockSprite(display.getTexture()), light, overlay, display.renderOptions().getColor().orElse(TintColor.WHITE).asArray());
    }
}