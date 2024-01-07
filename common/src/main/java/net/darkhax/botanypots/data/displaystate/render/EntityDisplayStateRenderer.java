package net.darkhax.botanypots.data.displaystate.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.displaystate.types.EntityDisplayState;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityDisplayStateRenderer extends DisplayStateRenderer<EntityDisplayState> {

    public static final EntityDisplayStateRenderer RENDERER = new EntityDisplayStateRenderer();

    private EntityDisplayStateRenderer() {

    }

    @Override
    public void render(BlockEntityRendererProvider.Context context, EntityDisplayState displayState, PoseStack pose, Level level, BlockPos pos, float tickDelta, MultiBufferSource bufferSource, int light, int overlay, BlockEntityBotanyPot pot, float progress) {

        final Entity displayEntity = displayState.getOrCreateDisplayEntity(level, level.getRandom(), pos);

        if (displayEntity != null) {

            if (displayState.shouldTickEntity()) {
                displayEntity.tickCount = (int) (level.getGameTime() % Integer.MAX_VALUE);
            }

            pose.pushPose();
            pose.translate(0.5, 0, 0.5);
            displayState.getScale().ifPresent(scale -> pose.scale(scale.x(), scale.y(), scale.z()));
            displayState.getOffset().ifPresent(offset -> pose.translate(offset.x(), offset.y(), offset.z()));

            if (displayState.getSpinSpeed() > 0) {

                pose.mulPose(Axis.YP.rotationDegrees((360f * displayState.getSpinSpeed()) * progress));
            }

            renderEntityAndPassengers(context.getEntityRenderer(), displayEntity, tickDelta, pose, bufferSource, light);
            pose.popPose();
        }
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
