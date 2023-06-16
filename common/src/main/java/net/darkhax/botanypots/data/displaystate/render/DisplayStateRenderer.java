package net.darkhax.botanypots.data.displaystate.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.data.displaystate.AgingDisplayState;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.darkhax.botanypots.data.displaystate.SimpleDisplayState;
import net.darkhax.botanypots.data.displaystate.TransitionalDisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class DisplayStateRenderer<T extends DisplayState> {

    private static final Map<ResourceLocation, DisplayStateRenderer<DisplayState>> RENDERERS = new HashMap<>();

    public static DisplayStateRenderer<DisplayState> getRenderer(DisplayState state) {

        final DisplayStateRenderer<DisplayState> renderer = getRenderer(state.getSerializer().getId());

        if (renderer != null) {

            return renderer;
        }

        throw new IllegalStateException("Display state " + state.getSerializer().getId() + " is not bound to a renderer.");
    }

    public static void renderState(DisplayState displayState, PoseStack stack, Level level, BlockPos pos, MultiBufferSource bufferSource, int light, int overlay, float progress) {

        getRenderer(displayState).render(displayState, stack, level, pos, bufferSource, light, overlay, progress);
    }

    @Nullable
    public static DisplayStateRenderer<DisplayState> getRenderer(ResourceLocation id) {

        return RENDERERS.get(id);
    }

    public abstract void render(T displayState, PoseStack stack, Level level, BlockPos pos, MultiBufferSource bufferSource, int light, int overlay, float progress);

    public static void init() {

        RENDERERS.put(SimpleDisplayState.ID, (DisplayStateRenderer) SimpleDisplayStateRenderer.RENDERER);
        RENDERERS.put(TransitionalDisplayState.ID, (DisplayStateRenderer) TransitionalDisplayStateRenderer.RENDERER);
        RENDERERS.put(AgingDisplayState.ID, (DisplayStateRenderer) AgingDisplayStateRenderer.RENDERER);
    }
}