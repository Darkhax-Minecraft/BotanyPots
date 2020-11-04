package net.darkhax.botanypots.addons.crt.crops;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.CropInfo;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.Crops")
public class Crops implements IRecipeManager {
    
    public Crops() {
        
        // This is needed for CraftTweaker
    }
    
    @ZenCodeType.Method
    public ZenCrop create (String id, IIngredient seed, MCBlockState display, int ticks, String categories) {
        
        return this.create(id, seed, new MCBlockState[] { display }, ticks, new String[] { categories });
    }
    
    @ZenCodeType.Method
    public ZenCrop create (String id, IIngredient seed, MCBlockState[] display, int ticks, String[] categories) {
        
        final ZenCrop crop = new ZenCrop(id, seed, display, ticks, categories);
        CraftTweakerAPI.apply(new ActionAddRecipe(this, crop.getInternal(), ""));
        return crop;
    }
    
    @ZenCodeType.Method
    public ZenCrop getCrop (String id) {
        
        final IRecipe<?> info = this.getRecipes().get(ResourceLocation.tryCreate(id));
        
        if (info instanceof CropInfo) {
            return new ZenCrop((CropInfo) info);
        }
        
        throw new IllegalStateException("Invalid crop ID: " + id);
    }
    
    @Override
    public ResourceLocation getBracketResourceLocation () {
        
        return BotanyPots.instance.getContent().recipeSerializerCrop.getRegistryName();
    }
    
    @Override
    public IRecipeType<?> getRecipeType () {
        
        return BotanyPots.instance.getContent().recipeTypeCrop;
    }
}