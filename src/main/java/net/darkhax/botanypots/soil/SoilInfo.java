package net.darkhax.botanypots.soil;

import java.util.Set;

import net.darkhax.bookshelf.Bookshelf;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.RecipeData;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class SoilInfo extends RecipeData {
    
    /**
     * The id of the soil entry.
     */
    private final ResourceLocation id;
    
    /**
     * The item used to get the soil into the pot.
     */
    private Ingredient ingredient;
    
    /**
     * The blockstate used to render the soil.
     */
    private BlockState renderState;
    
    /**
     * The base tick rate of the soil.
     */
    private int tickRate;
    
    /**
     * An array of associated soil categories.
     */
    private Set<String> categories;
    
    public SoilInfo(ResourceLocation id, Ingredient ingredient, BlockState renderState, int tickRate, Set<String> categories) {
        
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
    
    public Set<String> getCategories () {
        
        return this.categories;
    }
    
    public ItemStack getRandomSoilBlock () {
        
        final ItemStack[] matchingStacks = this.ingredient.getMatchingStacks();
        return matchingStacks.length > 0 ? matchingStacks[Bookshelf.RANDOM.nextInt(matchingStacks.length)] : ItemStack.EMPTY;
    }
    
    public void setIngredient (Ingredient ingredient) {
        
        this.ingredient = ingredient;
    }
    
    public void setRenderState (BlockState renderState) {
        
        this.renderState = renderState;
    }
    
    public void setTickRate (int tickRate) {
        
        this.tickRate = tickRate;
    }
    
    public void setCategories (Set<String> categories) {
        
        this.categories = categories;
    }

    @Override
    public IRecipeSerializer<?> getSerializer () {
        
        return BotanyPots.instance.getContent().getRecipeSerializerSoil();
    }

    @Override
    public IRecipeType<?> getType () {
        
        return BotanyPots.instance.getContent().getRecipeTypeSoil();
    }
}