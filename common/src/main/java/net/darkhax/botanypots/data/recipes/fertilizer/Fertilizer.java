package net.darkhax.botanypots.data.recipes.fertilizer;

import net.darkhax.bookshelf.api.data.recipes.RecipeBaseData;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class Fertilizer extends RecipeBaseData<Container> {

    public abstract boolean canApply(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot);

    public abstract void apply(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot);

    @Override
    public RecipeType<?> getType() {

        return BotanyPotHelper.FERTILIZER_TYPE.get();
    }
}
