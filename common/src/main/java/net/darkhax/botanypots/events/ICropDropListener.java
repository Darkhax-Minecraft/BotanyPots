package net.darkhax.botanypots.events;

import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Random;

@FunctionalInterface
public interface ICropDropListener {

    /**
     * This listener is invoked when drops for a crop are generated.
     *
     * @param rng           A random number generator that has been seeded with the pots random seed. There are certain
     *                      scenarios where being able to reproduce the same loot roll is required, to ensure this
     *                      happens event listeners should always pull randomness from this provided rng source.
     * @param level         The level the pot is in.
     * @param pos           The position of the pot.
     * @param pot           The pot growing the crop.
     * @param crop          The crop being harvested.
     * @param originalDrops The original drops for the crop.
     * @param drops         The new items to drop.
     */
    void generateDrop(Random rng, Level level, BlockPos pos, BlockEntityBotanyPot pot, Crop crop, List<ItemStack> originalDrops, List<ItemStack> drops);
}