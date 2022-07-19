package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.bookshelf.api.registry.RegistryObject;
import net.darkhax.bookshelf.api.util.MathsHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.CropInfo;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.darkhax.botanypots.data.recipes.potinteraction.PotInteraction;
import net.darkhax.botanypots.data.recipes.soil.SoilInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class BotanyPotHelper {

    public static final CachedSupplier<RecipeType<SoilInfo>> SOIL_TYPE = RegistryObject.deferred(Registry.RECIPE_TYPE, Constants.MOD_ID, "soil").cast();
    public static final CachedSupplier<RecipeSerializer<?>> SOIL_SERIALIZER = RegistryObject.deferred(Registry.RECIPE_SERIALIZER, Constants.MOD_ID, "soil").cast();

    public static final CachedSupplier<RecipeType<CropInfo>> CROP_TYPE = RegistryObject.deferred(Registry.RECIPE_TYPE, Constants.MOD_ID, "crop").cast();
    public static final CachedSupplier<RecipeSerializer<?>> CROP_SERIALIZER = RegistryObject.deferred(Registry.RECIPE_SERIALIZER, Constants.MOD_ID, "crop").cast();

    public static final CachedSupplier<RecipeType<PotInteraction>> POT_INTERACTION_TYPE = RegistryObject.deferred(Registry.RECIPE_TYPE, Constants.MOD_ID, "pot_interaction").cast();
    public static final CachedSupplier<RecipeSerializer<?>> SIMPLE_POT_INTERACTION_SERIALIZER = RegistryObject.deferred(Registry.RECIPE_SERIALIZER, Constants.MOD_ID, "simple_pot_interaction").cast();

    public static Optional<SoilInfo> getSoil(RecipeManager manager, ResourceLocation id) {

        return (Optional<SoilInfo>) manager.byKey(id);
    }

    public static Optional<CropInfo> getCrop(RecipeManager manager, ResourceLocation id) {

        return (Optional<CropInfo>) manager.byKey(id);
    }

    /**
     * Gets the total amount of world ticks required for a specific crop to reach maturity when planted in a given
     * soil.
     *
     * @param crop The crop to calculate.
     * @param soil The soil to calculate.
     * @return The total amount of world ticks for the crop to reach maturity when planted in the given soil.
     */
    public static int getRequiredGrowthTicks(@Nullable CropInfo crop, @Nullable SoilInfo soil) {

        return crop == null || soil == null ? -1 : crop.getGrowthTicksForSoil(soil);
    }

    @Nullable
    public static SoilInfo getSoil(@Nullable Level worldLevel, ItemStack item) {

        return worldLevel != null ? getSoil(worldLevel.getRecipeManager(), item) : null;
    }

    @Nullable
    public static SoilInfo getSoil(@Nullable RecipeManager manager, ItemStack item) {

        if (manager != null && !item.isEmpty()) {

            for (final SoilInfo soilInfo : manager.getAllRecipesFor(SOIL_TYPE.get())) {

                if (soilInfo.getIngredient().test(item)) {

                    return soilInfo;
                }
            }
        }

        return null;
    }

    @Nullable
    public static CropInfo getCrop(@Nullable Level worldLevel, ItemStack item) {

        return worldLevel != null ? getCrop(worldLevel.getRecipeManager(), item) : null;
    }

    @Nullable
    public static CropInfo getCrop(RecipeManager manager, ItemStack item) {

        if (!item.isEmpty()) {

            for (final CropInfo cropInfo : manager.getAllRecipesFor(CROP_TYPE.get())) {

                if (cropInfo.getSeed().test(item)) {

                    return cropInfo;
                }
            }
        }

        return null;
    }

    /**
     * Checks if a crop can be planted in a given soil.
     *
     * @param soil The soil to plant in.
     * @param crop The crop to plant.
     * @return Whether the soil is valid for the crop or not.
     */
    public static boolean isSoilValidForCrop(SoilInfo soil, CropInfo crop) {

        if (soil == null || crop == null) {

            return false;
        }

        for (final String soilCategory : soil.getCategories()) {

            for (final String cropCategory : crop.getSoilCategories()) {

                if (soilCategory.equalsIgnoreCase(cropCategory)) {

                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public static PotInteraction findPotInteraction(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot) {

        if (!heldStack.isEmpty()) {

            for (final PotInteraction interaction : world.getRecipeManager().getAllRecipesFor(POT_INTERACTION_TYPE.get())) {

                if (interaction.canApply(state, world, pos, player, hand, heldStack, pot)) {

                    return interaction;
                }
            }
        }

        return null;
    }

    public static NonNullList<ItemStack> generateDrop(Random rand, CropInfo crop) {

        final NonNullList<ItemStack> drops = NonNullList.create();

        for (final HarvestEntry cropEntry : crop.getResults()) {

            if (rand.nextFloat() <= cropEntry.getChance()) {

                final int rolls = MathsHelper.nextIntInclusive(rand, cropEntry.getMinRolls(), cropEntry.getMaxRolls());

                if (rolls > 0) {

                    for (int roll = 0; roll < rolls; roll++) {

                        drops.add(cropEntry.getItem().copy());
                    }
                }
            }
        }

        return drops;
    }
}