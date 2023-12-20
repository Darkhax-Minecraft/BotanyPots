package net.darkhax.botanypots.data.recipes.potinteraction;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.data.sound.Sound;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.block.inv.BotanyPotContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class BasicPotInteraction extends PotInteraction {

    protected final Ingredient heldTest;
    protected final boolean damageHeld;
    protected final Optional<Ingredient> soilTest;
    protected final Optional<Ingredient> seedTest;
    protected final Optional<ItemStack> newSoilStack;
    protected final Optional<ItemStack> newSeedStack;
    protected final Optional<Sound> sound;
    protected final List<ItemStack> extraDrops;

    public BasicPotInteraction(Ingredient heldTest, boolean damageHeld, Optional<Ingredient> soilTest, Optional<Ingredient> seedTest, Optional<ItemStack> newSoilStack, Optional<ItemStack> newSeedStack, Optional<Sound> sound, List<ItemStack> extraDrops) {

        this.heldTest = heldTest;
        this.damageHeld = damageHeld;
        this.soilTest = soilTest;
        this.seedTest = seedTest;
        this.newSoilStack = newSoilStack;
        this.newSeedStack = newSeedStack;
        this.sound = sound;
        this.extraDrops = extraDrops;
    }

    @Override
    public boolean canApply(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot) {

        return this.heldTest.test(heldStack) && (this.soilTest.isEmpty() || this.soilTest.get().test(pot.getInventory().getSoilStack())) && (this.seedTest.isEmpty() || this.seedTest.get().test(pot.getInventory().getCropStack()));
    }

    @Override
    public void apply(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot) {

        if (!world.isClientSide) {

            this.newSoilStack.ifPresent(soilStack -> {

                // Drop soil crafting remainder if applicable.
                if (Services.INVENTORY_HELPER.hasCraftingRemainder(pot.getInventory().getSoilStack())) {

                    final ItemStack dropStack = Services.INVENTORY_HELPER.getCraftingRemainder(pot.getInventory().getSoilStack());

                    if (dropStack != null) {

                        Block.popResource(world, pos, dropStack.copy());
                    }
                }

                // Set the soil slot.
                pot.getInventory().setItem(BotanyPotContainer.SOIL_SLOT, soilStack.copy());
            });

            this.newSeedStack.ifPresent(seedStack -> {

                // Drop seed crafting remainder if applicable.
                if (Services.INVENTORY_HELPER.hasCraftingRemainder(pot.getInventory().getCropStack())) {

                    final ItemStack dropStack = Services.INVENTORY_HELPER.getCraftingRemainder(pot.getInventory().getCropStack());

                    if (dropStack != null) {

                        Block.popResource(world, pos, dropStack.copy());
                    }
                }

                // Set the seed slot.
                pot.getInventory().setItem(BotanyPotContainer.CROP_SLOT, seedStack.copy());
            });

            // If the stack can be damaged try to damage it instead of destroying it directly.
            if (this.damageHeld && heldStack.getMaxDamage() > 0) {

                Services.INVENTORY_HELPER.damageStack(heldStack, 1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }

            else {

                final ItemStack remainder = Services.INVENTORY_HELPER.getCraftingRemainder(heldStack);

                if (!remainder.isEmpty()) {

                    Block.popResource(world, pos, remainder.copy());
                }

                heldStack.shrink(1);
            }

            // Drop extra items when specified.
            if (this.extraDrops != null && !this.extraDrops.isEmpty()) {

                for (ItemStack drop : this.extraDrops) {

                    Block.popResource(world, pos, drop.copy());
                }
            }
        }

        // Play a sound if one is specified.
        this.sound.ifPresent(harvestSound -> harvestSound.playSoundAt(world, player, pos));
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {

        return BotanyPotHelper.SIMPLE_POT_INTERACTION_SERIALIZER.get();
    }

    public Ingredient getHeldTest() {
        return heldTest;
    }

    public boolean isDamageHeld() {
        return damageHeld;
    }

    public Optional<Ingredient> getSoilTest() {
        return soilTest;
    }

    public Optional<Ingredient> getSeedTest() {
        return seedTest;
    }

    public Optional<ItemStack> getNewSoilStack() {
        return newSoilStack;
    }

    public Optional<ItemStack> getNewSeedStack() {
        return newSeedStack;
    }

    public Optional<Sound> getSound() {
        return sound;
    }

    public List<ItemStack> getExtraDrops() {
        return extraDrops;
    }
}
