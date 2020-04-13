package net.darkhax.botanypots.addons.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.darkhax.bookshelf.util.RecipeUtils;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.crop.HarvestEntry;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;

public class CropWrapper implements IRecipeCategoryExtension {
    
    private final CropInfo cropInfo;
    private final List<ItemStack> seedItems = NonNullList.create();
    private final List<ItemStack> soilItems = NonNullList.create();
    private final List<HarvestEntry> drops = NonNullList.create();
    
    public CropWrapper(CropInfo crop) {
        
        this.cropInfo = crop;
        
        this.seedItems.addAll(Arrays.asList(crop.getSeed().getMatchingStacks()));
        
        for (final SoilInfo soil : BotanyPotHelper.getSoilData(RecipeUtils.getActiveRecipeManager()).values()) {
            
            if (BotanyPotHelper.isSoilValidForCrop(soil, crop)) {
                
                this.soilItems.addAll(Arrays.asList(soil.getIngredient().getMatchingStacks()));
            }
        }
        
        this.drops.addAll(crop.getResults());
    }
    
    public List<ItemStack> getSeedItems () {
        
        return this.seedItems;
    }
    
    public List<ItemStack> getSoilItems () {
        
        return this.soilItems;
    }
    
    public List<HarvestEntry> getDrops () {
        
        return this.drops;
    }
    
    @Override
    public void setIngredients (IIngredients ingredients) {
        
        final List<ItemStack> pots = BotanyPots.instance.getContent().getBotanyPotBlocks().stream().map(ItemStack::new).collect(Collectors.toList());
        
        final List<ItemStack> inputs = new ArrayList<>();
        
        inputs.addAll(this.seedItems);
        inputs.addAll(this.soilItems);
        inputs.addAll(pots);
        ingredients.setInputs(VanillaTypes.ITEM, inputs);
        
        final List<ItemStack> outputs = new ArrayList<>();
        
        for (final HarvestEntry entry : this.drops) {
            
            outputs.add(entry.getItem());
        }
        
        outputs.addAll(pots);
        
        ingredients.setOutputs(VanillaTypes.ITEM, outputs);
    }
    
    public void getTooltip (int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
        
        if (!ingredient.isEmpty()) {
            
            if (input) {
                
                if (slotIndex == 0) {
                    
                    final int growthTicks = this.cropInfo.getGrowthTicks();
                    tooltip.add(TextFormatting.GRAY + I18n.format("botanypots.tooltip.jei.growthrate", growthTicks, ticksToTime(growthTicks, false)));
                }
                
                else if (slotIndex == 1) {
                    
                    final SoilInfo soil = BotanyPotHelper.getSoilForItem(ingredient);
                    
                    if (soil != null) {
                        
                        final int difference = this.cropInfo.getGrowthTicksForSoil(soil) - this.cropInfo.getGrowthTicks();
                        final TextFormatting formatting = difference < 0 ? TextFormatting.GREEN : difference > 0 ? TextFormatting.RED : TextFormatting.GRAY;
                        tooltip.add(formatting + I18n.format("botanypots.tooltip.jei.growthmodifier", soil.getGrowthModifier(), ticksToTime(difference, true)));
                    }
                }
            }
            
            else {
                
                final int outputIndex = slotIndex - 2;
                
                if (outputIndex < this.drops.size()) {
                    
                    final HarvestEntry entry = this.drops.get(outputIndex);
                    tooltip.add(TextFormatting.GRAY + I18n.format("botanypots.tooltip.jei.dropchance", entry.getChance() * 100f));
                    
                    final int rollMin = entry.getMinRolls();
                    final int rollMax = entry.getMaxRolls();
                    
                    if (rollMin == rollMax) {
                        
                        tooltip.add(TextFormatting.GRAY + I18n.format("botanypots.tooltip.jei.rolls", rollMin));
                    }
                    
                    else {
                        
                        tooltip.add(TextFormatting.GRAY + I18n.format("botanypots.tooltip.jei.rollrange", rollMin, rollMax));
                    }
                }
            }
        }
    }
    
    private static String ticksToTime (int ticks, boolean prefix) {
        
        final boolean isPositive = ticks > 0;
        ticks = Math.abs(ticks);
        int i = ticks / 20;
        final int j = i / 60;
        i = i % 60;
        
        final String result = i < 10 ? j + ":0" + i : j + ":" + i;
        return prefix ? (isPositive ? "+" : "-") + result : result;
    }
}