package net.darkhax.botanypots.addons.crafttweaker.crop;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;

import net.darkhax.botanypots.crop.CropReloadListener;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.Crop")
public class Crop {
    
    @ZenCodeType.Method
    public static void create (String id, IIngredient seed, MCBlockState display, int ticks, float multiplier, String[] categories) {
        
        CraftTweakerAPI.apply(new ActionCropCreate(id, seed, display, ticks, multiplier, categories));
    }
    
    @ZenCodeType.Method
    public static void remove (String id) {
        
        CraftTweakerAPI.apply(new ActionCropRemove(id));
    }
    
    @ZenCodeType.Method
    public static void setSeed (String id, IIngredient seed) {
        
        CraftTweakerAPI.apply(new ActionCropSeed(id, seed));
    }
    
    @ZenCodeType.Method
    public static void setDisplay (String id, MCBlockState display) {
        
        CraftTweakerAPI.apply(new ActionCropDisplay(id, display));
    }
    
    @ZenCodeType.Method
    public static void setTickRate (String id, int tickRate) {
        
        CraftTweakerAPI.apply(new ActionCropTickRate(id, tickRate));
    }
    
    @ZenCodeType.Method
    public static void setGrowthModifier (String id, float modifier) {
        
        CraftTweakerAPI.apply(new ActionCropGrowthModifier(id, modifier));
    }
    
    @ZenCodeType.Method
    public static void addCategory (String id, String[] toAdd) {
        
        CraftTweakerAPI.apply(new ActionCropAddCategory(id, toAdd));
    }
    
    @ZenCodeType.Method
    public static void removeCategory (String id, String[] toRemove) {
        
        CraftTweakerAPI.apply(new ActionCropRemoveCategory(id, toRemove));
    }
    
    @ZenCodeType.Method
    public static void clearCategories (String id) {
        
        CraftTweakerAPI.apply(new ActionCropClearCategories(id));
    }
    
    @ZenCodeType.Method
    public static void addDrop (String id, IItemStack item, float chance, int min, int max) {
        
        CraftTweakerAPI.apply(new ActionCropAddDrop(id, item, chance, min, max));
    }
    
    @ZenCodeType.Method
    public static void removeDrop (String id, IIngredient toRemove) {
        
        CraftTweakerAPI.apply(new ActionCropRemoveDrop(id, toRemove));
    }
    
    @ZenCodeType.Method
    public static String[] getAllIds () {
        
        return CropReloadListener.registeredCrops.keySet().stream().map(ResourceLocation::toString).toArray(String[]::new);
    }
    
    @ZenCodeType.Method
    public static void removeAll () {
        
        CropReloadListener.registeredCrops.clear();
    }
}