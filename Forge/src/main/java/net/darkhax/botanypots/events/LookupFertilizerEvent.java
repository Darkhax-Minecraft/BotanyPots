package net.darkhax.botanypots.events;

import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.fertilizer.Fertilizer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

@Cancelable
public class LookupFertilizerEvent extends BotanyPotEvent {

    private final BlockState state;

    private final Player player;

    private final InteractionHand hand;

    private final ItemStack stack;

    @Nullable
    private final Fertilizer original;

    @Nullable
    private Fertilizer result;

    public LookupFertilizerEvent(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockEntityBotanyPot pot, Fertilizer original) {

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
    public Fertilizer getOriginal() {

        return this.original;
    }

    @Nullable
    public Fertilizer getLookupResult() {

        return this.result;
    }

    public void setLookupResult(@Nullable Fertilizer newResult) {

        this.result = newResult;
    }
}