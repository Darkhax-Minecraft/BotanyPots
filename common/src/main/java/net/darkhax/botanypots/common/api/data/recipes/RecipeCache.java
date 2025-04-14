package net.darkhax.botanypots.common.api.data.recipes;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.darkhax.bookshelf.common.api.function.ReloadableCache;
import net.darkhax.bookshelf.common.api.function.SidedReloadableCache;
import net.darkhax.botanypots.common.api.data.context.BotanyPotContext;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * An experimental cache that makes looking up botany pot recipes faster.
 *
 * @param <T> The type of recipe held by the cache.
 */
public final class RecipeCache<T extends BotanyPotRecipe> {

    private final Supplier<RecipeType<T>> recipeType;
    private final ReloadableCache<Map<ResourceLocation, RecipeHolder<T>>> recipeCache;
    private final Multimap<Item, RecipeHolder<T>> lookupCache = ArrayListMultimap.create();
    private final List<RecipeHolder<T>> uncached = new LinkedList<>();

    private RecipeCache(Supplier<RecipeType<T>> recipeType) {
        this.recipeType = recipeType;
        this.recipeCache = ReloadableCache.recipes(this.recipeType);
    }

    /**
     * Checks if an item exists in the cache.
     *
     * @param stack The item to test.
     * @return If the item has at least one match in the lookup cache.
     */
    public boolean isCached(ItemStack stack) {
        return !stack.isEmpty() && !lookupCache.get(stack.getItem()).isEmpty();
    }

    /**
     * Gets a map of all cached values.
     *
     * @return The lookup cache.
     */
    public Multimap<Item, RecipeHolder<T>> getCachedValues() {
        return this.lookupCache;
    }

    /**
     * Looks up an item in the cache.
     *
     * @param stack   The item to lookup.
     * @param context The context of the lookup.
     * @param level   The current game level.
     * @return The recipe that best matches the provided information.
     */
    @Nullable
    public RecipeHolder<T> lookup(ItemStack stack, BotanyPotContext context, Level level) {
        if (stack.isEmpty()) {
            return null;
        }
        final Collection<RecipeHolder<T>> cachedRecipes = lookupCache.get(stack.getItem());
        for (RecipeHolder<T> cached : lookupCache.get(stack.getItem())) {
            if (cached.value().couldMatch(stack, context, level)) {
                return cached;
            }
        }
        for (RecipeHolder<T> holder : this.uncached) {
            if (holder.value().couldMatch(stack, context, level)) {
                return holder;
            }
        }
        return null;
    }

    private void buildCache(Level level) {
        long startTime = System.nanoTime();
        final Map<ResourceLocation, RecipeHolder<T>> recipes = recipeCache.apply(level);
        if (recipes == null) {
            BotanyPotsMod.LOG.error("Could not build {} cache. Entries do not exist?", this.recipeType.get());
            return;
        }
        uncached.clear();
        uncached.addAll(recipes.values());
        if (!recipes.isEmpty()) {
            for (Item item : BuiltInRegistries.ITEM) {
                final ItemStack defaultStack = item.getDefaultInstance();
                for (RecipeHolder<T> recipe : recipes.values()) {
                    if (recipe.value() instanceof CacheableRecipe cacheable && cacheable.canBeCached() && cacheable.isCacheKey(defaultStack)) {
                        this.lookupCache.put(item, recipe);
                        uncached.remove(recipe);
                    }
                }
            }
        }
        long endTime = System.nanoTime();
        BotanyPotsMod.LOG.info("Built {} {} cache in {}ms. {} / {} entries cached.", level.isClientSide ? "Client" : "Server", this.recipeType.get(), (endTime - startTime) / 1_000_000f, recipes.size() - uncached.size(), recipes.size());
    }

    /**
     * Creates a new cache for a given recipe type.
     *
     * @param type The type of recipe to build a cache for.
     * @param <T>  The type of the recipe.
     * @return The created recipe cache.
     */
    public static <T extends BotanyPotRecipe> SidedReloadableCache<RecipeCache<T>> of(Supplier<RecipeType<T>> type) {
        return SidedReloadableCache.of(level -> {
            final RecipeCache<T> cache = new RecipeCache<>(type);
            cache.buildCache(level);
            return cache;
        });
    }
}