package net.darkhax.botanypots.addons.crt.recipes;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.Fertilizers")
public class FertilizerManager implements IRecipeManager {
    
    public FertilizerManager() {
        
        // This is needed for CraftTweaker
    }
    
    @ZenCodeType.Method
    public ZenFertilizer create (String id, IIngredient input, int ticks) {
        
        return this.create(id, input, ticks, ticks);
    }
    
    @ZenCodeType.Method
    public ZenFertilizer create (String id, IIngredient input, int min, int max) {
        
        final ZenFertilizer fertilizer = new ZenFertilizer(id, input, min, max);
        CraftTweakerAPI.apply(new ActionAddRecipe(this, fertilizer.getInternal(), ""));
        return fertilizer;
    }
    
    @ZenCodeType.Method
    public ZenFertilizer getFertilizer (String id) {
        
        final IRecipe<?> info = this.getRecipes().get(ResourceLocation.tryCreate(id));
        
        if (info instanceof FertilizerInfo) {
            return new ZenFertilizer((FertilizerInfo) info);
        }
        
        throw new IllegalStateException("Invalid fertilizer ID: " + id);
    }
    
    @Override
    public ResourceLocation getBracketResourceLocation () {
        
        return BotanyPots.instance.getContent().recipeSerializerFertilizer.getRegistryName();
    }
    
    @Override
    public IRecipeType<?> getRecipeType () {
        
        return BotanyPots.instance.getContent().recipeTypeFertilizer;
    }
}