package net.darkhax.botanypots.common.api.data.display.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders a display in a botany pot.
 *
 * @param <T> The type of display rendered.
 */
public abstract class DisplayRenderer<T extends Display> {

    private static final Map<DisplayType<?>, DisplayRenderer<?>> RENDERERS = new HashMap<>();

    /**
     * Gets the renderer to use for a given display.
     *
     * @param state The display to render.
     * @return A renderer for the display.
     */
    public static DisplayRenderer<?> getRenderer(Display state) {
        final DisplayRenderer<?> renderer = RENDERERS.get(state.getType());
        if (renderer == null) {
            throw new IllegalStateException("Display state " + state.getType().typeId() + " is not bound to a renderer.");
        }
        return renderer;
    }

    /**
     * Renders a display in world.
     *
     * @param context      Context provided when rendering a block entity.
     * @param displayState The display to render.
     * @param stack        The pose stack for the renderer.
     * @param level        The current level, will always be a client level.
     * @param pos          The position of te block being rendered.
     * @param tickDelta    The delta between frames.
     * @param bufferSource Buffer source for rendering.
     * @param light        Packed light of the block.
     * @param overlay      Packed overlay of the block.
     * @param pot          The pot block entity.
     * @param progress     The growth progress of the crop.
     * @param growthScale  The scale based on current growth progress.
     * @param heightOffset A height offset used to make displays stack perfectly on top of each other.
     * @return The new height offset for the next display.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static float renderState(BlockEntityRendererProvider.Context context, Display displayState, PoseStack stack, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BotanyPotBlockEntity pot, float progress, float growthScale, float heightOffset) {
        return ((DisplayRenderer) getRenderer(displayState)).render(context, displayState, stack, level, pos, tickDelta, bufferSource, light, overlay, pot, progress, growthScale, heightOffset);
    }

    /**
     * Renders the display in the world.
     *
     * @param context      Context provided when rendering a block entity.
     * @param displayState The display to render.
     * @param stack        The pose stack for the renderer.
     * @param level        The current level, will always be a client level.
     * @param pos          The position of te block being rendered.
     * @param tickDelta    The delta between frames.
     * @param bufferSource Buffer source for rendering.
     * @param light        Packed light of the block.
     * @param overlay      Packed overlay of the block.
     * @param pot          The pot block entity.
     * @param progress     The growth progress of the crop.
     * @param growthScale  The scale based on current growth progress.
     * @param heightOffset A height offset used to make displays stack perfectly on top of each other.
     * @return The new height offset for the next display.
     */
    public abstract float render(BlockEntityRendererProvider.Context context, T displayState, PoseStack stack, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BotanyPotBlockEntity pot, float progress, float growthScale, float heightOffset);

    /**
     * Binds a renderer to a display type. Built-in types are bound during the same phase as vanilla tile entity
     * renderers. Binding a type that has already been bound will log a warning but still override the previous
     * renderer.
     *
     * @param type     The type to bind.
     * @param renderer The renderer to use.
     * @param <T>      The display type.
     */
    public static <T extends Display> void bind(DisplayType<T> type, DisplayRenderer<T> renderer) {
        if (RENDERERS.containsKey(type)) {
            BotanyPotsMod.LOG.warn("Renderer already bound for type " + type.typeId() + ". Replacing " + RENDERERS.get(type).toString() + " with " + renderer.toString());
        }
        RENDERERS.put(type, renderer);
    }
}