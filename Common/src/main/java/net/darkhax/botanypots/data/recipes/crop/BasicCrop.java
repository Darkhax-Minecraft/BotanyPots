package net.darkhax.botanypots.data.recipes.crop;

import net.darkhax.bookshelf.api.util.MathsHelper;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class BasicCrop extends Crop {

    /**
     * The ingredient used for the crop's seed.
     */
    protected Ingredient seed;

    /**
     * An array of valid soil categories.
     */
    protected Set<String> soilCategories;

    /**
     * The amount of ticks for the crop to grow under normal conditions.
     */
    protected int growthTicks;

    /**
     * An array of things the crop can drop.
     */
    protected List<HarvestEntry> results;

    /**
     * The BlockState to render for the crop.
     */
    protected List<DisplayState> displayStates;

    /**
     * The light level of the soil when placed in the crop. If this is not specified the light level of the first block
     * in {@link #displayStates} will be used.
     */
    protected int lightLevel;

    public BasicCrop(ResourceLocation id, Ingredient seed, Set<String> soilCategories, int growthTicks, List<HarvestEntry> results, List<DisplayState> displayStates, int lightLevel) {

        super(id);
        this.seed = seed;
        this.soilCategories = soilCategories;
        this.growthTicks = growthTicks;
        this.results = results;
        this.displayStates = displayStates;
        this.lightLevel = lightLevel;
    }

    @Override
    public boolean matchesLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack placedStack) {

        return this.seed.test(placedStack);
    }

    @Override
    public int getGrowthTicks(Level level, BlockPos pos, BlockEntityBotanyPot pot, @Nullable Soil soil) {

        return this.growthTicks;
    }

    @Override
    public int getLightLevel(Level level, BlockPos pos, BlockEntityBotanyPot pot) {

        return this.lightLevel;
    }

    @Override
    public boolean canGrowInSoil(Level level, BlockPos pos, BlockEntityBotanyPot pot, Soil soil) {

        for (final String soilCategory : soil.getCategories(level, pos, pot)) {

            for (final String cropCategory : this.getCategories(level, pos, pot)) {

                if (soilCategory.equalsIgnoreCase(cropCategory)) {

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Set<String> getCategories(Level level, BlockPos pos, BlockEntityBotanyPot pot) {

        return this.soilCategories;
    }

    @Override
    public List<DisplayState> getDisplayState(Level level, BlockPos pos, BlockEntityBotanyPot pot) {

        return this.displayStates;
    }

    @Override
    public List<ItemStack> generateDrops(Level level, BlockPos pos, BlockEntityBotanyPot pot) {

        final NonNullList<ItemStack> drops = NonNullList.create();

        for (final HarvestEntry cropEntry : this.results) {

            if (level.random.nextFloat() <= cropEntry.getChance()) {

                final int rolls = MathsHelper.nextIntInclusive(level.random, cropEntry.getMinRolls(), cropEntry.getMaxRolls());

                if (rolls > 0) {

                    for (int roll = 0; roll < rolls; roll++) {

                        drops.add(cropEntry.getItem().copy());
                    }
                }
            }
        }

        return drops;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return BotanyPotHelper.CROP_SERIALIZER.get();
    }
}