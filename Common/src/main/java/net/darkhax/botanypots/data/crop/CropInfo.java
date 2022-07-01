package net.darkhax.botanypots.data.crop;

import net.darkhax.botanypots.tempshelf.DisplayableBlockState;
import net.darkhax.bookshelf.api.data.recipes.RecipeBaseData;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.data.soil.SoilInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Set;

public class CropInfo extends RecipeBaseData<Container> {

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
    private DisplayableBlockState[] displayBlocks;

    /**
     * The light level of the soil when placed in the crop. If this is not specified the light
     * level of the first block in {@link #displayBlocks} will be used.
     */
    private int lightLevel;

    public CropInfo(ResourceLocation id, Ingredient seed, Set<String> soilCategories, int growthTicks, List<HarvestEntry> results, DisplayableBlockState[] displayStates, int lightLevel) {

        super(id);
        this.seed = seed;
        this.soilCategories = soilCategories;
        this.growthTicks = growthTicks;
        this.results = results;
        this.displayBlocks = displayStates;
        this.lightLevel = lightLevel;
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
    public DisplayableBlockState[] getDisplayState () {

        return this.displayBlocks;
    }

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

            return Mth.floor(requiredGrowthTicks * (1 + growthModifier * -1));
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

    public void setDisplayBlock (DisplayableBlockState[] displayBlocks) {

        this.displayBlocks = displayBlocks;
    }

    public void setLightLevel (int lightLevel) {

        this.lightLevel = lightLevel;
    }

    public int getLightLevel () {

        return this.lightLevel;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return BotanyPotHelper.CROP_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {

        return BotanyPotHelper.CROP_TYPE.get();
    }
}