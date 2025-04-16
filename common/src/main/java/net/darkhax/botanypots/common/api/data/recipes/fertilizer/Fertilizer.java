package net.darkhax.botanypots.common.api.data.recipes.fertilizer;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.function.SidedReloadableCache;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
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
 * Represents a fertilizer interaction with a crop.
 */
public abstract class Fertilizer extends BotanyPotRecipe {

    public static final Supplier<RecipeType<Fertilizer>> TYPE = CachedSupplier.of(BuiltInRegistries.RECIPE_TYPE, BotanyPotsMod.id("fertilizer")).cast();
    public static final SidedReloadableCache<Map<ResourceLocation, RecipeHolder<Fertilizer>>> RECIPES = SidedReloadableCache.recipes(TYPE);
    public static final SidedReloadableCache<RecipeCache<Fertilizer>> CACHE = RecipeCache.of(TYPE);

    /**
     * Applies the fertilizer to the crop.
     *
     * @param context The context that the fertilizer is being used in.
     * @param level   The current game level.
     */
    public abstract void apply(@NotNull BotanyPotContext context, @NotNull Level level);

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return TYPE.get();
    }

    @Nullable
    public static RecipeHolder<Fertilizer> getFertilizer(Level level, BotanyPotContext context, ItemStack stack) {
        final RecipeCache<Fertilizer> cache = CACHE.apply(level);
        return cache != null ? cache.lookup(stack, context, level) : null;
    }
}