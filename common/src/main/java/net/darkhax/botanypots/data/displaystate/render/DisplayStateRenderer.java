package net.darkhax.botanypots.data.displaystate.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.data.displaystate.DisplayTypes;
import net.darkhax.botanypots.data.displaystate.types.DisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public abstract class DisplayStateRenderer<T extends DisplayState> {

    private static final Map<DisplayTypes.DisplayType<?>, DisplayStateRenderer<?>> RENDERERS = new HashMap<>();

    public static DisplayStateRenderer<?> getRenderer(DisplayState state) {

        final DisplayStateRenderer<?> renderer = RENDERERS.get(state.getType());

        if (renderer == null) {

            throw new IllegalStateException("Display state " + state.getType().id() + " is not bound to a renderer.");
        }

        return renderer;
    }

    public static void renderState(DisplayState displayState, PoseStack stack, Level level, BlockPos pos, MultiBufferSource bufferSource, int light, int overlay, float progress) {

        ((DisplayStateRenderer) getRenderer(displayState)).render(displayState, stack, level, pos, bufferSource, light, overlay, progress);
    }

    public abstract void render(T displayState, PoseStack stack, Level level, BlockPos pos, MultiBufferSource bufferSource, int light, int overlay, float progress);

    public static void init() {

        RENDERERS.put(DisplayTypes.SIMPLE, SimpleDisplayStateRenderer.RENDERER);
        RENDERERS.put(DisplayTypes.TRANSITIONAL, PhasedDisplayStateRenderer.TRANSITIONAL);
        RENDERERS.put(DisplayTypes.AGING, PhasedDisplayStateRenderer.AGING);
    }
}