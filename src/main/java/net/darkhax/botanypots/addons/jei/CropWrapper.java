package net.darkhax.botanypots.addons.jei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.crop.HarvestEntry;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class CropWrapper implements IRecipeCategoryExtension {
    
    private final CropInfo cropInfo;
    private final List<ItemStack> seedItems = NonNullList.create();
    private final List<ItemStack> soilItems = NonNullList.create();
    private final List<HarvestEntry> drops = NonNullList.create();
    
    public CropWrapper(CropInfo crop) {
        
        this.cropInfo = crop;
        
        this.seedItems.addAll(Arrays.asList(crop.getSeed().getMatchingStacks()));
        
        for (final SoilInfo soil : BotanyPotHelper.getSoilData(BotanyPots.instance.getActiveRecipeManager()).values()) {
            
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
    
    public void cropTooltip (int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
        
    }
    
    public void soilTooltip (int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
        
    }
    
    public void output (int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) {
        
    }
}