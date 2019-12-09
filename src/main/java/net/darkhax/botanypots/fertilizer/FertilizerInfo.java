package net.darkhax.botanypots.fertilizer;

import java.util.Random;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.RecipeData;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class FertilizerInfo extends RecipeData {
    
    /**
     * The id for the fertilizer entry.
     */
    private final ResourceLocation id;
    
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
        
        this.id = id;
        this.ingredient = ingredient;
        this.minTicks = minTicks;
        this.maxTicks = maxTicks;
    }
    
    /**
     * The id of the fertilizer.
     * 
     * @return The fertilizer's internal id.
     */
    public ResourceLocation getId () {
        
        return this.id;
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
    public int getTicksToGrow (Random random) {
        
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
    public IRecipeSerializer<?> getSerializer () {
        
        return BotanyPots.instance.getContent().getRecipeSerializerFertilizer();
    }

    @Override
    public IRecipeType<?> getType () {
        
        return BotanyPots.instance.getContent().getRecipeTypeFertilizer();
    }
}