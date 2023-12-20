package net.darkhax.botanypots.events;

import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

@Cancelable
public class LookupCropEvent extends BotanyPotEvent {

    final ItemStack stack;

    @Nullable
    private final RecipeHolder<Crop> original;

    @Nullable
    private RecipeHolder<Crop> result;

    public LookupCropEvent(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack, RecipeHolder<Crop> original) {

        super(level, pos, pot);
        this.stack = stack;
        this.original = original;
        this.result = original;
    }

    public ItemStack getStack() {

        return this.stack;
    }

    @Nullable
    public RecipeHolder<Crop> getOriginal() {

        return this.original;
    }

    @Nullable
    public RecipeHolder<Crop> getLookupResult() {

        return this.result;
    }

    public void setLookupResult(@Nullable RecipeHolder<Crop> newResult) {

        this.result = newResult;
    }
}