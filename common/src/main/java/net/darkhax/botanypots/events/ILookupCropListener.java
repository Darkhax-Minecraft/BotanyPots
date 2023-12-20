package net.darkhax.botanypots.events;

import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

@FunctionalInterface
public interface ILookupCropListener {

    /**
     * This event listener is invoked when the game tries to lookup a Crop.
     *
     * @param level The level the pot is in.
     * @param pos   The position of the pot.
     * @param pot   The pot being used.
     * @param stack The item being used.
     * @param found The entry that would normally be used. A null value indicates that the entry could not normally be
     *              found.
     * @return The new result for the lookup.
     */
    @Nullable
    RecipeHolder<Crop> lookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack, @Nullable RecipeHolder<Crop> found);
}