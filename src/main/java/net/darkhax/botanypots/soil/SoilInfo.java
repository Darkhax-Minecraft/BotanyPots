package net.darkhax.botanypots.soil;

import java.util.Optional;
import java.util.Set;

import net.darkhax.bookshelf.block.DisplayableBlockState;
import net.darkhax.bookshelf.crafting.RecipeDataBase;
import net.darkhax.botanypots.BotanyPots;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class SoilInfo extends RecipeDataBase {
    
    /**
     * The item used to get the soil into the pot.
     */
    private Ingredient ingredient;
    
    /**
     * The blockstate used to render the soil.
     */
    private DisplayableBlockState renderState;
    
    /**
     * A modifier applied to the growth time of the crop.
     */
    private float growthModifier;
    
    /**
     * An array of associated soil categories.
     */
    private Set<String> categories;
    
    /**
     * The light level of the soil when placed in the crop. If this is not specified the light
     * level of {@link #renderState} will be used.
     */
    private Optional<Integer> lightLevel;
    
    public SoilInfo(ResourceLocation id, Ingredient ingredient, BlockState renderState, float growthModifier, Set<String> categories, Optional<Integer> lightLevel) {
        
        this(id, ingredient, new DisplayableBlockState(renderState), growthModifier, categories, lightLevel);
    }
    
    public SoilInfo(ResourceLocation id, Ingredient ingredient, DisplayableBlockState renderState, float growthModifier, Set<String> categories, Optional<Integer> lightLevel) {
        
        super(id);
        this.ingredient = ingredient;
        this.renderState = renderState;
        this.growthModifier = growthModifier;
        this.categories = categories;
        this.lightLevel = lightLevel;
    }
    
    public float getGrowthModifier () {
        
        return this.growthModifier;
    }
    
    public Ingredient getIngredient () {
        
        return this.ingredient;
    }
    
    public DisplayableBlockState getRenderState () {
        
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
        
        this.setRenderState(new DisplayableBlockState(renderState));
    }
    
    public void setRenderState (DisplayableBlockState renderState) {
        
        this.renderState = renderState;
    }
    
    public void setGrowthModifier (float modifier) {
        
        this.growthModifier = modifier;
    }
    
    public void setCategories (Set<String> categories) {
        
        this.categories = categories;
    }
    
    public void setLightLevel (int lightLevel) {
        
        this.lightLevel = Optional.of(lightLevel);
    }
    
    public Optional<Integer> getLightLevel () {
        
        return this.lightLevel;
    }
    
    public int getLightLevel (IBlockReader world, BlockPos pos) {
        
        return this.getLightLevel().orElseGet( () -> this.renderState.getState().getLightValue(world, pos));
    }
    
    @Override
    public boolean isDynamic () {
        
        return true;
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer () {
        
        return BotanyPots.instance.getContent().recipeSerializerSoil;
    }
    
    @Override
    public IRecipeType<?> getType () {
        
        return BotanyPots.instance.getContent().recipeTypeSoil;
    }
}