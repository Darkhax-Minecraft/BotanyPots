package net.darkhax.botanypots.common.api.data.recipes;

import net.minecraft.world.item.ItemStack;

/**
 * Cacheable recipes are used to improve lookup times for simple recipes that only consider the input Item. Cacheable
 * recipes should NOT care about additional context, like the world, player, pot, or components on the ItemStack.
 */
public interface CacheableRecipe {

    /**
     * Determines if recipe can be cached based on the deserialized contents of the recipe. For example, imagine a
     * recipe with an optional list of non-cacheable conditions. If this list is not present or empty, the recipe could
     * be cached when it otherwise could not.
     *
     * @return If the recipe can be cached.
     */
    boolean canBeCached();

    /**
     * Determines if a given item would be a valid cache key for this recipe. Recipes can have more than one valid key.
     * <p>
     * Implementations should only check intrinsic properties of the stack like the item id, tags, and the type of the
     * item. Properties like data components should not be checked as these are not intrinsic to the item. An ItemStack
     * is only provided in this context because it is needed to test Ingredient.
     *
     * @param stack The item to test.
     * @return If the item is a valid cache key for this recipe.
     */
    boolean isCacheKey(ItemStack stack);
}