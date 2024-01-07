package net.darkhax.botanypots.data.displaystate.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.displaystate.types.AgingDisplayState;
import net.darkhax.botanypots.data.displaystate.types.DisplayState;
import net.darkhax.botanypots.data.displaystate.types.PhasedDisplayState;
import net.darkhax.botanypots.data.displaystate.types.TransitionalDisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class PhasedDisplayStateRenderer<T extends PhasedDisplayState> extends DisplayStateRenderer<T> {

    public static final PhasedDisplayStateRenderer<AgingDisplayState> AGING = new PhasedDisplayStateRenderer<>();
    public static final PhasedDisplayStateRenderer<TransitionalDisplayState> TRANSITIONAL = new PhasedDisplayStateRenderer<>();

    private PhasedDisplayStateRenderer() {

    }

    @Override
    public void render(BlockEntityRendererProvider.Context renderContext, T displayState, PoseStack stack, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BlockEntityBotanyPot pot, float progress) {

        final int phaseIndex = Math.min(Mth.floor(displayState.getDisplayPhases().size() * progress), displayState.getDisplayPhases().size() - 1);
        final DisplayState currentState = displayState.getDisplayPhases().get(phaseIndex);
        DisplayStateRenderer.renderState(renderContext, currentState, stack, level, pos, tickDelta, bufferSource, light, overlay, pot, progress);
    }
}