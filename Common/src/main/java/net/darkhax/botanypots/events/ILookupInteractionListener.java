package net.darkhax.botanypots.events;

import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.potinteraction.PotInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

@FunctionalInterface
public interface ILookupInteractionListener {

    /**
     * This event listener is invoked when the game tries to lookup a pot interaction.
     *
     * @param state     The state of the pot.
     * @param level     The level the pot is in.
     * @param pos       The position of the pot.
     * @param player    The player performing the interaction.
     * @param hand      The hand the player is using.
     * @param heldStack The item the player is using.
     * @param pot       The pot being interacted with.
     * @param found     The entry that would normally be used. A null value indicates that the entry could not normally
     *                  be found.
     * @return The new result for the lookup.
     */
    @Nullable
    PotInteraction lookup(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot, PotInteraction found);
}
