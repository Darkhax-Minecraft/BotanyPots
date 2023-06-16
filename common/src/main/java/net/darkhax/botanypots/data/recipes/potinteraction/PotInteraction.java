package net.darkhax.botanypots.data.recipes.potinteraction;

import net.darkhax.bookshelf.api.data.recipes.RecipeBaseData;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PotInteraction extends RecipeBaseData<Container> {

    public PotInteraction(ResourceLocation id) {

        super(id);
    }

    public abstract boolean canApply(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot);

    public abstract void apply(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot);

    @Override
    public RecipeType<?> getType() {

        return BotanyPotHelper.POT_INTERACTION_TYPE.get();
    }
}