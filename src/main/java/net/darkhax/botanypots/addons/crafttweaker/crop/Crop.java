package net.darkhax.botanypots.addons.crafttweaker.crop;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;

import net.darkhax.botanypots.BotanyPotHelper;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.Crop")
public class Crop {
    
    @ZenCodeType.Method
    public static void create (String id, IIngredient seed, MCBlockState display, int ticks, String[] categories) {
        
        CraftTweakerAPI.apply(new ActionCropCreate(id, seed, display, ticks, categories));
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
        
        return BotanyPotHelper.getCropData(CTCraftingTableManager.recipeManager).keySet().stream().map(ResourceLocation::toString).toArray(String[]::new);
    }
    
    @ZenCodeType.Method
    public static void removeAll () {
        
        BotanyPotHelper.getCropData(CTCraftingTableManager.recipeManager).clear();
    }
}