package net.darkhax.botanypots.api.fertilizer;

import java.util.Random;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class FertilizerInfo {
    
    /**
     * The id for the fertilizer entry.
     */
    private final ResourceLocation id;
    
    /**
     * The items in the fertilizer category.
     */
    private final Ingredient ingredient;
    
    /**
     * The minimum amount of growth ticks to add.
     */
    private final int minTicks;
    
    /**
     * The maximum amount of growth ticks to add.
     */
    private final int maxTicks;
    
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
    
    /**
     * Reads a FertilizerInfo object from a JsonObject.
     * 
     * @param id The id to assign the new fertilizer info.
     * @param json The json object to read from.
     * @return A new FertilizerInfo that was deserialize. May also throw an exception if JSON
     *         syntax is not valid.
     */
    public static FertilizerInfo deserialize (ResourceLocation id, JsonObject json) {
        
        final Ingredient ingredient = Ingredient.deserialize(json.getAsJsonObject("fertilizer"));
        final int minTicks = JSONUtils.getInt(json, "minTicks");
        final int maxTicks = JSONUtils.getInt(json, "maxTicks");
        
        return new FertilizerInfo(id, ingredient, minTicks, maxTicks);
    }
}