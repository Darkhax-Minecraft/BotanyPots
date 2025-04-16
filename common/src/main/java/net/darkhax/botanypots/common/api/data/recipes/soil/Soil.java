package net.darkhax.botanypots.common.api.data.recipes.soil;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.function.SidedReloadableCache;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.recipes.BotanyPotRecipe;
import net.darkhax.botanypots.common.api.data.recipes.RecipeCache;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Represents additional properties for a soil in a botany pot.
 */
public abstract class Soil extends BotanyPotRecipe {

    public static final ResourceLocation TYPE_ID = BotanyPotsMod.id("soil");
    public static final Supplier<RecipeType<Soil>> TYPE = CachedSupplier.of(BuiltInRegistries.RECIPE_TYPE, TYPE_ID).cast();
    public static final SidedReloadableCache<Map<ResourceLocation, RecipeHolder<Soil>>> RECIPES = SidedReloadableCache.recipes(TYPE);
    public static final SidedReloadableCache<RecipeCache<Soil>> CACHE = RecipeCache.of(TYPE);

    /**
     * Gets the modifier to apply to the crop growth rate.
     *
     * @param context The current context.
     * @param level   The current game level.
     * @return The growth modifier of the soil.
     */
    public abstract float getGrowthModifier(@NotNull BotanyPotContext context, @NotNull Level level);

    /**
     * Gets the light level emitted by the soil.
     *
     * @param context The current context.
     * @param level   The current game level.
     * @return The light level emitted by the soil.
     */
    public abstract int getLightLevel(@NotNull BotanyPotContext context, @NotNull Level level);

    /**
     * Gets the display for the soil.
     *
     * @param context The current context.
     * @param level   The current game level.
     * @return The display for the soil.
     */
    public abstract Display getDisplay(BotanyPotContext context, @NotNull Level level);

    /**
     * An optional hook that fires every tick the soil is in a botany pot.
     *
     * @param context The current context.
     * @param level   The current game level.
     */
    public void onTick(BotanyPotContext context, Level level) {
        // No op
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return TYPE.get();
    }

    /**
     * Gets the soil that best represents a given item stack.
     *
     * @param level   The current game level.
     * @param context The current context.
     * @param stack   The item to lookup.
     * @return The soil that best represents the given item stack.
     */
    @Nullable
    public static RecipeHolder<Soil> getSoil(Level level, BotanyPotContext context, ItemStack stack) {
        final RecipeCache<Soil> cache = CACHE.apply(level);
        return cache != null ? cache.lookup(stack, context, level) : null;
    }
}