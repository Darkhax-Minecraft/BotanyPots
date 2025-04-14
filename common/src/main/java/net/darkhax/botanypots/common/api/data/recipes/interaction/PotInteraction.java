package net.darkhax.botanypots.common.api.data.recipes.interaction;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.function.SidedReloadableCache;
import net.darkhax.botanypots.common.api.data.context.BotanyPotContext;
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
 * Represents a special type of interaction that can happen when the player interacts with a botany pot.
 */
public abstract class PotInteraction extends BotanyPotRecipe {

    public static final Supplier<RecipeType<PotInteraction>> TYPE = CachedSupplier.of(BuiltInRegistries.RECIPE_TYPE, BotanyPotsMod.id("pot_interaction")).cast();
    public static final SidedReloadableCache<Map<ResourceLocation, RecipeHolder<PotInteraction>>> RECIPES = SidedReloadableCache.recipes(TYPE);
    public static final SidedReloadableCache<RecipeCache<PotInteraction>> CACHE = RecipeCache.of(TYPE);

    /**
     * Applies the interaction effect to the given context.
     *
     * @param context The current context.
     */
    public abstract void apply(@NotNull BotanyPotContext context);

    @NotNull
    @Override
    public RecipeType<?> getType() {
        return TYPE.get();
    }

    @Nullable
    public static RecipeHolder<PotInteraction> getInteraction(Level level, BotanyPotContext context, ItemStack stack) {
        final RecipeCache<PotInteraction> cache = CACHE.apply(level);
        return cache != null ? cache.lookup(stack, context, level) : null;
    }
}