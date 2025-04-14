package net.darkhax.botanypots.common.api.data.itemdrops;

import net.darkhax.bookshelf.common.api.PhysicalSide;
import net.darkhax.bookshelf.common.api.annotation.OnlyFor;
import net.darkhax.botanypots.common.api.data.context.BotanyPotContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a potential outcome from a botany pot, such as the items produced when a crop is harvested.
 */
public interface ItemDropProvider {

    /**
     * Performs the outcome of the drop provider.
     *
     * @param context The context of the action.
     * @param level   The current game level.
     * @param drops   A consumer that adds produced items to the output.
     */
    void apply(BotanyPotContext context, Level level, Consumer<ItemStack> drops);

    /**
     * Gets the type of the provider.
     *
     * @return The type of the provider.
     */
    ItemDropProviderType<?> getType();

    /**
     * Provides a list of items that can be produced by the provider. These are used to display potential outcomes in
     * recipe viewers like JEI.
     *
     * @return A list of items to display. Can be empty, can not be null.
     */
    @OnlyFor(PhysicalSide.CLIENT)
    List<ItemStack> getDisplayItems();
}