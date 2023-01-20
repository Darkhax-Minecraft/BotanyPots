package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.bookshelf.api.registry.RegistryObject;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.data.recipes.fertilizer.Fertilizer;
import net.darkhax.botanypots.data.recipes.potinteraction.PotInteraction;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.darkhax.botanypots.events.BotanyPotEventDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BotanyPotHelper {

    public static final CachedSupplier<RecipeType<Soil>> SOIL_TYPE = RegistryObject.deferred(BuiltInRegistries.RECIPE_TYPE, Constants.MOD_ID, "soil").cast();
    public static final CachedSupplier<RecipeSerializer<?>> SOIL_SERIALIZER = RegistryObject.deferred(BuiltInRegistries.RECIPE_SERIALIZER, Constants.MOD_ID, "soil").cast();

    public static final CachedSupplier<RecipeType<Crop>> CROP_TYPE = RegistryObject.deferred(BuiltInRegistries.RECIPE_TYPE, Constants.MOD_ID, "crop").cast();
    public static final CachedSupplier<RecipeSerializer<?>> CROP_SERIALIZER = RegistryObject.deferred(BuiltInRegistries.RECIPE_SERIALIZER, Constants.MOD_ID, "crop").cast();

    public static final CachedSupplier<RecipeType<PotInteraction>> POT_INTERACTION_TYPE = RegistryObject.deferred(BuiltInRegistries.RECIPE_TYPE, Constants.MOD_ID, "pot_interaction").cast();
    public static final CachedSupplier<RecipeSerializer<?>> SIMPLE_POT_INTERACTION_SERIALIZER = RegistryObject.deferred(BuiltInRegistries.RECIPE_SERIALIZER, Constants.MOD_ID, "pot_interaction").cast();

    public static final CachedSupplier<RecipeType<Fertilizer>> FERTILIZER_TYPE = RegistryObject.deferred(BuiltInRegistries.RECIPE_TYPE, Constants.MOD_ID, "fertilizer").cast();
    public static final CachedSupplier<RecipeSerializer<?>> BASIC_FERTILIZER_SERIALIZER = RegistryObject.deferred(BuiltInRegistries.RECIPE_SERIALIZER, Constants.MOD_ID, "fertilizer").cast();

    public static final BotanyPotEventDispatcher EVENT_DISPATCHER = Services.load(BotanyPotEventDispatcher.class);

    /**
     * Calculates the amount of growth ticks required for a crop to be considered fully grown.
     *
     * @param level The level of the world that the crop is growing in.
     * @param pos   The position of the pot growing the crop.
     * @param pot   The pot growing the crop.
     * @param crop  The crop being grown.
     * @param soil  The soil being grown.
     * @return The amount of growth ticks required for a crop to be fully grown. A result of -1 indicates that the crop
     * can not grow.
     */
    public static int getRequiredGrowthTicks(Level level, BlockPos pos, BlockEntityBotanyPot pot, @Nullable Crop crop, @Nullable Soil soil) {

        if (crop == null || soil == null) {

            return -1;
        }

        final float requiredGrowthTicks = crop.getGrowthTicks(level, pos, pot, soil);
        final float growthModifier = soil.getGrowthModifier(level, pos, pot, crop);

        if (growthModifier >= 0) {

            return Mth.floor(requiredGrowthTicks / growthModifier);
        }

        return -1;
    }

    /**
     * Tests if a crop can grow in the specified growing condition.s
     *
     * @param level The level of the world that the crop is growing in.
     * @param pos   The position of the pot growing the crop.
     * @param pot   The pot that the crop is growing in.
     * @param soil  The soil that the crop is growing in.
     * @param crop  The crop being grown.
     * @return Whether the crop can grow or not.
     */
    public static boolean canCropGrow(Level level, BlockPos pos, BlockEntityBotanyPot pot, Soil soil, Crop crop) {

        if (soil == null || crop == null) {

            return false;
        }

        return soil.canGrowCrop(level, pos, pot, crop) && crop.canGrowInSoil(level, pos, pot, soil);
    }

    /**
     * Attempts to find a soil entry in the game recipe manager.
     *
     * @param level     The level of the world that the soil is being placed.
     * @param pos       The position of the pot.
     * @param pot       The pot that the soil would go into.
     * @param soilStack The item being used.
     * @return A soil that is applicable for the provided context. If no soil is found the result will be null.
     */
    @Nullable
    public static Soil findSoil(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack soilStack) {

        Soil result = null;

        if (level != null && !soilStack.isEmpty()) {

            for (final Soil soil : getAllRecipes(level.getRecipeManager(), SOIL_TYPE.get())) {

                if (soil.matchesLookup(level, pos, pot, soilStack)) {

                    result = soil;
                    break;
                }
            }
        }

        return EVENT_DISPATCHER.postSoilLookup(level, pos, pot, soilStack, result);
    }

    /**
     * Attempts to find a crop entry in the game recipe manager.
     *
     * @param level The level of the world that the crop is being placed.
     * @param pos   The position of the pot.
     * @param pot   The pot that the crop would go into.
     * @param stack The item being used.
     * @return A crop that is applicable for the provided context. If no crop is found the result will be null.
     */
    @Nullable
    public static Crop findCrop(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack) {

        Crop result = null;

        if (level != null && !stack.isEmpty()) {

            for (final Crop crop : getAllRecipes(level.getRecipeManager(), CROP_TYPE.get())) {

                if (crop.matchesLookup(level, pos, pot, stack)) {

                    result = crop;
                    break;
                }
            }
        }

        return EVENT_DISPATCHER.postCropLookup(level, pos, pot, stack, result);
    }

    public static <C extends Container, T extends Recipe<C>> List<T> getAllRecipes(RecipeManager manager, RecipeType<T> type) {

        return manager.getAllRecipesFor(type);
    }

    /**
     * Attempts to find a pot interaction based on the provided context.
     *
     * @param state     The state of the pot being interacted with.
     * @param world     The world the pot is in.
     * @param pos       The position of the pot.
     * @param player    The player interacting with the pot.
     * @param hand      The hand being used.
     * @param heldStack The stack being used.
     * @param pot       The pot being interacted with.
     * @return A pot interaction applicable for the provided context. If no interaction could be found the result will
     * be null.
     */
    @Nullable
    public static PotInteraction findPotInteraction(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot) {

        PotInteraction result = null;

        if (!heldStack.isEmpty()) {

            for (final PotInteraction interaction : getAllRecipes(world.getRecipeManager(), POT_INTERACTION_TYPE.get())) {

                if (interaction.canApply(state, world, pos, player, hand, heldStack, pot)) {

                    result = interaction;
                    break;
                }
            }
        }

        return EVENT_DISPATCHER.postInteractionLookup(state, world, pos, player, hand, heldStack, pot, result);
    }

    /**
     * Attempts to find a fertilizer based on the provided context.
     *
     * @param state     The state of the pot being interacted with.
     * @param world     The world the pot is in.
     * @param pos       The position of the pot.
     * @param player    The player interacting with the pot.
     * @param hand      The hand being used.
     * @param heldStack The stack being used.
     * @param pot       The pot being interacted with.
     * @return A fertilizer applicable for the provided context. If no fertilizer could be found the result will be
     * null.
     */
    @Nullable
    public static Fertilizer findFertilizer(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot) {

        Fertilizer result = null;

        if (!heldStack.isEmpty()) {

            for (final Fertilizer fertilizer : getAllRecipes(world.getRecipeManager(), FERTILIZER_TYPE.get())) {

                if (fertilizer.canApply(state, world, pos, player, hand, heldStack, pot)) {

                    result = fertilizer;
                    break;
                }
            }
        }

        return EVENT_DISPATCHER.postFertilizerLookup(state, world, pos, player, hand, heldStack, pot, result);
    }

    /**
     * Generates the harvested drops for a crop.
     *
     * @param level The world the pot is in.
     * @param pos   The position of the pot.
     * @param pot   The pot being harvested from.
     * @param crop  The crop being harvested.
     * @return A list of drops generated by harvesting the crop.
     */
    public static List<ItemStack> generateDrop(Random rng, Level level, BlockPos pos, BlockEntityBotanyPot pot, Crop crop) {

        return EVENT_DISPATCHER.postCropDrops(rng, level, pos, pot, crop, crop.generateDrops(rng, level, pos, pot));
    }
}
