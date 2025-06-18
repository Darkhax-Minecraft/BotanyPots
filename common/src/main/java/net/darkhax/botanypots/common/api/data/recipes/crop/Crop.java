package net.darkhax.botanypots.common.api.data.recipes.crop;

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

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Represents a type of crop that can grow in the botany pot.
 */
public abstract class Crop extends BotanyPotRecipe {

    public static final Supplier<RecipeType<Crop>> TYPE = CachedSupplier.of(BuiltInRegistries.RECIPE_TYPE, BotanyPotsMod.id("crop")).cast();
    public static final SidedReloadableCache<Map<ResourceLocation, RecipeHolder<Crop>>> RECIPES = SidedReloadableCache.recipes(TYPE);
    public static final SidedReloadableCache<RecipeCache<Crop>> CACHE = RecipeCache.of(TYPE);

    /**
     * Handles the harvesting logic for the crop. This is invoked when the crop is harvested by a player or through some
     * automatic action like a hopping botany pot.
     *
     * @param context Additional context about the pot that is growing the crop and how the crop was harvested.
     * @param level   The world in which the crop was harvested.
     * @param drops   Collects items that were dropped as a result of harvesting the crop. For example, this may drop
     *                the items into the world or insert them into the pots inventory depending on how the crop was
     *                harvested.
     */
    public abstract void onHarvest(BotanyPotContext context, Level level, Consumer<ItemStack> drops);

    /**
     * Gets the display states that should be rendered for the crop. When multiple displays are returned they will be
     * rendered in bottom up order. For example if the crop is a 2 block tall multi-block you could return the lower
     * block as the first display and the top block as the second display.
     *
     * @param context Additional context about the pot that is growing the crop.
     * @param level   The world the crop is growing in.
     * @return A list of display states to render for the crop.
     */
    public abstract List<Display> getDisplayState(BotanyPotContext context, Level level);

    /**
     * The amount of ticks required for the crop to grow under normal circumstance. The result should not include
     * fertilizer, soil growth modifiers, or any other modifier that is not inherent to the crop.
     *
     * @param context Additional context about the pot that is growing the crop.
     * @param level   The world the crop is growing in.
     * @return The base amount of ticks required to grow the crop.
     */
    public abstract int getRequiredGrowthTicks(BotanyPotContext context, Level level);

    /**
     * Checks if the crop can still grow under the current circumstances. This will be used to revalidate if the crop
     * can continue to grow in the pot or if it is invalid. Crops that have reached max age should still have their
     * growth sustained.
     *
     * @param context Additional context about the pot that is growing the crop.
     * @param level   The world the crop is growing in.
     * @return If the crop can still grow under the current circumstances.
     */
    public abstract boolean isGrowthSustained(BotanyPotContext context, Level level);

    /**
     * Gets the light level emitted by the crop.
     *
     * @param context Additional context about the pot that is growing the crop.
     * @param level   The world the crop is growing in.
     * @return The light level emitted by the crop.
     */
    public int getLightLevelEmitted(BotanyPotContext context, Level level) {
        return 0;
    }

    /**
     * Gets the base chance that the crop will drop items when harvested.
     *
     * @param context Context for how the crop is being grown.
     * @param level   The world the crop is growing in.
     * @return The base chance that the crop drops items when harvested.
     */
    public float getBaseYield(BotanyPotContext context, Level level) {
        return 1f;
    }

    /**
     * Determines how much drop chance modifiers from other sources affect the base drop chance of the crop.
     *
     * @param context Context for how the crop is being grown.
     * @param level   The world the crop is growing in.
     * @return The scale to apply to drop modifiers.
     */
    public float getYieldScale(BotanyPotContext context, Level level) {
        return 1f;
    }

    /**
     * Invoked every tick while the crop is in a pot. This can be used for adding tick based effects to your crop, like
     * a random chance to spawn a mob, mutate, or wither.
     *
     * @param context Additional context about the pot that is growing the crop.
     * @param level   The world the crop is growing in.
     */
    public void onTick(BotanyPotContext context, Level level) {
        // No op
    }

    /**
     * Invoked every tick while the crop is in a pot and the growth of the crop is sustained.
     *
     * @param context Additional context about the pot that is growing the crop.
     * @param level   The world the crop is growing in.
     */
    public void onGrowthTick(BotanyPotContext context, Level level) {
        // No op
    }

    /**
     * Checks if the crop can be harvested. This can be used to add harvest conditions that are unrelated to the
     * standard growth logic, like only allowing the crop to be harvested at night.
     *
     * @param context Additional context about the pot that is growing the crop.
     * @param level   The world the crop is growing in.
     * @return If the crop can be harvested.
     */
    public boolean canHarvest(BotanyPotContext context, Level level) {
        return true;
    }

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return TYPE.get();
    }

    /**
     * Gets the crop that best represents an item.
     *
     * @param level   The current game level.
     * @param context The current game context.
     * @param stack   The item to lookup.
     * @return The crop that best represents the item.
     */
    @Nullable
    public static RecipeHolder<Crop> getCrop(Level level, BotanyPotContext context, ItemStack stack) {
        final RecipeCache<Crop> cache = CACHE.apply(level);
        return cache != null ? cache.lookup(stack, context, level) : null;
    }
}