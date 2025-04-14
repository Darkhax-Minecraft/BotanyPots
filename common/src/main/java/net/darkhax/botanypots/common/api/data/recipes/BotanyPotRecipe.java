package net.darkhax.botanypots.common.api.data.recipes;

import com.mojang.serialization.Codec;
import net.darkhax.botanypots.common.api.data.context.BotanyPotContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The base class for all recipes that use a botany pot.
 */
public abstract class BotanyPotRecipe implements Recipe<BotanyPotContext> {

    /**
     * Tests if the recipe is valid for the current context.
     *
     * @param candidate The item to test.
     * @param context   The current context.
     * @param level     The current game level.
     * @return If the recipe is valid.
     */
    public abstract boolean couldMatch(ItemStack candidate, BotanyPotContext context, Level level);

    @NotNull
    @Override
    public ItemStack assemble(@NotNull BotanyPotContext input, @NotNull HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack getResultItem(HolderLookup.@NotNull Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return false;
    }

    /**
     * Populates a tooltip when the item is hovered in a botany pot.
     *
     * @param stack        The ItemStack being hovered.
     * @param context      The context of the current situation.
     * @param level        The current game level.
     * @param tooltipLines The list of tooltips being displayed.
     */
    public void hoverTooltip(ItemStack stack, BotanyPotContext context, Level level, Consumer<Component> tooltipLines) {
        // No-Op
    }

    /**
     * Creates a codec for a recipe of a specific type.
     *
     * @param recipeType The type of recipe to serialize.
     * @param <T>        The type of the recipe.
     * @return A codec that serializes recipes of a given type.
     */
    public static <T extends Recipe<?>> Codec<T> recipeCodec(Supplier<RecipeType<T>> recipeType) {
        return Codec.lazyInitialized(() -> Recipe.CODEC.xmap(recipe -> (T) recipe, Function.identity()));
    }

    /**
     * Creates a stream codec for a recipe of a specific type.
     *
     * @param recipeType The type of recipe to serialize.
     * @param <T>        A stream codec that serializes recipes of a given type.
     * @return The type of the recipe.
     */
    public static <T extends Recipe<?>> StreamCodec<RegistryFriendlyByteBuf, T> recipeStream(Supplier<RecipeType<T>> recipeType) {
        return StreamCodec.of(Recipe.STREAM_CODEC::encode, buf -> (T) Recipe.STREAM_CODEC.decode(buf));
    }
}