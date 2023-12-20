package net.darkhax.botanypots.block.inv;

import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

public class BotanyPotScreen extends AbstractContainerScreen<BotanyPotMenu> {

    private static final NumberFormat MULTIPLIER_FORMAT = new DecimalFormat("##.##");
    private static final Component NEW_LINE = Component.literal("");
    private static final Component TOOLTIP_INVALID_SOIL = Component.translatable("tooltip.botanypots.invalid_soil").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_INVALID_CROP = Component.translatable("tooltip.botanypots.invalid_seed").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_INCORRECT_SOIL = Component.translatable("tooltip.botanypots.incorrect_soil").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_INCORRECT_SEED = Component.translatable("tooltip.botanypots.incorrect_seed").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_MISSING_SOIL = Component.translatable("tooltip.botanypots.missing_soil").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_MISSING_SEED = Component.translatable("tooltip.botanypots.missing_seed").withStyle(ChatFormatting.RED);
    private static final Component TOOLTIP_SOIL_ITEM = Component.translatable("tooltip.botanypots.soil_item").withStyle(ChatFormatting.GREEN);
    private static final Component TOOLTIP_SEED_ITEM = Component.translatable("tooltip.botanypots.seed_item").withStyle(ChatFormatting.GREEN);

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
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int i, int i1) {

        graphics.blit(this.backgroundTexture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float tickDelta) {

        this.renderBackground(graphics, mouseX, mouseY, tickDelta);
        super.render(graphics, mouseX, mouseY, tickDelta);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected List<Component> getTooltipFromContainerItem(ItemStack stack) {

        final List<Component> tooltips = super.getTooltipFromContainerItem(stack);

        if (this.minecraft != null) {

            final boolean isAdvanced = this.minecraft.options.advancedItemTooltips;
            final BlockEntityBotanyPot pot = this.menu.getPotInventory().getPotEntity();
            final Level level = pot.getLevel();
            final BlockPos pos = pot.getBlockPos();

            // Add soil slot tooltips.
            if (this.hoveredSlot instanceof SlotSoil soilSlot) {

                // Show an error if the soil is not valid for the pot. This does not
                // necessarily mean the soil is null, only that the pot does not accept
                // the current soil type.
                if (!pot.isValidSoil(stack)) {

                    tooltips.add(TOOLTIP_INVALID_SOIL);
                }

                else {

                    final RecipeHolder<Soil> soilRecipe = BotanyPotHelper.findSoil(level, pos, pot, stack);

                    if (soilRecipe != null) {

                        final Soil soil = soilRecipe.value();

                        // Add information about how the soil behaves.
                        if (soil != null) {

                            if (pot.getCrop() == null) {

                                tooltips.add(TOOLTIP_MISSING_SEED);
                            }

                            else if (!BotanyPotHelper.canCropGrow(level, pos, pot, soil, pot.getCrop())) {

                                tooltips.add(TOOLTIP_INCORRECT_SOIL);
                            }

                            else {

                                final float growthModifier = soil.getGrowthModifier(level, pos, pot, pot.getCrop());
                                final MutableComponent multiplier = Component.literal(MULTIPLIER_FORMAT.format(growthModifier)).withStyle(growthModifier > 1 ? ChatFormatting.GREEN : growthModifier < 1 ? ChatFormatting.RED : ChatFormatting.GRAY);
                                tooltips.add(Component.translatable("tooltip.botanypots.soil_modifier", multiplier).withStyle(ChatFormatting.GRAY));
                            }
                        }
                    }
                }
            }

            // Add seed slot tooltips.
            else if (this.hoveredSlot instanceof SlotCropSeed seedSlot) {

                if (!pot.isValidSeed(stack)) {

                    tooltips.add(TOOLTIP_INVALID_CROP);
                }

                final RecipeHolder<Crop> cropRecipe = BotanyPotHelper.findCrop(level, pos, pot, stack);

                if (cropRecipe != null) {

                    final Crop crop = cropRecipe.value();

                    if (crop != null) {

                        if (pot.getSoil() == null) {

                            tooltips.add(TOOLTIP_MISSING_SOIL);
                        }

                        else if (!BotanyPotHelper.canCropGrow(level, pos, pot, pot.getSoil(), crop)) {

                            tooltips.add(TOOLTIP_INCORRECT_SEED);
                        }
                    }
                }
            }

            else {

                final RecipeHolder<Soil> hoverSoil = BotanyPotHelper.findSoil(level, pos, pot, stack);

                if (hoverSoil != null) {

                    tooltips.add(TOOLTIP_SOIL_ITEM);

                    if (isAdvanced) {

                        tooltips.add(Component.translatable("tooltip.botanypots.soil_id", hoverSoil.id().toString()).withStyle(ChatFormatting.GRAY));
                    }
                }

                final RecipeHolder<Crop> hoveredCrop = BotanyPotHelper.findCrop(level, pos, pot, stack);

                if (hoveredCrop != null) {

                    tooltips.add(TOOLTIP_SEED_ITEM);

                    if (isAdvanced) {

                        tooltips.add(Component.translatable("tooltip.botanypots.crop_id", hoveredCrop.id().toString()).withStyle(ChatFormatting.GRAY));
                    }
                }
            }
        }

        return tooltips;
    }
}
