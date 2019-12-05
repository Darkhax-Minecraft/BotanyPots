package net.darkhax.botanypots.api;

import java.util.Random;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.botanypots.api.crop.CropEntry;
import net.darkhax.botanypots.api.crop.CropInfo;
import net.darkhax.botanypots.api.crop.CropReloadListener;
import net.darkhax.botanypots.api.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.api.fertilizer.FertilizerReloadListener;
import net.darkhax.botanypots.api.soil.SoilInfo;
import net.darkhax.botanypots.api.soil.SoilReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class BotanyPotHelper {
    
    public static int getRequiredGrowthTicks (CropInfo crop, SoilInfo soil) {
        
        return crop.getGrowthTicksForSoil(soil);
    }
    
    @Nullable
    public static SoilInfo getSoilForItem (ItemStack item) {
        
        for (final SoilInfo soilInfo : SoilReloadListener.registeredSoil.values()) {
            
            if (soilInfo.getIngredient().test(item)) {
                
                return soilInfo;
            }
        }
        
        return null;
    }
    
    @Nullable
    public static CropInfo getCropForItem (ItemStack item) {
        
        for (final CropInfo cropInfo : CropReloadListener.registeredCrops.values()) {
            
            if (cropInfo.getSeed().test(item)) {
                
                return cropInfo;
            }
        }
        
        return null;
    }
    
    public static boolean isSoilValidForCrop (SoilInfo soil, CropInfo crop) {
        
        for (final String soilCategory : soil.getCategories()) {
            
            for (final String cropCategory : crop.getSoilCategories()) {
                
                if (soilCategory.equalsIgnoreCase(cropCategory)) {
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static NonNullList<ItemStack> getHarvestStacks (World world, CropInfo crop) {
        
        final NonNullList<ItemStack> drops = NonNullList.create();
        
        for (final CropEntry cropEntry : crop.getResults()) {
            
            if (world.rand.nextFloat() <= cropEntry.getChance()) {
                
                final int rolls = MathsUtils.nextIntInclusive(world.rand, cropEntry.getMinRolls(), cropEntry.getMaxRolls());
                
                if (rolls > 0) {
                    
                    for (int roll = 0; roll < rolls; roll++) {
                        
                        drops.add(cropEntry.getItem().copy());
                    }
                }
            }
        }
        
        return drops;
    }
    
    public static int getFertilizerTicks(ItemStack item, Random random) {
        
        for (FertilizerInfo fertilizer : FertilizerReloadListener.registeredFertilizer.values()) {
            
            if (fertilizer.getIngredient().test(item)) {
                
                return fertilizer.getTicksToGrow(random);
            }
        }
        
        return -1;
    }
}