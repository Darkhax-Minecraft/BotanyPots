package net.darkhax.botanypots.addons.crt.crops;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.Soils")
public class Soils implements IRecipeManager {
    
    public Soils() {
        
        // This is needed for CraftTweaker
    }
    
    @ZenCodeType.Method
    public ZenSoil create (String id, IIngredient ingredient, MCBlockState renderState, float growthModifier, String categories) {
        
        return this.create(id, ingredient, renderState, growthModifier, new String[] { categories });
    }
    
    @ZenCodeType.Method
    public ZenSoil create (String id, IIngredient ingredient, MCBlockState renderState, float growthModifier, String[] categories) {
        
        final ZenSoil soil = new ZenSoil(id, ingredient, renderState, growthModifier, categories);
        CraftTweakerAPI.apply(new ActionAddRecipe(this, soil.getInternal(), ""));
        return soil;
    }
    
    @ZenCodeType.Method
    public ZenSoil get (String id) {
        
        final IRecipe<?> info = this.getRecipes().get(ResourceLocation.tryCreate(id));
        
        if (info instanceof SoilInfo) {
            return new ZenSoil((SoilInfo) info);
        }
        
        throw new IllegalStateException("Invalid soil ID: " + id);
    }
    
    @Override
    public ResourceLocation getBracketResourceLocation () {
        
        return BotanyPots.instance.getContent().recipeSerializerSoil.getRegistryName();
    }
    
    @Override
    public IRecipeType<?> getRecipeType () {
        
        return BotanyPots.instance.getContent().recipeTypeSoil;
    }
}