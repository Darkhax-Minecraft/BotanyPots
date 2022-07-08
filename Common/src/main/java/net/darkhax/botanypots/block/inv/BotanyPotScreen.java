package net.darkhax.botanypots.block.inv;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.crop.CropInfo;
import net.darkhax.botanypots.data.soil.SoilInfo;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class BotanyPotScreen extends AbstractContainerScreen<BotanyPotMenu> {

    private static final NumberFormat MULTIPLIER_FORMAT = new DecimalFormat("##.##");
    private static final Component NEW_LINE = new TextComponent("");
    private static final Component TOOLTIP_INVALID_SOIL = new TranslatableComponent("tooltip.botanypots.invalid_soil").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_INVALID_CROP = new TranslatableComponent("tooltip.botanypots.invalid_seed").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_INCORRECT_SOIL = new TranslatableComponent("tooltip.botanypots.incorrect_soil").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_INCORRECT_SEED = new TranslatableComponent("tooltip.botanypots.incorrect_seed").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_MISSING_SOIL = new TranslatableComponent("tooltip.botanypots.missing_soil").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_MISSING_SEED = new TranslatableComponent("tooltip.botanypots.missing_seed").withStyle(ChatFormatting.RED);

    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private final ResourceLocation backgroundTexture;

    public BotanyPotScreen(BotanyPotMenu menu, Inventory playerInv, Component name) {

        super(menu, playerInv, name);
        this.backgroundTexture = new ResourceLocation(Constants.MOD_ID, "textures/gui/container/" + (menu.isHopper() ? "hopper_botany_pot_gui.png" : "botany_pot_gui.png"));
    }

    @Override
    public void init() {

        super.init();

        int recipeOffset = this.leftPos + (this.menu.isHopper() ? 13 : 33);
        this.addRenderableWidget(new ImageButton(recipeOffset, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (btn) -> {}));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float v, int i, int i1) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.backgroundTexture);
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float tickDelta) {

        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, tickDelta);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    public List<Component> getTooltipFromItem(ItemStack stack) {

        final List<Component> tooltips = super.getTooltipFromItem(stack);

        if (this.minecraft != null) {

            final boolean isAdvanced = this.minecraft.options.advancedItemTooltips;
            final BlockEntityBotanyPot pot = this.menu.getPotInventory().getPotEntity();;

            // Add soil slot tooltips.
            if (this.hoveredSlot instanceof SlotSoil soilSlot) {

                // Show an error if the soil is not valid for the pot. This does not
                // necessarily mean the soil is null, only that the pot does not accept
                // the current soil type.
                if (!pot.isValidSoil(stack)) {

                    tooltips.add(TOOLTIP_INVALID_SOIL);
                }

                else {

                    final SoilInfo soil = BotanyPotHelper.getSoil(pot.getLevel(), stack);

                    // Add information about how the soil behaves.
                    if (soil != null) {

                        if (pot.getCropInfo() == null) {

                            tooltips.add(TOOLTIP_MISSING_SEED);
                        }

                        else if (!BotanyPotHelper.isSoilValidForCrop(soil, pot.getCropInfo())) {

                            tooltips.add(TOOLTIP_INCORRECT_SOIL);
                        }

                        else {

                            final MutableComponent multiplier = new TextComponent(MULTIPLIER_FORMAT.format(soil.getGrowthModifier())).withStyle(soil.getGrowthModifier() > 1 ? ChatFormatting.GREEN : soil.getGrowthModifier() < 1 ? ChatFormatting.RED : ChatFormatting.GRAY);
                            tooltips.add(new TranslatableComponent("tooltip.botanypots.soil_modifier", multiplier).withStyle(ChatFormatting.GRAY));
                        }
                    }
                }
            }

            // Add seed slot tooltips.
            else if (this.hoveredSlot instanceof SlotCropSeed seedSlot) {

                if (!pot.isValidSeed(stack)) {

                    tooltips.add(TOOLTIP_INVALID_CROP);
                }

                final CropInfo crop = BotanyPotHelper.getCrop(pot.getLevel(), stack);

                if (crop != null) {

                    if (pot.getSoilInfo() == null) {

                        tooltips.add(TOOLTIP_MISSING_SOIL);
                    }

                    else if (!BotanyPotHelper.isSoilValidForCrop(pot.getSoilInfo(), crop)) {

                        tooltips.add(TOOLTIP_INCORRECT_SEED);
                    }
                }
            }

            else {

                if (BotanyPotHelper.getSoil(pot.getLevel(), stack) != null) {

                    tooltips.add(new TranslatableComponent("tooltip.botanypots.soil_item").withStyle(ChatFormatting.GREEN));
                }

                if (BotanyPotHelper.getCrop(pot.getLevel(), stack) != null) {

                    tooltips.add(new TranslatableComponent("tooltip.botanypots.seed_item").withStyle(ChatFormatting.GREEN));
                }
            }
        }

        return tooltips;
    }
}
