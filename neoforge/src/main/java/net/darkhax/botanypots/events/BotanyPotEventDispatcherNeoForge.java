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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BotanyPotEventDispatcherNeoForge implements BotanyPotEventDispatcher {

    @Nullable
    @Override
    public RecipeHolder<Soil> postSoilLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack, @Nullable RecipeHolder<Soil> found) {
        
        final LookupSoilEvent event = new LookupSoilEvent(level, pos, pot, stack, found);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled() ? null : event.getLookupResult();
    }

    @Override
    public void listenSoilLookup(ILookupSoilListener listener) {

        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LookupSoilEvent.class, (event) -> {

            event.setLookupResult(listener.lookup(event.getLevel(), event.getPos(), event.getPot(), event.getStack(), event.getOriginal()));
        });
    }

    @Nullable
    @Override
    public RecipeHolder<Crop> postCropLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack, @Nullable RecipeHolder<Crop> found) {

        final LookupCropEvent event = new LookupCropEvent(level, pos, pot, stack, found);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled() ? null : event.getLookupResult();
    }

    @Override
    public void listenCropLookup(ILookupCropListener listener) {

        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LookupCropEvent.class, (event) -> {

            event.setLookupResult(listener.lookup(event.getLevel(), event.getPos(), event.getPot(), event.getStack(), event.getOriginal()));
        });
    }

    @Nullable
    @Override
    public RecipeHolder<PotInteraction> postInteractionLookup(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot, RecipeHolder<PotInteraction> found) {

        final LookupInteractionEvent event = new LookupInteractionEvent(state, level, pos, player, hand, heldStack, pot, found);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled() ? null : event.getLookupResult();
    }

    @Override
    public void listenInteractionLookup(ILookupInteractionListener listener) {

        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LookupInteractionEvent.class, (event) -> {

            event.setLookupResult(listener.lookup(event.getBlockState(), event.getLevel(), event.getPos(), event.getPlayer(), event.getHand(), event.getStack(), event.getPot(), event.getOriginal()));
        });
    }

    @Nullable
    @Override
    public RecipeHolder<Fertilizer> postFertilizerLookup(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot, RecipeHolder<Fertilizer> found) {

        final LookupFertilizerEvent event = new LookupFertilizerEvent(state, level, pos, player, hand, heldStack, pot, found);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled() ? null : event.getLookupResult();
    }

    @Override
    public void listenFertilizerLookup(ILookupFertilizerListener listener) {

        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LookupFertilizerEvent.class, (event) -> {

            event.setLookupResult(listener.lookup(event.getBlockState(), event.getLevel(), event.getPos(), event.getPlayer(), event.getHand(), event.getStack(), event.getPot(), event.getOriginal()));
        });
    }

    @Override
    public List<ItemStack> postCropDrops(Random rng, Level level, BlockPos pos, BlockEntityBotanyPot pot, Crop crop, List<ItemStack> originalDrops) {

        final CropDropEvent event = new CropDropEvent(rng, level, pos, pot, crop, originalDrops);
        NeoForge.EVENT_BUS.post(event);
        return event.isCanceled() ? Collections.emptyList() : event.getDrops();
    }

    @Override
    public void listenCropDrops(ICropDropListener listener) {

        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, CropDropEvent.class, (event) -> {

            listener.generateDrop(event.getRandom(), event.getLevel(), event.getPos(), event.getPot(), event.getCrop(), event.getOriginalDrops(), event.getDrops());
        });
    }
}