package net.darkhax.botanypots.common.impl.data.display.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.common.api.data.display.render.DisplayRenderer;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.darkhax.botanypots.common.impl.data.display.types.AgingDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.PhasedDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.TransitionalDisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public final class PhasedDisplayStateRenderer<T extends PhasedDisplayState> extends DisplayRenderer<T> {

    public static final PhasedDisplayStateRenderer<AgingDisplayState> AGING = new PhasedDisplayStateRenderer<>();
    public static final PhasedDisplayStateRenderer<TransitionalDisplayState> TRANSITIONAL = new PhasedDisplayStateRenderer<>();

    @Override
    public float render(BlockEntityRendererProvider.Context renderContext, T displayState, PoseStack stack, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BotanyPotBlockEntity pot, float progress, float growthScale, float heightOffset) {
        final int phaseIndex = Math.min(Math.max((int) Math.floor(progress * (displayState.getDisplayPhases().size() - 1)), 0), displayState.getDisplayPhases().size() - 1);
        final Display currentState = displayState.getDisplayPhases().get(phaseIndex);
        return DisplayRenderer.renderState(renderContext, currentState, stack, level, pos, tickDelta, bufferSource, light, overlay, pot, progress, growthScale, heightOffset);
    }
}