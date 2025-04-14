package net.darkhax.botanypots.common.impl.block;

import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.bookshelf.common.api.util.IRenderHelper;
import net.darkhax.botanypots.common.api.data.display.math.AxisAlignedRotation;
import net.darkhax.botanypots.common.api.data.display.render.DisplayRenderer;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.darkhax.botanypots.common.impl.data.display.types.BasicOptions;
import net.darkhax.botanypots.common.impl.data.display.types.SimpleDisplayState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BotanyPotRenderer implements BlockEntityRenderer<BotanyPotBlockEntity> {

    private static final Map<Item, Display> DEFAULT_SOIL_DISPLAYS = new HashMap<>();
    private final BlockEntityRendererProvider.Context renderContext;
    private final IRenderHelper helper;

    public BotanyPotRenderer(BlockEntityRendererProvider.Context ctx) {
        this.renderContext = ctx;
        this.helper = IRenderHelper.GET;
    }

    private static AxisAlignedRotation getRotationForFace(Direction facing) {
        return switch (facing) {
            case EAST -> AxisAlignedRotation.Y_90;
            case NORTH -> AxisAlignedRotation.Y_180;
            case WEST -> AxisAlignedRotation.Y_270;
            default -> AxisAlignedRotation.Y_0;
        };
    }

    public static void applyRotation(AxisAlignedRotation rotation, PoseStack pose) {
        // Applies the rotation to the render.
        pose.mulPose(rotation.rotation);
        // Realigns the render with the original axis-aligned position.
        pose.translate(rotation.offset.x(), rotation.offset.y(), rotation.offset.z());
    }

    private static boolean canRenderSoil(ItemStack stack, @Nullable RecipeHolder<Soil> soil) {
        return !stack.isEmpty() && (soil != null || stack.getItem() instanceof BlockItem);
    }

    @Override
    public void render(BotanyPotBlockEntity pot, float tickDelta, @NotNull PoseStack pose, @NotNull MultiBufferSource bufferSource, int light, int overlay) {
        final Level level = pot.getLevel();
        if (level == null) {
            return;
        }
        final BlockPos pos = pot.getBlockPos();
        final int maxGrowth = pot.getRequiredGrowthTicks();
        final float progress = Math.max(Mth.lerp(tickDelta, pot.growthTime.getTicks() - 1f, pot.growthTime.getTicks()) / maxGrowth, 0f);
        final AxisAlignedRotation baseRotation = getRotationForFace(pot.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING));
        final Soil soil = pot.getOrInvalidateSoil();
        final Crop crop = pot.getOrInvalidateCrop();
        final boolean isGrowing = crop != null && crop.isGrowthSustained(pot.getRecipeContext(), level);
        final ItemStack soilItem = pot.getSoilItem();

        final float cropScale = 0.40f + Math.clamp(0.60f * progress, 0f, 0.60f);
        float lastOffset = 0.3984375f;

        if (isGrowing) {
            for (Display state : crop.getDisplayState(pot.getRecipeContext(), pot.getLevel())) {
                lastOffset = DisplayRenderer.renderState(this.renderContext, state, pose, level, pos, tickDelta, bufferSource, light, overlay, pot, progress, cropScale, lastOffset);
            }
        }

        if (!soilItem.isEmpty() && Minecraft.getInstance().getBlockEntityRenderDispatcher().camera.getPosition().y >= pos.getY()) {
            if (soil != null || (crop != null && soilItem.getItem() instanceof BlockItem && isGrowing)) {
                pose.pushPose();
                applyRotation(baseRotation, pose);
                pose.scale(1, 0.6375f, 1f);
                final Display soilRender = soil != null ? soil.getDisplay(pot.getRecipeContext(), level) : soilItem.getItem() instanceof BlockItem blockItem ? DEFAULT_SOIL_DISPLAYS.computeIfAbsent(blockItem, i -> new SimpleDisplayState(blockItem.getBlock().defaultBlockState(), BasicOptions.ofDefault())) : null;
                if (soilRender != null) {
                    DisplayRenderer.renderState(this.renderContext, soilRender, pose, level, pos, tickDelta, bufferSource, light, overlay, pot, progress, 1f, 0f);
                }
                pose.popPose();
            }
        }
    }

    @Override
    public int getViewDistance() {
        return 40;
    }
}