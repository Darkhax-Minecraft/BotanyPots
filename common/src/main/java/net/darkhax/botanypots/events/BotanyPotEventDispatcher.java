package net.darkhax.botanypots.events;

import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.data.recipes.fertilizer.Fertilizer;
import net.darkhax.botanypots.data.recipes.potinteraction.PotInteraction;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public interface BotanyPotEventDispatcher {

    @Nullable
    RecipeHolder<Soil> postSoilLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack, @Nullable RecipeHolder<Soil> found);

    void listenSoilLookup(ILookupSoilListener listener);

    @Nullable
    RecipeHolder<Crop> postCropLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack, @Nullable RecipeHolder<Crop> found);

    void listenCropLookup(ILookupCropListener listener);

    @Nullable
    RecipeHolder<PotInteraction> postInteractionLookup(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot, RecipeHolder<PotInteraction> found);

    void listenInteractionLookup(ILookupInteractionListener listener);

    @Nullable
    RecipeHolder<Fertilizer> postFertilizerLookup(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot, RecipeHolder<Fertilizer> found);

    void listenFertilizerLookup(ILookupFertilizerListener listener);

    List<ItemStack> postCropDrops(Random rng, Level level, BlockPos pos, BlockEntityBotanyPot pot, Crop crop, List<ItemStack> originalDrops);

    void listenCropDrops(ICropDropListener listener);
}