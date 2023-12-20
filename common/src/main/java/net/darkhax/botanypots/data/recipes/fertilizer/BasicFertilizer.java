package net.darkhax.botanypots.data.recipes.fertilizer;

import com.google.gson.JsonParseException;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

public class BasicFertilizer extends Fertilizer {

    protected Ingredient ingredient;
    protected Optional<Ingredient> cropIngredient;
    protected Optional<Ingredient> soilIngredient;
    protected int minTicks;
    protected int maxTicks;

    public BasicFertilizer(Ingredient ingredient, Optional<Ingredient> cropIngredient, Optional<Ingredient> soilIngredient, int minTicks, int maxTicks) {

        if (minTicks < 0 || maxTicks < 0) {

            throw new JsonParseException("Growth ticks must be greater than 0! min=" + minTicks + " max=" + maxTicks);
        }

        if (minTicks > maxTicks) {

            throw new JsonParseException("Min growth ticks must not be greater than max ticks.  min=" + minTicks + " max=" + maxTicks);
        }

        this.ingredient = ingredient;
        this.cropIngredient = cropIngredient;
        this.soilIngredient = soilIngredient;
        this.minTicks = minTicks;
        this.maxTicks = maxTicks;
    }

    @Override
    public boolean canApply(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot) {

        return ingredient.test(heldStack) && (!this.cropIngredient.isPresent() || this.cropIngredient.get().test(pot.getInventory().getCropStack())) && (!this.soilIngredient.isPresent() || this.soilIngredient.get().test(pot.getInventory().getSoilStack()));
    }

    @Override
    public void apply(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot) {

        if (!world.isClientSide) {

            pot.addGrowth(world.random.nextIntBetweenInclusive(this.minTicks, this.maxTicks));

            // Bonemeal particles
            world.levelEvent(1505, pos, 0);

            if (!player.isCreative()) {

                heldStack.shrink(1);
            }
        }
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return BotanyPotHelper.BASIC_FERTILIZER_SERIALIZER.get();
    }

    public Ingredient getIngredient() {

        return ingredient;
    }

    @Nullable
    public Optional<Ingredient> getCropIngredient() {

        return cropIngredient;
    }

    @Nullable
    public Optional<Ingredient> getSoilIngredient() {

        return soilIngredient;
    }

    public int getMinTicks() {

        return minTicks;
    }

    public int getMaxTicks() {

        return maxTicks;
    }
}