package net.darkhax.botanypots.data.recipes.crop;

import net.darkhax.bookshelf.api.data.recipes.RecipeBaseData;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class Crop extends RecipeBaseData<Container> {

    public Crop(ResourceLocation id) {

        super(id);
    }

    /**
     * Tests if the crop matches the provided placement context. This is used when looking up crops from their ItemStack
     * and determines which soil they represent.
     *
     * @param level       The current world level that the crop is in.
     * @param pos         The position of the pot.
     * @param pot         The pot being used.
     * @param placedStack The ItemStack being placed.
     * @return Whether the crop matches the lookup context or not.
     */
    public abstract boolean matchesLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack placedStack);

    /**
     * Gets the amount of growth ticks required to grow the crop.
     * <p>
     * The soil parameter may be null in some circumstances. This indicates that the soil is not available in the
     * current context, such as generating tooltip information.
     * <p>
     * This code is not responsible for applying the growth modifier of the soil. This is handled externally.
     * Implementations of this method should not factor this in themselves.
     *
     * @param level The current world level that the crop is in.
     * @param pos   The position that the pot is at.
     * @param pot   The pot growing the crop.
     * @param soil  The soil that the crop is growing in.
     * @return The amount of growth ticks required to grow the crop.
     */
    public abstract int getGrowthTicks(Level level, BlockPos pos, BlockEntityBotanyPot pot, @Nullable Soil soil);

    /**
     * Gets the light level emitted by the crop when placed in a pot.
     *
     * @param level The current world level that the crop is in.
     * @param pos   The position that the pot is at.
     * @param pot   The pot growing the crop.
     * @return The light level for the crop within the pot. This should be a value between 0 and 15.
     */
    public abstract int getLightLevel(Level level, BlockPos pos, BlockEntityBotanyPot pot);

    /**
     * Determines if the crop can grow in a given soil.
     *
     * @param level The current world level that the crop is in.
     * @param pos   The position of the pot.
     * @param pot   The pot growing the crop.
     * @param soil  The soil the crop is being grown in.
     * @return Whether the crop can grow in the soil.
     */
    public abstract boolean canGrowInSoil(Level level, BlockPos pos, BlockEntityBotanyPot pot, Soil soil);

    /**
     * Gets a set of soil categories the crop can grow in.
     *
     * @param level The current world level that the crop is in.
     * @param pos   The position of the pot.
     * @param pot   The pot growing the crop.
     * @return A set of soil categories that the crop can grow in.
     */
    public abstract Set<String> getCategories(Level level, BlockPos pos, BlockEntityBotanyPot pot);

    /**
     * Gets the display state of the crop. This will be used to render the crop in the pot.
     *
     * @param level The current world level that the pot is in.
     * @param pos   The position that the pot is at.
     * @param pot   The pot growing the crop.
     * @return A display state to render inside the pot.
     */
    public abstract List<DisplayState> getDisplayState(Level level, BlockPos pos, BlockEntityBotanyPot pot);

    /**
     * Generates a new list of drops to spawn when the crop has been harvested.
     *
     * @param level The current world level that the pot is in.
     * @param pos   The position of the pot.
     * @param pot   The pot growing the crop.
     * @return A list of drops to spawn when the crop is harvested.
     */
    public abstract List<ItemStack> generateDrops(Random rng, Level level, BlockPos pos, BlockEntityBotanyPot pot);

    /**
     * A hook that is invoked when a botany pot using the crop is ticked. This is invoked every tick, regardless of if
     * the crop can grow.
     *
     * @param level The current world level that the pot is in.
     * @param pos   The position of the pot.
     * @param pot   The pot growing the crop.
     */
    public void onTick(Level level, BlockPos pos, BlockEntityBotanyPot pot) {

    }

    /**
     * A hook that is invoked when the crop is being grown in a soil using the botany pot.
     *
     * @param level The current world level that the pot is in.
     * @param pos   The position of the pot.
     * @param pot   The pot growing the crop.
     * @param soil  The soil that the crop is growing in.
     */
    public void onGrowthTick(Level level, BlockPos pos, BlockEntityBotanyPot pot, Soil soil) {

    }

    @Override
    public RecipeType<?> getType() {

        return BotanyPotHelper.CROP_TYPE.get();
    }
}