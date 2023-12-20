package net.darkhax.botanypots.addons.rei.ui;

import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.addons.rei.REIPlugin;
import net.darkhax.botanypots.data.recipes.crop.BasicCrop;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.darkhax.botanypots.data.recipes.soil.BasicSoil;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BasicCropDisplay extends BasicDisplay implements CropDisplay {

    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    private final List<HarvestEntry> drops;
    private final int growthTime;
    private final float modifier;

    public static List<CropDisplay> getCropRecipes(RecipeHolder<Crop> cropRecipe, List<RecipeHolder<Soil>> soilRecipes) {

        final List<CropDisplay> info = new ArrayList<>();

        if (cropRecipe.value() instanceof BasicCrop crop) {

            for (RecipeHolder<Soil> soilRecipe : soilRecipes) {

                if (soilRecipe.value() instanceof BasicSoil soil && crop.canGrowInSoil(null, null, null, soil)) {

                    final int ticks = BotanyPotHelper.getRequiredGrowthTicks(null, null, null, crop, soil);
                    info.add(new BasicCropDisplay(cropRecipe.id(), crop.getSeed(), soil.getIngredient(), crop.getResults(), ticks, soil.getGrowthModifier()));
                }
            }
        }

        return info;
    }

    public BasicCropDisplay(ResourceLocation id, Ingredient seed, Ingredient soil, List<HarvestEntry> drops, int growthTime, float modifier) {

        this(id, List.of(seed), List.of(soil), drops, growthTime, modifier);
    }

    public BasicCropDisplay(ResourceLocation id, List<Ingredient> seeds, List<Ingredient> soils, List<HarvestEntry> drops, int growthTime, float modifier) {
        super(List.of(reiIngredientFromIngredients(seeds), reiIngredientFromIngredients(soils)),
                reiIngredientsFromDrops(drops), Optional.ofNullable(id));
        this.drops = drops;
        this.growthTime = growthTime;
        this.modifier = modifier;
    }

    private static EntryIngredient reiIngredientFromIngredients(List<Ingredient> seeds) {
        return EntryIngredients.ofItemStacks(CollectionUtils.flatMap(seeds, ingredient -> Arrays.asList(ingredient.getItems())));
    }

    private static List<EntryIngredient> reiIngredientsFromDrops(List<HarvestEntry> drops) {
        return CollectionUtils.map(drops, entry -> EntryIngredients.of(entry.getItem()));
    }

    public float getModifier() {
        return modifier;
    }

    public int getGrowthTime() {
        return growthTime;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REIPlugin.CROP;
    }

    @Override
    public List<Widget> setupDisplay(Rectangle bounds) {

        List<EntryIngredient> inputEntries = this.getInputEntries();
        List<EntryIngredient> outputEntries = this.getOutputEntries();
        List<Widget> widgets = new ArrayList<>();
        
        widgets.add(Widgets.createRecipeBase(bounds));

        // Seeds
        widgets.add(Widgets.createSlot(new Point(bounds.x + 34, bounds.y + 16))
                .markInput()
                .entries(inputEntries.get(0).map(stack -> stack.tooltip(getSeedTooltip())))
        );

        // Soils
        widgets.add(Widgets.createSlot(new Point(bounds.x + 34, bounds.y + 34))
                .markInput()
                .entries(inputEntries.get(1).map(stack -> stack.tooltip(getSoilTooltip())))
        );

        // Drops

        int dropCount = 0;

        for (int i = 0; i < 12; i++) {
            Slot slot = Widgets.createSlot(new Point(bounds.x + 73 + 18 * (dropCount % 4), bounds.y + 7 + (18 * (dropCount / 4))))
                    .markOutput();
            widgets.add(slot);

            if (i < outputEntries.size()) {
                Component[] dropTooltip = getDropTooltip(this.drops.get(dropCount));
                slot.entries(outputEntries.get(i).map(stack -> stack.tooltip(dropTooltip)));
            }

            dropCount++;
        }

        return widgets;
    }

    private Component[] getSeedTooltip() {

        return new Component[]{
                Component.translatable("tooltip.botanypots.grow_time", ticksToTime(this.growthTime)).withStyle(ChatFormatting.GRAY)
        };
    }

    private Component[] getSoilTooltip() {

        return new Component[]{
                Component.translatable("tooltip.botanypots.modifier", FORMAT.format(this.modifier)).withStyle(ChatFormatting.GRAY)
        };
    }

    private Component[] getDropTooltip(HarvestEntry drops) {

        final int rollMin = drops.getMinRolls();
        final int rollMax = drops.getMaxRolls();

        Component rolls;

        if (rollMin == rollMax) {

            rolls = Component.translatable("tooltip.botanypots.rolls", rollMin).withStyle(ChatFormatting.GRAY);
        } else {

            rolls = Component.translatable("tooltip.botanypots.rollrange", rollMin, rollMax).withStyle(ChatFormatting.GRAY);
        }

        return new Component[]{
                Component.translatable("tooltip.botanypots.chance", FORMAT.format(drops.getChance() * 100f)).withStyle(ChatFormatting.GRAY),
                rolls
        };
    }

    private static String ticksToTime(int ticks) {

        ticks = Math.abs(ticks);
        int i = ticks / 20;
        final int j = i / 60;
        i = i % 60;

        return i < 10 ? j + ":0" + i : j + ":" + i;
    }
}
