package net.darkhax.botanypots;

import java.util.Random;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.crop.CropReloadListener;
import net.darkhax.botanypots.crop.HarvestEntry;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.fertilizer.FertilizerReloadListener;
import net.darkhax.botanypots.soil.SoilInfo;
import net.darkhax.botanypots.soil.SoilReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class BotanyPotHelper {
    
    public static final ResourceLocation NONE = new ResourceLocation(BotanyPots.MOD_ID, "none");
    
    /**
     * Gets the total amount of world ticks required for a specific crop to reach maturity when
     * planted in a given soil.
     * 
     * @param crop The crop to calculate.
     * @param soil The soil to calculate.
     * @return The total amount of world ticks for the crop to reach maturity when planted in
     *         the given soil.
     */
    public static int getRequiredGrowthTicks (@Nullable CropInfo crop, @Nullable SoilInfo soil) {
        
        return crop == null || soil == null ? -1 : crop.getGrowthTicksForSoil(soil);
    }
    
    /**
     * Gets the first soil info associated with a given item stack.
     * 
     * @param item The item stack to look up.
     * @return The soil info associated with the item stack. If this is null it means the item
     *         is not a valid soil.
     */
    @Nullable
    public static SoilInfo getSoilForItem (ItemStack item) {
        
        for (final SoilInfo soilInfo : SoilReloadListener.registeredSoil.values()) {
            
            if (soilInfo.getIngredient().test(item)) {
                
                return soilInfo;
            }
        }
        
        return null;
    }
    
    /**
     * Gets the first crop for a given potential seed item stack.
     * 
     * @param item The potential seed item stack.
     * @return The crop info associated with the given item stack. If this is null it means the
     *         item is not a valid seed.
     */
    @Nullable
    public static CropInfo getCropForItem (ItemStack item) {
        
        for (final CropInfo cropInfo : CropReloadListener.registeredCrops.values()) {
            
            if (cropInfo.getSeed().test(item)) {
                
                return cropInfo;
            }
        }
        
        return null;
    }
    
    /**
     * Checks if a crop can be planted in a given soil.
     * 
     * @param soil The soil to plant in.
     * @param crop The crop to plant.
     * @return Whether or not the crop can be planted in the given soil.
     */
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
    
    /**
     * Calculates and gathers the harvested drops of a given crop. This is used to get the
     * actual items to yield when a crop is harvested.
     * 
     * @param world The world instance.
     * @param crop The crop being harvested.
     * @return A list containing all the yielded ItemStack from harvesting. These are all fresh
     *         copies of the original harvest stacks.
     */
    public static NonNullList<ItemStack> getHarvestStacks (World world, CropInfo crop) {
        
        final NonNullList<ItemStack> drops = NonNullList.create();
        
        for (final HarvestEntry cropEntry : crop.getResults()) {
            
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
    
    /**
     * Gets the amount of ticks to progress a crop for a given fertilizer.
     * 
     * @param item The item to get the growth ticks for.
     * @param random An instance of random used to get ticks between the min/max of the
     *        potential fertilizer.
     * @return The amount of ticks to progress a crop. If this is -1 the item is not a
     *         fertilizer.
     */
    public static int getFertilizerTicks (ItemStack item, Random random) {
        
        for (final FertilizerInfo fertilizer : FertilizerReloadListener.registeredFertilizer.values()) {
            
            if (fertilizer.getIngredient().test(item)) {
                
                return fertilizer.getTicksToGrow(random);
            }
        }
        
        return -1;
    }
    
    public static ITextComponent getSoilName(@Nullable SoilInfo soil) {
        
        final ResourceLocation id = soil != null ? soil.getId() : NONE;
        return new TranslationTextComponent("botanypots.soil." + id.getNamespace() + "." + id.getPath());
    }
    
    public static ITextComponent getCropName(@Nullable CropInfo crop) {
        
        final ResourceLocation id = crop != null ? crop.getId() : NONE;
        return new TranslationTextComponent("botanypots.crop." + id.getNamespace() + "." + id.getPath());
    }
}