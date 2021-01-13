package net.darkhax.botanypots;

import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.bookshelf.util.RecipeUtils;
import net.darkhax.bookshelf.util.SidedExecutor;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.crop.HarvestEntry;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class BotanyPotHelper {
    
    public static RecipeManager getManager () {
        
        return getManager(null);
    }
    
    public static RecipeManager getManager (@Nullable RecipeManager manager) {
        
        return manager != null ? manager : SidedExecutor.callForSide( () -> () -> Minecraft.getInstance().player.connection.getRecipeManager(), () -> () -> ServerLifecycleHooks.getCurrentServer().getRecipeManager());
    }
    
    public static Map<ResourceLocation, SoilInfo> getSoilData (RecipeManager manager) {
        
        if (manager != null) {
            
            return RecipeUtils.getRecipes(BotanyPots.instance.getContent().recipeTypeSoil, manager);
        }
        
        return Collections.emptyMap();
    }
    
    public static Map<ResourceLocation, CropInfo> getCropData (RecipeManager manager) {
        
        if (manager != null) {
            
            return RecipeUtils.getRecipes(BotanyPots.instance.getContent().recipeTypeCrop, manager);
        }
        
        return Collections.emptyMap();
    }

    public static Map<ResourceLocation, FertilizerInfo> getFertilizerData (RecipeManager manager) {
        
        if (manager != null) {
            
            return RecipeUtils.getRecipes(BotanyPots.instance.getContent().recipeTypeFertilizer, manager);
        }
        
        return Collections.emptyMap();
    }
    
    @Nullable
    public static SoilInfo getSoil (ResourceLocation id) {
        
        final RecipeManager manager = getManager();
        
        if (manager != null && id != null) {
            
            return RecipeUtils.getRecipes(BotanyPots.instance.getContent().recipeTypeSoil, manager).get(id);
        }
        
        return null;
    }
    
    @Nullable
    public static CropInfo getCrop (ResourceLocation id) {
        
        final RecipeManager manager = getManager();
        
        if (manager != null && id != null) {
            
            return RecipeUtils.getRecipes(BotanyPots.instance.getContent().recipeTypeCrop, manager).get(id);
        }
        
        return null;
    }
    
    @Nullable
    public static FertilizerInfo getFertilizer (ResourceLocation id) {
        
        final RecipeManager manager = getManager();
        
        if (manager != null && id != null) {
            
            return RecipeUtils.getRecipes(BotanyPots.instance.getContent().recipeTypeFertilizer, manager).get(id);
        }
        
        return null;
    }
    
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
    
    @Nullable
    public static SoilInfo getSoilForItem (ItemStack item) {
        
        for (final SoilInfo soilInfo : RecipeUtils.getRecipeList(BotanyPots.instance.getContent().recipeTypeSoil, getManager())) {
            
            if (soilInfo.getIngredient().test(item)) {
                
                return soilInfo;
            }
        }
        
        return null;
    }
    
    @Nullable
    public static CropInfo getCropForItem (ItemStack item) {
        
        for (final CropInfo cropInfo : RecipeUtils.getRecipeList(BotanyPots.instance.getContent().recipeTypeCrop, getManager())) {
            
            if (cropInfo.getSeed().test(item)) {
                
                return cropInfo;
            }
        }
        
        return null;
    }
    
    @Nullable
    public static FertilizerInfo getFertilizerForItem (ItemStack item) {
        
        for (final FertilizerInfo fertilizer : RecipeUtils.getRecipeList(BotanyPots.instance.getContent().recipeTypeFertilizer, getManager())) {
            
            if (fertilizer.getIngredient().test(item)) {
                
                return fertilizer;
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
}