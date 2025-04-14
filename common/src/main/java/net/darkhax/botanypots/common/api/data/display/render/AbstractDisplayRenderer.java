package net.darkhax.botanypots.common.api.data.display.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.common.api.data.display.math.AxisAlignedRotation;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.RenderOptions;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

/**
 * A helpful base to build a display renderer. It handles stuff like scale, rotation, and offset.
 *
 * @param <T> The display type to render.
 * @param <O> The configurable render options.
 */
public abstract class AbstractDisplayRenderer<T extends Display, O extends RenderOptions> extends DisplayRenderer<T> {

    @Override
    public float render(BlockEntityRendererProvider.Context context, T display, PoseStack pose, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BotanyPotBlockEntity pot, float progress, float growthScale, float heightOffset) {
        final O renderOptions = this.getRenderOptions(display);
        final Vector3f scale = renderOptions.getScale();
        final float scaleX = scale.x * growthScale;
        final float scaleY = scale.y * growthScale;
        final float scaleZ = scale.z * growthScale;
        final Vector3f offset = renderOptions.getOffset();

        pose.pushPose();
        pose.translate(0.5f - (scaleX / 2f), heightOffset, 0.5f - (scaleZ / 2f)); // Center render based on user scale.
        pose.translate(offset.x * scaleX, offset.y * scaleY, offset.z * scaleZ); // Apply user offset.
        pose.scale(scaleX, scaleY, scaleZ); // Apply user scale
        for (AxisAlignedRotation rotation : renderOptions.getRotations()) {
            pose.mulPose(rotation.rotation); // Rotate the render
            pose.translate(rotation.offset.x(), rotation.offset.y(), rotation.offset.z()); // Recenter the render.
        }
        this.render(context, display, renderOptions, pose, level, pos, tickDelta, bufferSource, light, overlay, pot, progress, growthScale, heightOffset);
        pose.popPose();

        heightOffset += (offset.y * scaleY);
        heightOffset += this.getHeight(display, renderOptions, level, pos, tickDelta, pot) * scaleY;
        return heightOffset;
    }

    /**
     * Gets the render options to apply from the display.
     *
     * @param display The display to render.
     * @return The options to use when rendering.
     */
    public abstract O getRenderOptions(T display);

    /**
     * Calculates the height of the render. This is used to apply an offset to future renders, making sure they render
     * directly on top of the previous render.
     *
     * @param display       The display being rendered.
     * @param renderOptions User defined rendering options used to configure the renderer.
     * @param level         The current level, will always be a client level.
     * @param pos           The position of the pot being rendered.
     * @param tickDelta     The delta between frames.
     * @param pot           The pot block entity.
     * @return The height of the render.
     */
    public float getHeight(T display, O renderOptions, Level level, BlockPos pos, float tickDelta, BotanyPotBlockEntity pot) {
        return 1f;
    }

    /**
     * Renders the display in the world.
     *
     * @param context       Context provided when rendering a block entity.
     * @param display       The display to render.
     * @param renderOptions User defined rendering options used to configure the renderer.
     * @param pose          The pose stack for the renderer.
     * @param level         The current level, will always be a client level.
     * @param pos           The position of te block being rendered.
     * @param tickDelta     The delta between frames.
     * @param bufferSource  Buffer source for rendering.
     * @param light         Packed light of the block.
     * @param overlay       Packed overlay of the block.
     * @param pot           The pot block entity.
     * @param progress      The growth progress of the crop.
     * @param growthScale   The scale based on current growth progress.
     * @param heightOffset  A height offset used to make displays stack perfectly on top of each other.
     */
    public abstract void render(BlockEntityRendererProvider.Context context, T display, O renderOptions, PoseStack pose, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BotanyPotBlockEntity pot, float progress, float growthScale, float heightOffset);
}