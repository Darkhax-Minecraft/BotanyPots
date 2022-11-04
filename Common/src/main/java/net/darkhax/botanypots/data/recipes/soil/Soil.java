package net.darkhax.botanypots.data.recipes.soil;

import net.darkhax.bookshelf.api.data.recipes.RecipeBaseData;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Set;

public abstract class Soil extends RecipeBaseData<Container> {

    public Soil(ResourceLocation id) {

        super(id);
    }

    /**
     * Tests if the soil matches the provided placement context. This is used when looking up soils from their ItemStack
     * and determines which soil they represent.
     *
     * @param level       The current world level that the soil is in.
     * @param pos         The position of the pot.
     * @param pot         The pot being used.
     * @param placedStack The ItemStack being placed.
     * @return Whether the soil matches the lookup context or not.
     */
    public abstract boolean matchesLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack placedStack);

    /**
     * Gets the growth modifier applied by the soil when growing a crop. The growth modifier will be multiplied by the
     * crops total growth time to create the required growth time. For example a growth modifier of 0.5f would make a
     * crop that normally takes 1200 ticks to grow take just 600 ticks. A modifier of 2 would make the same crop take
     * 2400 ticks. The base growth modifier value is 1f.
     * <p>
     * The crop parameter may be null in some circumstances. This indicates that the crop is not available in the
     * current context, such as generating tooltip information.
     *
     * @param level The current world level that the soil is in.
     * @param pos   The position that the soil is being used at.
     * @param pot   The pot using the soil.
     * @param crop  The crop being grown in the soil.
     * @return The growth modifier applied to the crop growth time when planted in this soil.
     */
    public abstract float getGrowthModifier(Level level, BlockPos pos, BlockEntityBotanyPot pot, @Nullable Crop crop);

    /**
     * Gets the light level emitted by the soil when placed in a pot.
     *
     * @param level The current world level that the soil is in.
     * @param pos   The position that the soil is being used at.
     * @param pot   The pot using the soil.
     * @return The light level for the soil within the pot. This should be a value between 0 and 15.
     */
    public abstract int getLightLevel(Level level, BlockPos pos, BlockEntityBotanyPot pot);

    /**
     * Determines if a given crop can grow in this soil.
     *
     * @param level The current world level that the soil is in.
     * @param pos   The position that the soil is being used at.
     * @param pot   The pot using the soil.
     * @param crop  The crop being tested.
     * @return Whether the crop can grow in the soil or not.
     */
    public abstract boolean canGrowCrop(@Nullable Level level, @Nullable BlockPos pos, @Nullable BlockEntityBotanyPot pot, Crop crop);

    /**
     * Gets a set of soil categories associated with the soil.
     *
     * @param level The current world level that the soil is in.
     * @param pos   The position that the soil is being used at.
     * @param pot   The pot using the soil.
     * @return A set of soil categories associated with the soil.
     */
    public abstract Set<String> getCategories(@Nullable Level level, @Nullable BlockPos pos, @Nullable BlockEntityBotanyPot pot);

    /**
     * Gets the display state for the soil. This will be used to render the soil in the pot.
     *
     * @param level The current world level that the soil is in.
     * @param pos   The position that the soil is being used at.
     * @param pot   The pot using the soil.
     * @return A display state to render inside the botany pot.
     */
    public abstract DisplayState getDisplayState(Level level, BlockPos pos, BlockEntityBotanyPot pot);

    /**
     * A hook that is invoked when a botany pot using the soil is ticked. This is invoked every tick, regardless of if a
     * crop is being grown.
     *
     * @param level The current world level that the soil is in.
     * @param pos   The position that the soil is being used at.
     * @param pot   The pot using the soil.
     */
    public void onTick(Level level, BlockPos pos, BlockEntityBotanyPot pot) {

    }

    /**
     * A hook that is invoked when a crop is grown in the soil using a botany pot.
     *
     * @param level The current world level that the soil is in.
     * @param pos   The position that the soil is being used at.
     * @param pot   The pot using the soil.
     * @param crop  The crop being grown.
     */
    public void onGrowthTick(Level level, BlockPos pos, BlockEntityBotanyPot pot, Crop crop) {

    }

    @Override
    public RecipeType<?> getType() {

        return BotanyPotHelper.SOIL_TYPE.get();
    }
}