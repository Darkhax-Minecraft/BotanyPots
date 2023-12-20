package net.darkhax.botanypots.events;

import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.data.recipes.fertilizer.Fertilizer;
import net.darkhax.botanypots.data.recipes.potinteraction.PotInteraction;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class BotanyPotEventDispatcherFabric implements BotanyPotEventDispatcher {

    public static final Event<ILookupCropListener> LOOKUP_CROP = EventFactory.createArrayBacked(ILookupCropListener.class, callbacks -> (level, pos, pot, stack, found) -> {

        RecipeHolder<Crop> result = found;

        for (ILookupCropListener listener : callbacks) {

            result = listener.lookup(level, pos, pot, stack, found);
        }

        return result;
    });

    public static final Event<ILookupSoilListener> LOOKUP_SOIL = EventFactory.createArrayBacked(ILookupSoilListener.class, callbacks -> (level, pos, pot, stack, found) -> {

        RecipeHolder<Soil> result = found;

        for (ILookupSoilListener listener : callbacks) {

            result = listener.lookup(level, pos, pot, stack, found);
        }

        return result;
    });

    public static final Event<ILookupInteractionListener> LOOKUP_INTERACTION = EventFactory.createArrayBacked(ILookupInteractionListener.class, callbacks -> (state, level, pos, player, hand, stack, pot, found) -> {

        RecipeHolder<PotInteraction> result = found;

        for (ILookupInteractionListener listener : callbacks) {

            result = listener.lookup(state, level, pos, player, hand, stack, pot, found);
        }

        return result;
    });

    public static final Event<ILookupFertilizerListener> LOOKUP_FERTILIZER = EventFactory.createArrayBacked(ILookupFertilizerListener.class, callbacks -> (state, level, pos, player, hand, stack, pot, found) -> {

        RecipeHolder<Fertilizer> result = found;

        for (ILookupFertilizerListener listener : callbacks) {

            result = listener.lookup(state, level, pos, player, hand, stack, pot, found);
        }

        return result;
    });

    public static final Event<ICropDropListener> CROP_DROPS = EventFactory.createArrayBacked(ICropDropListener.class, callbacks -> ((rng, level, pos, pot, crop, originalDrops, drops) -> {

        for (ICropDropListener listener : callbacks) {

            listener.generateDrop(rng, level, pos, pot, crop, originalDrops, drops);
        }
    }));

    @Nullable
    @Override
    public RecipeHolder<Soil> postSoilLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack, @Nullable RecipeHolder<Soil> found) {

        return LOOKUP_SOIL.invoker().lookup(level, pos, pot, stack, found);
    }

    @Override
    public void listenSoilLookup(ILookupSoilListener listener) {

        LOOKUP_SOIL.register(listener);
    }

    @Nullable
    @Override
    public RecipeHolder<Crop> postCropLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack, @Nullable RecipeHolder<Crop> found) {

        return LOOKUP_CROP.invoker().lookup(level, pos, pot, stack, found);
    }

    @Override
    public void listenCropLookup(ILookupCropListener listener) {

        LOOKUP_CROP.register(listener);
    }

    @Nullable
    @Override
    public RecipeHolder<PotInteraction> postInteractionLookup(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot, RecipeHolder<PotInteraction> found) {

        return LOOKUP_INTERACTION.invoker().lookup(state, level, pos, player, hand, heldStack, pot, found);
    }

    @Override
    public void listenInteractionLookup(ILookupInteractionListener listener) {

        LOOKUP_INTERACTION.register(listener);
    }

    @Nullable
    @Override
    public RecipeHolder<Fertilizer> postFertilizerLookup(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot, RecipeHolder<Fertilizer> found) {

        return LOOKUP_FERTILIZER.invoker().lookup(state, level, pos, player, hand, heldStack, pot, found);
    }

    @Override
    public void listenFertilizerLookup(ILookupFertilizerListener listener) {

        LOOKUP_FERTILIZER.register(listener);
    }

    @Override
    public List<ItemStack> postCropDrops(Random rng, Level level, BlockPos pos, BlockEntityBotanyPot pot, Crop crop, List<ItemStack> originalDrops) {

        final List<ItemStack> drops = NonNullList.create();
        drops.addAll(originalDrops);
        CROP_DROPS.invoker().generateDrop(rng, level, pos, pot, crop, originalDrops, drops);
        return drops;
    }

    @Override
    public void listenCropDrops(ICropDropListener listener) {

        CROP_DROPS.register(listener);
    }
}
