package net.darkhax.botanypots.crop;

import java.util.List;
import java.util.Set;

import net.darkhax.bookshelf.crafting.RecipeDataBase;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

public class CropInfo extends RecipeDataBase {
    
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
    private BlockState[] displayBlocks;
    
    public CropInfo(ResourceLocation id, Ingredient seed, Set<String> soilCategories, int growthTicks, List<HarvestEntry> results, BlockState[] displayStates) {
        
        super(id);
        this.seed = seed;
        this.soilCategories = soilCategories;
        this.growthTicks = growthTicks;
        this.results = results;
        this.displayBlocks = displayStates;
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
    public BlockState[] getDisplayState () {
        
        return this.displayBlocks;
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
    
    public void setDisplayBlock (BlockState[] displayBlocks) {
        
        this.displayBlocks = displayBlocks;
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer () {
        
        return BotanyPots.instance.getContent().recipeSerializerCrop;
    }
    
    @Override
    public IRecipeType<?> getType () {
        
        return BotanyPots.instance.getContent().recipeTypeCrop;
    }
    
    public ITextComponent getName () {
        
        // TODO Ask forge to give me the old code back.
        // TODO allow json override
        return new TranslationTextComponent(this.getDisplayState()[0].getBlock().getTranslationKey());
    }
    
    public int getLightLevel (IBlockReader world, BlockPos pos) {
        
        // TODO allow json override
        return this.getDisplayState()[0].getLightValue(world, pos);
    }
}