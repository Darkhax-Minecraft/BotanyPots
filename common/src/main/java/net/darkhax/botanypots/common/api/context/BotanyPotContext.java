package net.darkhax.botanypots.common.api.context;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.Nullable;

/**
 * Provides context for recipes related to botany pots.
 */
public interface BotanyPotContext extends RecipeInput {

    /**
     * Gets the item in the soil slot.
     *
     * @return The item in the soil slot.
     */
    ItemStack getSoilItem();

    /**
     * Gets the item in the seed slot.
     *
     * @return The item in the seed slot.
     */
    ItemStack getSeedItem();

    /**
     * Gets the item in the harvest tool slot. This is not the same as the players held item.
     *
     * @return The item in the harvest tool slot.
     */
    ItemStack getHarvestItem();

    /**
     * Creates loot table parameters for generating loot from a loot table. Loot tables only exist on the server thread
     * and parameters can not be constructed on a client thread.
     *
     * @param state The state for the block_state parameter. Sometimes it is necessary to use a different block to get
     *              te right loot table results. For example, the wheat loot table checks the block id and age.
     * @return Loot parameters based on the current context.
     */
    LootParams createLootParams(@Nullable BlockState state);

    /**
     * Runs an MCFunction file based on the provided ID. Functions can only be executed on the server thread, trying to
     * do this on a client thread will cause issues.
     *
     * @param functionId The ID of the MCFunction to run.
     */
    void runFunction(ResourceLocation functionId);

    /**
     * Gets the relevant player if one is available.
     *
     * @return The relevant player if one is available.
     */
    @Nullable
    Player getPlayer();

    /**
     * Gets the item used by the player if one was used.
     *
     * @return The item the player used to interact.
     */
    ItemStack getInteractionItem();

    /**
     * Determines the total amount of growth ticks required for the crop to fully mature based on the current
     * conditions.
     *
     * @return The total amount of growth ticks required to grow the crop.
     */
    int getRequiredGrowthTicks();

    /**
     * Determines if the action is happening on the server thread. It does not matter if the server is integrated or
     * dedicated.
     *
     * @return If the action is happening on a server thread.
     */
    boolean isServerThread();
}
