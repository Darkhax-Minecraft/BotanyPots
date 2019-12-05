package net.darkhax.botanypots.api.fertilizer;

import java.util.Random;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class FertilizerInfo {
    
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final int minTicks;
    private final int maxTicks;
    
    public FertilizerInfo(ResourceLocation id, Ingredient ingredient, int minTicks, int maxTicks) {
        
        this.id = id;
        this.ingredient = ingredient;
        this.minTicks = minTicks;
        this.maxTicks = maxTicks;
    }
    
    public ResourceLocation getId () {
        
        return this.id;
    }
    
    public Ingredient getIngredient () {
        
        return this.ingredient;
    }
    
    public int getMinTicks () {
        
        return this.minTicks;
    }
    
    public int getMaxTicks () {
        
        return this.maxTicks;
    }
    
    public int getTicksToGrow(Random random) {
        
        return MathHelper.nextInt(random, this.minTicks, this.maxTicks);
    }
    
    public static FertilizerInfo deserialize (ResourceLocation id, JsonObject json) {
        
        final Ingredient ingredient = Ingredient.deserialize(json.getAsJsonObject("fertilizer"));
        final int minTicks = JSONUtils.getInt(json, "minTicks");
        final int maxTicks = JSONUtils.getInt(json, "maxTicks");
        
        return new FertilizerInfo(id, ingredient, minTicks, maxTicks);
    }
}