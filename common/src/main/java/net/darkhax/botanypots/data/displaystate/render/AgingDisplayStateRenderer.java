package net.darkhax.botanypots.data.displaystate.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.data.displaystate.AgingDisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class AgingDisplayStateRenderer extends DisplayStateRenderer<AgingDisplayState> {

    public static final AgingDisplayStateRenderer RENDERER = new AgingDisplayStateRenderer();

    private AgingDisplayStateRenderer() {

    }

    @Override
    public void render(AgingDisplayState displayState, PoseStack stack, Level level, BlockPos pos, MultiBufferSource bufferSource, int light, int overlay, float progress) {

        TransitionalDisplayStateRenderer.RENDERER.render(displayState, stack, level, pos, bufferSource, light, overlay, progress);
    }
}
