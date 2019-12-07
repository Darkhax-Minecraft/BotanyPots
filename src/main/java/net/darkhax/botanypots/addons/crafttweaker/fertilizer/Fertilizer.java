package net.darkhax.botanypots.addons.crafttweaker.fertilizer;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;

import net.darkhax.botanypots.fertilizer.FertilizerReloadListener;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.Fertilizer")
public class Fertilizer {
    
    @ZenCodeType.Method
    public static void create (String id, IIngredient ingredient, int min, int max) {
        
        CraftTweakerAPI.apply(new ActionFertilizerCreate(id, ingredient, min, max));
    }
    
    @ZenCodeType.Method
    public static void remove (String id) {
        
        CraftTweakerAPI.apply(new ActionFertilizerRemove(id));
    }
    
    @ZenCodeType.Method
    public static void setTicks (String id, int min, int max) {
        
        CraftTweakerAPI.apply(new ActionFertilizerSetTicks(id, min, max));
    }
    
    @ZenCodeType.Method
    public static void setIngredient (String id, IIngredient ingredient) {
        
        CraftTweakerAPI.apply(new ActionFertilizerSetIngredient(id, ingredient));
    }
    
    @ZenCodeType.Method
    public static String[] getAllIds () {
        
        return FertilizerReloadListener.registeredFertilizer.keySet().stream().map(ResourceLocation::toString).toArray(String[]::new);
    }
    
    @ZenCodeType.Method
    public static void removeAll () {
        
        FertilizerReloadListener.registeredFertilizer.clear();
    }
}