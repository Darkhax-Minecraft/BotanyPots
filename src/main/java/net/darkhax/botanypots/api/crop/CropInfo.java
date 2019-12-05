package net.darkhax.botanypots.api.crop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.darkhax.bookshelf.Bookshelf;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.JsonUtils;
import net.darkhax.botanypots.api.soil.SoilInfo;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class CropInfo {
    
    private final ResourceLocation id;
    private final Ingredient seed;
    private final String[] soilCategories;
    private final int growthTicks;
    private final float growthMultiplier;
    private final CropEntry[] results;
    private final BlockState displayBlock;
    
    public CropInfo(ResourceLocation id, Ingredient seed, String[] soilCategories, int growthTicks, float growthMultiplier, CropEntry[] results, BlockState displayState) {
        
        this.id = id;
        this.seed = seed;
        this.soilCategories = soilCategories;
        this.growthTicks = growthTicks;
        this.growthMultiplier = growthMultiplier;
        this.results = results;
        this.displayBlock = displayState;
    }
    
    public ResourceLocation getId () {
        
        return this.id;
    }
    
    public Ingredient getSeed () {
        
        return this.seed;
    }
    
    public String[] getSoilCategories () {
        
        return this.soilCategories;
    }
    
    public CropEntry[] getResults () {
        
        return this.results;
    }
    
    public BlockState getDisplayState () {
        
        return this.displayBlock;
    }
    
    public static CropInfo deserialize (ResourceLocation id, JsonObject json) {
        
        final Ingredient seed = Ingredient.deserialize(json.getAsJsonObject("seed"));
        final Set<String> validSoils = deserializeSoilInfo(id, json);
        final int growthTicks = JSONUtils.getInt(json, "growthTicks");
        final float growthModifier = JSONUtils.getFloat(json, "growthModifier");
        final List<CropEntry> results = deserializeCropEntries(id, json);
        final BlockState displayState = JsonUtils.deserializeBlockState(json.getAsJsonObject("display"));
        return new CropInfo(id, seed, validSoils.toArray(new String[0]), growthTicks, growthModifier, results.toArray(new CropEntry[0]), displayState);
    }
    
    private static Set<String> deserializeSoilInfo (ResourceLocation ownerId, JsonObject json) {
        
        final Set<String> categories = new HashSet<>();
        
        for (final JsonElement element : json.getAsJsonArray("categories")) {
            
            categories.add(element.getAsString().toLowerCase());
        }
        
        return categories;
    }
    
    private static List<CropEntry> deserializeCropEntries (ResourceLocation ownerId, JsonObject json) {
        
        final List<CropEntry> crops = new ArrayList<>();
        
        for (final JsonElement entry : json.getAsJsonArray("results")) {
            
            if (!entry.isJsonObject()) {
                
                BotanyPots.LOGGER.error("Crop entry in {} is not a JsonObject.", ownerId);
            }
            
            else {
                
                final CropEntry cropEntry = CropEntry.deserialize(entry.getAsJsonObject());
                crops.add(cropEntry);
            }
        }
        
        return crops;
    }
    
    public int getGrowthTicks () {
        
        return this.growthTicks;
    }
    
    public float getGrowthMultiplier () {
        
        return this.growthMultiplier;
    }
    
    public int getGrowthTicksForSoil (SoilInfo soil) {
        
        return MathHelper.floor(soil.getTickRate() * this.getGrowthMultiplier() * this.growthTicks);
    }
    
    public ItemStack getRandomSeed () {
        
        final ItemStack[] matchingStacks = this.seed.getMatchingStacks();
        return matchingStacks.length > 0 ? matchingStacks[Bookshelf.RANDOM.nextInt(matchingStacks.length)] : ItemStack.EMPTY;
    }
}