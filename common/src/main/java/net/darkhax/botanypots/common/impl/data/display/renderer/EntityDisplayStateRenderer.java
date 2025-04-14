package net.darkhax.botanypots.common.impl.data.display.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.darkhax.botanypots.common.api.data.display.render.DisplayRenderer;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.darkhax.botanypots.common.impl.data.display.types.EntityDisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class EntityDisplayStateRenderer extends DisplayRenderer<EntityDisplayState> {

    public static final EntityDisplayStateRenderer RENDERER = new EntityDisplayStateRenderer();

    @Override
    public float render(BlockEntityRendererProvider.Context context, EntityDisplayState displayState, PoseStack pose, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BotanyPotBlockEntity pot, float progress, float cropScale, float heightOffset) {
        final Entity displayEntity = displayState.getDisplayEntity().apply(level);
        if (displayEntity != null) {
            if (displayState.shouldTickEntity()) {
                displayEntity.tickCount = (int) (level.getGameTime() % Integer.MAX_VALUE);
            }
            pose.pushPose();
            pose.translate(0.5, 0, 0.5);
            final Vector3f scale = displayState.getScale();
            pose.scale(scale.x(), scale.y(), scale.z());
            displayState.getOffset().ifPresent(offset -> pose.translate(offset.x(), offset.y(), offset.z()));
            if (displayState.getSpinSpeed() > 0) {
                pose.mulPose(Axis.YP.rotationDegrees((360f * displayState.getSpinSpeed()) * progress));
            }
            renderEntityAndPassengers(context.getEntityRenderer(), displayEntity, tickDelta, pose, bufferSource, light);
            pose.popPose();
        }
        return 0f;
    }

    private static void renderEntityAndPassengers(EntityRenderDispatcher renderer, Entity parent, float tickDelta, PoseStack pose, MultiBufferSource buffer, int light) {
        renderer.render(parent, 0, 0, 0, 0, tickDelta, pose, buffer, light);
        for (Entity passenger : parent.getPassengers()) {
            final Vec3 passengerPosition = parent.getPassengerRidingPosition(passenger);
            pose.translate(passengerPosition.x, passengerPosition.y, passengerPosition.z);
            renderEntityAndPassengers(renderer, passenger, tickDelta, pose, buffer, light);
        }
    }
}