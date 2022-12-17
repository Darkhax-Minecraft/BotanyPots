package net.darkhax.botanypots.addons.jei.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.data.recipes.crop.BasicCrop;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.darkhax.botanypots.data.recipes.soil.BasicSoil;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BasicCropDisplayInfo extends CropDisplayInfo {

    public static List<CropDisplayInfo> getCropRecipes(BasicCrop crop, List<Soil> soils) {

        final List<CropDisplayInfo> info = new ArrayList<>();

        for (Soil soil : soils) {

            if (soil instanceof BasicSoil basicSoil && crop.canGrowInSoil(null, null, null, soil)) {

                final int ticks = BotanyPotHelper.getRequiredGrowthTicks(null, null, null, crop, soil);
                info.add(new BasicCropDisplayInfo(crop.getSeed(), basicSoil.getIngredient(), crop.getResults(), ticks, basicSoil.getGrowthModifier()));
            }
        }

        return info;
    }

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    private final List<Ingredient> seeds;
    private final List<Ingredient> soils;
    private final List<HarvestEntry> drops;
    private final int growthTime;
    private final float modifier;

    public BasicCropDisplayInfo(Ingredient seeds, Ingredient soils, List<HarvestEntry> drops, int growthTime, float modifier) {

        this(List.of(seeds), List.of(soils), drops, growthTime, modifier);
    }

    public BasicCropDisplayInfo(List<Ingredient> seeds, List<Ingredient> soils, List<HarvestEntry> drops, int growthTime, float modifier) {

        this.seeds = seeds;
        this.soils = soils;
        this.drops = drops;
        this.growthTime = growthTime;
        this.modifier = modifier;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IFocusGroup focuses) {

        // Seeds
        final IRecipeSlotBuilder seedSlot = builder.addSlot(RecipeIngredientRole.INPUT, 31, 11);
        this.seeds.forEach(seedSlot::addIngredients);
        seedSlot.addTooltipCallback(getSeedTooltip());

        // Soils
        final IRecipeSlotBuilder soilSlot = builder.addSlot(RecipeIngredientRole.INPUT, 31, 29);
        this.soils.forEach(soilSlot::addIngredients);
        soilSlot.addTooltipCallback(getSoilTooltip());

        // Drops

        int dropCount = 0;

        for (final HarvestEntry entry : drops) {

            final IRecipeSlotBuilder dropSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 81 + 18 * (dropCount % 4), 1 + (18 * (dropCount / 4)));
            dropSlot.addItemStack(entry.getItem());
            dropSlot.addTooltipCallback(getDropTooltip(entry));

            dropCount++;
        }
    }

    private IRecipeSlotTooltipCallback getSeedTooltip() {

        return (view, tooltip) -> {

            tooltip.add(Component.translatable("tooltip.botanypots.grow_time", ticksToTime(this.growthTime)).withStyle(ChatFormatting.GRAY));
        };
    }

    private IRecipeSlotTooltipCallback getSoilTooltip() {

        return (view, tooltip) -> {

            tooltip.add(Component.translatable("tooltip.botanypots.modifier", FORMAT.format(this.modifier)).withStyle(ChatFormatting.GRAY));
        };
    }

    private IRecipeSlotTooltipCallback getDropTooltip(HarvestEntry drops) {

        return (view, tooltip) -> {

            tooltip.add(Component.translatable("tooltip.botanypots.chance", FORMAT.format(drops.getChance() * 100f)).withStyle(ChatFormatting.GRAY));

            final int rollMin = drops.getMinRolls();
            final int rollMax = drops.getMaxRolls();

            if (rollMin == rollMax) {

                tooltip.add(Component.translatable("tooltip.botanypots.rolls", rollMin).withStyle(ChatFormatting.GRAY));
            }

            else {

                tooltip.add(Component.translatable("tooltip.botanypots.rollrange", rollMin, rollMax).withStyle(ChatFormatting.GRAY));
            }
        };
    }

    @Override
    public void draw(IRecipeSlotsView view, PoseStack stack, double mouseX, double mouseY, IGuiHelper guiHelper) {

        final IDrawableStatic slot = guiHelper.getSlotDrawable();

        slot.draw(stack, 30, 10); // Seed
        slot.draw(stack, 30, 28); // Soil

        // Drops
        for (int nextSlotId = 0; nextSlotId < 12; nextSlotId++) {

            slot.draw(stack, 80 + 18 * (nextSlotId % 4), 18 * (nextSlotId / 4));
        }
    }

    private static String ticksToTime (int ticks) {

        ticks = Math.abs(ticks);
        int i = ticks / 20;
        final int j = i / 60;
        i = i % 60;

        return i < 10 ? j + ":0" + i : j + ":" + i;
    }
}