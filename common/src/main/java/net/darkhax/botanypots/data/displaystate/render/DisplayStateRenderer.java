package net.darkhax.botanypots.data.displaystate.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.displaystate.DisplayTypes;
import net.darkhax.botanypots.data.displaystate.types.DisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
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

    public static void renderState(BlockEntityRendererProvider.Context context, DisplayState displayState, PoseStack stack, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BlockEntityBotanyPot pot, float progress) {

        ((DisplayStateRenderer) getRenderer(displayState)).render(context, displayState, stack, level, pos, tickDelta, bufferSource, light, overlay, pot, progress);
    }

    public abstract void render(BlockEntityRendererProvider.Context context, T displayState, PoseStack stack, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BlockEntityBotanyPot pot, float progress);

    public static void init() {

        RENDERERS.put(DisplayTypes.SIMPLE, SimpleDisplayStateRenderer.RENDERER);
        RENDERERS.put(DisplayTypes.TRANSITIONAL, PhasedDisplayStateRenderer.TRANSITIONAL);
        RENDERERS.put(DisplayTypes.AGING, PhasedDisplayStateRenderer.AGING);
        RENDERERS.put(DisplayTypes.ENTITY, EntityDisplayStateRenderer.RENDERER);
    }
}