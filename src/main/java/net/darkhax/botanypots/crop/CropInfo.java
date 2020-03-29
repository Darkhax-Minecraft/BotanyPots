package net.darkhax.botanypots.crop;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.Bookshelf;
import net.darkhax.bookshelf.item.crafting.RecipeDataBase;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

public class CropInfo extends RecipeDataBase {
    
    /**
     * The id of the crop.
     */
    private final ResourceLocation id;
    
    /**
     * The ingredient used for the crop's seed.
     */
    private Ingredient seed;
    
    /**
     * An array of valid soil categories.
     */
    private Set<String> soilCategories;
    
    /**
     * The amount of ticks for the crop to grow under normal conditions.
     */
    private int growthTicks;
    
    /**
     * An array of things the crop can drop.
     */
    private List<HarvestEntry> results;
    
    /**
     * The BlockState to render for the crop.
     */
    private BlockState displayBlock;
    
    public CropInfo(ResourceLocation id, Ingredient seed, Set<String> soilCategories, int growthTicks, List<HarvestEntry> results, BlockState displayState) {
        
        this.id = id;
        this.seed = seed;
        this.soilCategories = soilCategories;
        this.growthTicks = growthTicks;
        this.results = results;
        this.displayBlock = displayState;
    }
    
    /**
     * Gets the internal ID of the crop.
     * 
     * @return The internal ID of the crop.
     */
    @Override
    public ResourceLocation getId () {
        
        return this.id;
    }
    
    /**
     * Gets an ingredient that can be used to match an ItemStack as a seed for this crop.
     * 
     * @return An ingredient that can used to match an ItemStack as a seed for the crop.
     */
    public Ingredient getSeed () {
        
        return this.seed;
    }
    
    /**
     * Gets all the soil categories that are valid for this crop.
     * 
     * @return An array of valid soil categories for this crop.
     */
    public Set<String> getSoilCategories () {
        
        return this.soilCategories;
    }
    
    /**
     * Gets all the possible results when harvesting the crop.
     * 
     * @return An array of harvest results for the crop.
     */
    public List<HarvestEntry> getResults () {
        
        return this.results;
    }
    
    /**
     * Gets the state to render when displaying the crop.
     * 
     * @return The state to display when rendering the crop.
     */
    public BlockState getDisplayState () {
        
        return this.displayBlock;
    }
    
    /**
     * Gets the growth tick factor for the crop.
     * 
     * @return The crop's growth tick factor.
     */
    
    /**
     * Gets the amount of ticks the crop needs to grow under normal circumstances.
     * 
     * @return The growth time for the crop under normal circumstances.
     */
    public int getGrowthTicks () {
        
        return this.growthTicks;
    }
    
    /**
     * Calculates the total world ticks for this crop to reach maturity if planted on a given
     * soil.
     * 
     * @param soil The soil to calculate growth time with.
     * @return The amount of world ticks it would take for this crop to reach maturity when
     *         planted on the given soil.
     */
    public int getGrowthTicksForSoil (SoilInfo soil) {
        
        final float requiredGrowthTicks = this.growthTicks;
        final float growthModifier = soil.getGrowthModifier();
        
        if (growthModifier > -1) {
            
            return MathHelper.floor(requiredGrowthTicks * (1 + growthModifier * -1));
        }
        
        return -1;
    }
    
    /**
     * Gets a random seed item. This is used when taking a seed out of a pot. Since seeds are
     * an ingredient multiple seeds may be possible. To ensure fairness this method will select
     * one of those items at random.
     * 
     * @return A random seed item.
     */
    public ItemStack getRandomSeed () {
        
        final ItemStack[] matchingStacks = this.seed.getMatchingStacks();
        return matchingStacks.length > 0 ? matchingStacks[Bookshelf.RANDOM.nextInt(matchingStacks.length)] : ItemStack.EMPTY;
    }
    
    public void setSeed (Ingredient seed) {
        
        this.seed = seed;
    }
    
    public void setSoilCategories (Set<String> soilCategories) {
        
        this.soilCategories = soilCategories;
    }
    
    public void setGrowthTicks (int growthTicks) {
        
        this.growthTicks = growthTicks;
    }
    
    public void setResults (List<HarvestEntry> results) {
        
        this.results = results;
    }
    
    public void setDisplayBlock (BlockState displayBlock) {
        
        this.displayBlock = displayBlock;
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer () {
        
        return BotanyPots.instance.getContent().getRecipeSerializerCrop();
    }
    
    @Override
    public IRecipeType<?> getType () {
        
        return BotanyPots.instance.getContent().getRecipeTypeCrop();
    }
    
    
    public ITextComponent getName () {
        
        return this.getDisplayState().getBlock().getNameTextComponent();
    }
}