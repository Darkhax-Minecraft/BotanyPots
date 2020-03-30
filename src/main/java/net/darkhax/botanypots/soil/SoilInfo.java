package net.darkhax.botanypots.soil;

import java.util.Set;

import net.darkhax.bookshelf.Bookshelf;
import net.darkhax.bookshelf.item.crafting.RecipeDataBase;
import net.darkhax.botanypots.BotanyPots;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class SoilInfo extends RecipeDataBase {
    
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
     * A modifier applied to the growth time of the crop.
     */
    private float growthModifier;
    
    /**
     * An array of associated soil categories.
     */
    private Set<String> categories;
    
    public SoilInfo(ResourceLocation id, Ingredient ingredient, BlockState renderState, float growthModifier, Set<String> categories) {
        
        this.id = id;
        this.ingredient = ingredient;
        this.renderState = renderState;
        this.growthModifier = growthModifier;
        this.categories = categories;
    }
    
    @Override
    public ResourceLocation getId () {
        
        return this.id;
    }
    
    public float getGrowthModifier () {
        
        return this.growthModifier;
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
    
    @Deprecated
    public ItemStack getFirstSoil () {
        
        final ItemStack[] matchingStacks = this.ingredient.getMatchingStacks();
        return matchingStacks.length > 0 ? matchingStacks[0] : ItemStack.EMPTY;
    }
    
    public void setIngredient (Ingredient ingredient) {
        
        this.ingredient = ingredient;
    }
    
    public void setRenderState (BlockState renderState) {
        
        this.renderState = renderState;
    }
    
    public void setGrowthModifier (float modifier) {
        
        this.growthModifier = modifier;
    }
    
    public void setCategories (Set<String> categories) {
        
        this.categories = categories;
    }
    
    public ITextComponent getName () {
        
        return this.getRenderState().getBlock().getNameTextComponent();
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