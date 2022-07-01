package net.darkhax.botanypots;

import java.util.Optional;
import javax.annotation.Nullable;

import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.botanypots.data.crop.CropInfo;
import net.darkhax.botanypots.data.soil.SoilInfo;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class BotanyPotHelper {

    public static final CachedSupplier<RecipeType<SoilInfo>> SOIL_TYPE = CachedSupplier.cache(() -> (RecipeType<SoilInfo>) Registry.RECIPE_TYPE.get(new ResourceLocation(Constants.MOD_ID, "soil")));
    public static final CachedSupplier<RecipeSerializer<?>> SOIL_SERIALIZER = CachedSupplier.cache(() -> Registry.RECIPE_SERIALIZER.get(new ResourceLocation(Constants.MOD_ID, "soil")));

    public static final CachedSupplier<RecipeType<CropInfo>> CROP_TYPE = CachedSupplier.cache(() -> (RecipeType<CropInfo>) Registry.RECIPE_TYPE.get(new ResourceLocation(Constants.MOD_ID, "crop")));
    public static final CachedSupplier<RecipeSerializer<?>> CROP_SERIALIZER = CachedSupplier.cache(() -> Registry.RECIPE_SERIALIZER.get(new ResourceLocation(Constants.MOD_ID, "crop")));

    public static Optional<SoilInfo> getSoil (RecipeManager manager, ResourceLocation id) {

        return (Optional<SoilInfo>) manager.byKey(id);
    }

    public static Optional<CropInfo> getCrop (RecipeManager manager, ResourceLocation id) {

        return (Optional<CropInfo>) manager.byKey(id);
    }

    /**
     * Gets the total amount of world ticks required for a specific crop to reach maturity when
     * planted in a given soil.
     *
     * @param crop The crop to calculate.
     * @param soil The soil to calculate.
     * @return The total amount of world ticks for the crop to reach maturity when planted in
     *         the given soil.
     */
    public static int getRequiredGrowthTicks (@Nullable CropInfo crop, @Nullable SoilInfo soil) {

        return crop == null || soil == null ? -1 : crop.getGrowthTicksForSoil(soil);
    }

    @Nullable
    public static SoilInfo getSoil (RecipeManager manager, ItemStack item) {

        for (final SoilInfo soilInfo : manager.getAllRecipesFor(SOIL_TYPE.get())) {

            if (soilInfo.getIngredient().test(item)) {

                return soilInfo;
            }
        }

        return null;
    }

    @Nullable
    public static CropInfo getCrop (RecipeManager manager, ItemStack item) {

        for (final CropInfo cropInfo : manager.getAllRecipesFor(CROP_TYPE.get())) {

            if (cropInfo.getSeed().test(item)) {

                return cropInfo;
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
    public static boolean isSoilValidForCrop (SoilInfo soil, CropInfo crop) {

        for (final String soilCategory : soil.getCategories()) {

            for (final String cropCategory : crop.getSoilCategories()) {

                if (soilCategory.equalsIgnoreCase(cropCategory)) {

                    return true;
                }
            }
        }

        return false;
    }
}