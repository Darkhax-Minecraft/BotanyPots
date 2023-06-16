package net.darkhax.botanypots.data.recipes.soil;

import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Set;

public class BasicSoil extends Soil {

    /**
     * The item used to get the soil into the pot.
     */
    protected Ingredient ingredient;

    /**
     * The blockstate used to render the soil.
     */
    protected DisplayState displayState;

    /**
     * A modifier applied to the growth time of the crop.
     */
    protected float growthModifier;

    /**
     * An array of associated soil categories.
     */
    protected Set<String> categories;

    /**
     * The light level for the soil when placed in a botany pot.
     */
    protected int lightLevel;

    public BasicSoil(ResourceLocation id, Ingredient ingredient, DisplayState renderState, float growthModifier, Set<String> categories, int lightLevel) {

        super(id);
        this.ingredient = ingredient;
        this.displayState = renderState;
        this.growthModifier = growthModifier;
        this.categories = categories;
        this.lightLevel = lightLevel;
    }

    @Override
    public boolean matchesLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack placedStack) {

        return this.ingredient.test(placedStack);
    }

    @Override
    public float getGrowthModifier(Level level, BlockPos pos, BlockEntityBotanyPot pot, @Nullable Crop crop) {

        return this.growthModifier;
    }

    @Override
    public int getLightLevel(Level level, BlockPos pos, BlockEntityBotanyPot pot) {

        return this.lightLevel;
    }

    @Override
    public boolean canGrowCrop(Level level, BlockPos pos, BlockEntityBotanyPot pot, Crop crop) {

        for (final String soilCategory : this.getCategories(level, pos, pot)) {

            for (final String cropCategory : crop.getCategories(level, pos, pot)) {

                if (soilCategory.equalsIgnoreCase(cropCategory)) {

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Set<String> getCategories(Level level, BlockPos pos, BlockEntityBotanyPot pot) {

        return this.categories;
    }

    @Override
    public DisplayState getDisplayState(Level level, BlockPos pos, BlockEntityBotanyPot pot) {

        return this.displayState;
    }

    @Override
    public RecipeType<?> getType() {

        return BotanyPotHelper.SOIL_TYPE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return BotanyPotHelper.SOIL_SERIALIZER.get();
    }


    public Ingredient getIngredient() {
        return ingredient;
    }

    public DisplayState getDisplayState() {
        return displayState;
    }

    public float getGrowthModifier() {
        return growthModifier;
    }

    public Set<String> getCategories() {
        return categories;
    }

    public int getLightLevel() {
        return lightLevel;
    }
}