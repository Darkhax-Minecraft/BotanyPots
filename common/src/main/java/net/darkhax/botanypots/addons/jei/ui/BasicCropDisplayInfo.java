package net.darkhax.botanypots.addons.jei.ui;

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
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.darkhax.botanypots.data.recipes.soil.BasicSoil;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BasicCropDisplayInfo extends CropDisplayInfo {

    public static List<CropDisplayInfo> getCropRecipes(RecipeHolder<Crop> cropRecipe, List<RecipeHolder<Soil>> soils) {

        final List<CropDisplayInfo> info = new ArrayList<>();

        if (cropRecipe.value() instanceof BasicCrop crop) {

            for (RecipeHolder<Soil> soilRecipe : soils) {

                if (soilRecipe.value() instanceof BasicSoil soil && crop.canGrowInSoil(null, null, null, soil)) {

                    final int ticks = BotanyPotHelper.getRequiredGrowthTicks(null, null, null, crop, soil);
                    info.add(new BasicCropDisplayInfo(cropRecipe.id(), crop.getSeed(), soil.getIngredient(), crop.getResults(), ticks, soil.getGrowthModifier()));
                }
            }
        }

        return info;
    }

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");

    private final ResourceLocation id;
    private final List<Ingredient> seeds;
    private final List<Ingredient> soils;
    private final List<HarvestEntry> drops;
    private final int growthTime;
    private final float modifier;

    public BasicCropDisplayInfo(ResourceLocation id, Ingredient seeds, Ingredient soils, List<HarvestEntry> drops, int growthTime, float modifier) {

        this(id, List.of(seeds), List.of(soils), drops, growthTime, modifier);
    }

    public BasicCropDisplayInfo(ResourceLocation id, List<Ingredient> seeds, List<Ingredient> soils, List<HarvestEntry> drops, int growthTime, float modifier) {

        this.id = id;
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
    public void draw(IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY, IGuiHelper guiHelper) {

        final IDrawableStatic slot = guiHelper.getSlotDrawable();

        slot.draw(graphics, 30, 10); // Seed
        slot.draw(graphics, 30, 28); // Soil

        // Drops
        for (int nextSlotId = 0; nextSlotId < 12; nextSlotId++) {

            slot.draw(graphics, 80 + 18 * (nextSlotId % 4), 18 * (nextSlotId / 4));
        }
    }

    @Override
    public ResourceLocation getCropId() {

        return this.id;
    }

    private static String ticksToTime(int ticks) {

        ticks = Math.abs(ticks);
        int i = ticks / 20;
        final int j = i / 60;
        i = i % 60;

        return i < 10 ? j + ":0" + i : j + ":" + i;
    }
}