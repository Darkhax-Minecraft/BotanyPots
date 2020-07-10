package net.darkhax.botanypots.addons.crafttweaker.soil;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;

import net.darkhax.botanypots.BotanyPotHelper;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.Soil")
public class Soil {
    
    @ZenCodeType.Method
    public static void create (String id, IIngredient ingredient, MCBlockState displayState, float growthModifier, String[] categories) {
        
        CraftTweakerAPI.apply(new ActionSoilCreate(id, ingredient, displayState, growthModifier, categories));
    }
    
    @ZenCodeType.Method
    public static void remove (String id) {
        
        CraftTweakerAPI.apply(new ActionSoilRemove(id));
    }
    
    @ZenCodeType.Method
    public static void setGrowthModifier (String id, float modifier) {
        
        CraftTweakerAPI.apply(new ActionSoilSetGrowthModifier(id, modifier));
    }
    
    @ZenCodeType.Method
    public static void setIngredient (String id, IIngredient ingredient) {
        
        CraftTweakerAPI.apply(new ActionSoilSetIngredient(id, ingredient));
    }
    
    @ZenCodeType.Method
    public static void setDisplayState (String id, MCBlockState state) {
        
        CraftTweakerAPI.apply(new ActionSoilSetDisplay(id, state));
    }
    
    @ZenCodeType.Method
    public static void addCategory (String id, String[] categories) {
        
        CraftTweakerAPI.apply(new ActionSoilAddCategory(id, categories));
    }
    
    @ZenCodeType.Method
    public static void removeCategory (String id, String[] categories) {
        
        CraftTweakerAPI.apply(new ActionSoilRemoveCategory(id, categories));
    }
    
    @ZenCodeType.Method
    public static void clearCategories (String id) {
        
        CraftTweakerAPI.apply(new ActionSoilClearCategories(id));
    }
    
    @ZenCodeType.Method
    public static String[] getAllIds () {
        
        return BotanyPotHelper.getSoilData(CTCraftingTableManager.recipeManager).keySet().stream().map(ResourceLocation::toString).toArray(String[]::new);
    }
    
    @ZenCodeType.Method
    public static void removeAll () {
        
        BotanyPotHelper.getSoilData(CTCraftingTableManager.recipeManager).clear();
    }
}