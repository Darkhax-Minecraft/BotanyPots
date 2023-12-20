package net.darkhax.botanypots.events;

import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.potinteraction.PotInteraction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class LookupInteractionEvent extends BotanyPotEvent {

    private final BlockState state;

    private final Player player;

    private final InteractionHand hand;

    private final ItemStack stack;

    @Nullable
    private final RecipeHolder<PotInteraction> original;

    @Nullable
    private RecipeHolder<PotInteraction> result;

    public LookupInteractionEvent(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockEntityBotanyPot pot, RecipeHolder<PotInteraction> original) {

        super(level, pos, pot);
        this.state = state;
        this.player = player;
        this.hand = hand;
        this.stack = stack;
        this.original = original;
        this.result = original;
    }

    public BlockState getBlockState() {

        return this.state;
    }

    public Player getPlayer() {

        return this.player;
    }

    public InteractionHand getHand() {

        return this.hand;
    }

    public ItemStack getStack() {

        return this.stack;
    }

    @Nullable
    public RecipeHolder<PotInteraction> getOriginal() {

        return this.original;
    }

    @Nullable
    public RecipeHolder<PotInteraction> getLookupResult() {

        return this.result;
    }

    public void setLookupResult(@Nullable RecipeHolder<PotInteraction> newResult) {

        this.result = newResult;
    }
}