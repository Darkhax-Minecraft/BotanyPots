package net.darkhax.botanypots.api.crop;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.util.JSONUtils;

public class CropEntry {
    
    private final float chance;
    private final ItemStack item;
    private final int minRolls;
    private final int maxRolls;
    
    public CropEntry(float chance, ItemStack item, int minRolls, int maxRolls) {
        
        this.chance = chance;
        this.item = item;
        this.minRolls = minRolls;
        this.maxRolls = maxRolls;
        
        if (minRolls < 0 || maxRolls < 0) {
            
            throw new IllegalArgumentException("Rolls must not be negative!");
        }
        
        if (minRolls > maxRolls) {
            
            throw new IllegalArgumentException("Min rolls must not be greater than max rolls!");
        }
    }
    
    public float getChance () {
        
        return this.chance;
    }
    
    public ItemStack getItem () {
        
        return this.item;
    }
    
    public int getMinRolls () {
        
        return this.minRolls;
    }
    
    public int getMaxRolls () {
        
        return this.maxRolls;
    }
    
    public static CropEntry deserialize (JsonObject json) {
        
        final float chance = JSONUtils.getFloat(json, "chance");
        final ItemStack item = ShapedRecipe.deserializeItem(json.getAsJsonObject("output"));
        final int minRolls = JSONUtils.getInt(json, "minRolls");
        final int maxRolls = JSONUtils.getInt(json, "maxRolls");
        
        return new CropEntry(chance, item, minRolls, maxRolls);
    }
}