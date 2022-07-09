package net.darkhax.botanypots.data.displaystate.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.darkhax.botanypots.data.displaystate.TransitionalDisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class TransitionalDisplayStateRenderer extends DisplayStateRenderer<TransitionalDisplayState> {

    public static final TransitionalDisplayStateRenderer RENDERER = new TransitionalDisplayStateRenderer();

    private TransitionalDisplayStateRenderer() {

    }

    @Override
    public void render(TransitionalDisplayState displayState, PoseStack stack, Level level, BlockPos pos, MultiBufferSource bufferSource, int light, int overlay, float progress) {

        final int phaseIndex = Math.min(Mth.floor(displayState.phases.size() * progress), displayState.phases.size() - 1);
        final DisplayState currentState = displayState.phases.get(phaseIndex);
        DisplayStateRenderer.getRenderer(currentState).render(currentState, stack, level, pos, bufferSource, light, overlay, progress);
    }
}
