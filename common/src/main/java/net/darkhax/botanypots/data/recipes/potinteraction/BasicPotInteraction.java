package net.darkhax.botanypots.data.recipes.potinteraction;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.data.sound.Sound;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.block.inv.BotanyPotContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;

public class BasicPotInteraction extends PotInteraction {

    protected final Ingredient heldTest;

    protected final boolean damageHeld;

    @Nullable
    protected final Ingredient soilTest;

    @Nullable
    protected final Ingredient seedTest;

    @Nullable
    protected final ItemStack newSoilStack;

    @Nullable
    protected final ItemStack newSeedStack;

    @Nullable
    protected final Sound sound;

    protected final List<ItemStack> extraDrops;

    public BasicPotInteraction(ResourceLocation id, Ingredient heldTest, boolean damageHeld, @Nullable Ingredient soilTest, @Nullable Ingredient seedTest, @Nullable ItemStack newSoilStack, @Nullable ItemStack newSeedStack, @Nullable Sound sound, List<ItemStack> extraDrops) {

        super(id);

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

        return this.heldTest.test(heldStack) && (this.soilTest == null || this.soilTest.test(pot.getInventory().getSoilStack())) && (this.seedTest == null || this.seedTest.test(pot.getInventory().getCropStack()));
    }

    @Override
    public void apply(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, ItemStack heldStack, BlockEntityBotanyPot pot) {

        if (!world.isClientSide) {

            if (this.newSoilStack != null) {

                // Drop soil crafting remainder if applicable.
                if (Services.INVENTORY_HELPER.hasCraftingRemainder(pot.getInventory().getSoilStack())) {

                    final ItemStack dropStack = Services.INVENTORY_HELPER.getCraftingRemainder(pot.getInventory().getSoilStack());

                    if (dropStack != null) {

                        Block.popResource(world, pos, dropStack.copy());
                    }
                }

                // Set the soil slot.
                pot.getInventory().setItem(BotanyPotContainer.SOIL_SLOT, this.newSoilStack.copy());
            }

            if (this.newSeedStack != null) {

                // Drop seed crafting remainder if applicable.
                if (Services.INVENTORY_HELPER.hasCraftingRemainder(pot.getInventory().getCropStack())) {

                    final ItemStack dropStack = Services.INVENTORY_HELPER.getCraftingRemainder(pot.getInventory().getCropStack());

                    if (dropStack != null) {

                        Block.popResource(world, pos, dropStack.copy());
                    }
                }

                // Set the seed slot.
                pot.getInventory().setItem(BotanyPotContainer.CROP_SLOT, this.newSeedStack.copy());
            }

            // If the stack can be damaged try to damage it instead of destroying it directly.
            if (this.damageHeld) {

                if (heldStack.isDamageableItem()) {

                    Services.INVENTORY_HELPER.damageStack(heldStack, 1, player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                }
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
        if (this.sound != null) {

            this.sound.playSoundAt(world, player, pos);
        }
    }

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

    @Nullable
    public Ingredient getSoilTest() {
        return soilTest;
    }

    @Nullable
    public Ingredient getSeedTest() {
        return seedTest;
    }

    @Nullable
    public ItemStack getNewSoilStack() {
        return newSoilStack;
    }

    @Nullable
    public ItemStack getNewSeedStack() {
        return newSeedStack;
    }

    @Nullable
    public Sound getSound() {
        return sound;
    }

    public List<ItemStack> getExtraDrops() {
        return extraDrops;
    }
}
