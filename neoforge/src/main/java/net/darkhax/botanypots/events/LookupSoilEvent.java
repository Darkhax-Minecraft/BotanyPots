package net.darkhax.botanypots.events;

import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class LookupSoilEvent extends BotanyPotEvent {

    final ItemStack stack;

    @Nullable
    private final RecipeHolder<Soil> original;

    @Nullable
    private RecipeHolder<Soil> result;

    public LookupSoilEvent(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack, RecipeHolder<Soil> original) {

        super(level, pos, pot);
        this.stack = stack;
        this.original = original;
        this.result = original;
    }

    public ItemStack getStack() {

        return this.stack;
    }

    @Nullable
    public RecipeHolder<Soil> getOriginal() {

        return this.original;
    }

    @Nullable
    public RecipeHolder<Soil> getLookupResult() {

        return this.result;
    }

    public void setLookupResult(@Nullable RecipeHolder<Soil> newResult) {

        this.result = newResult;
    }
}