package net.darkhax.botanypots.fertilizer;

import java.util.Random;

import net.darkhax.bookshelf.crafting.RecipeDataBase;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class FertilizerInfo extends RecipeDataBase {
    
    /**
     * The items in the fertilizer category.
     */
    private Ingredient ingredient;
    
    /**
     * The minimum amount of growth ticks to add.
     */
    private int minTicks;
    
    /**
     * The maximum amount of growth ticks to add.
     */
    private int maxTicks;
    
    public FertilizerInfo(ResourceLocation id, Ingredient ingredient, int minTicks, int maxTicks) {
        
        super(id);
        this.ingredient = ingredient;
        this.minTicks = minTicks;
        this.maxTicks = maxTicks;
    }
    
    /**
     * Gets the ingredient used for this fertilizer. This is used to match ItemStacks to the
     * fertilizer.
     * 
     * @return An ingredient that can be used to match ItemStacks to the fertilizer.
     */
    public Ingredient getIngredient () {
        
        return this.ingredient;
    }
    
    /**
     * Get the minimum amount of ticks that the fertilizer can add.
     * 
     * @return The minimum amount of ticks that the fertilizer can add.
     */
    public int getMinTicks () {
        
        return this.minTicks;
    }
    
    /**
     * Gets the maximum amount of ticks that the fertilizer can add.
     * 
     * @return The maximum amount of ticks that the fertilizer can add.
     */
    public int getMaxTicks () {
        
        return this.maxTicks;
    }
    
    /**
     * Get the amount of ticks to grow a crop by. The result is not deterministic.
     * 
     * @param random An instance of random used to generate the amount of ticks.
     * @return The amount of ticks to grow the crop by.
     */
    public int getTicksToGrow (Random random, SoilInfo soil, CropInfo crop) {
        
        return MathHelper.nextInt(random, this.minTicks, this.maxTicks);
    }
    
    public void setIngredient (Ingredient ingredient) {
        
        this.ingredient = ingredient;
    }
    
    public void setMinTicks (int minTicks) {
        
        this.minTicks = minTicks;
    }
    
    public void setMaxTicks (int maxTicks) {
        
        this.maxTicks = maxTicks;
    }
    
    @Override
    public boolean isDynamic () {
        
        return true;
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer () {
        
        return BotanyPots.instance.getContent().recipeSerializerFertilizer;
    }
    
    @Override
    public IRecipeType<?> getType () {
        
        return BotanyPots.instance.getContent().recipeTypeFertilizer;
    }
}