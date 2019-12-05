package net.darkhax.botanypots.api.soil;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.darkhax.bookshelf.Bookshelf;
import net.darkhax.bookshelf.util.MCJsonUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class SoilInfo {
    
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final BlockState renderState;
    private final int tickRate;
    private final String[] categories;
    
    public SoilInfo(ResourceLocation id, Ingredient ingredient, BlockState renderState, int tickRate, String[] categories) {
        
        this.id = id;
        this.ingredient = ingredient;
        this.renderState = renderState;
        this.tickRate = tickRate;
        this.categories = categories;
    }
    
    public ResourceLocation getId () {
        
        return this.id;
    }
    
    public int getTickRate () {
        
        return this.tickRate;
    }
    
    public Ingredient getIngredient () {
        
        return this.ingredient;
    }
    
    public BlockState getRenderState () {
        
        return this.renderState;
    }
    
    public String[] getCategories () {
        
        return this.categories;
    }
    
    public static SoilInfo deserialize (ResourceLocation id, JsonObject json) {
        
        final Ingredient input = Ingredient.deserialize(json.getAsJsonObject("input"));
        final BlockState renderState = MCJsonUtils.deserializeBlockState(json.getAsJsonObject("display"));
        final int tickRate = JSONUtils.getInt(json, "ticks");
        final Set<String> categories = new HashSet<>();
        
        for (final JsonElement element : json.getAsJsonArray("categories")) {
            
            categories.add(element.getAsString().toLowerCase());
        }
        
        return new SoilInfo(id, input, renderState, tickRate, categories.toArray(new String[0]));
    }
    
    public ItemStack getRandomSoilBlock () {
        
        final ItemStack[] matchingStacks = this.ingredient.getMatchingStacks();
        return matchingStacks.length > 0 ? matchingStacks[Bookshelf.RANDOM.nextInt(matchingStacks.length)] : ItemStack.EMPTY;
    }
}