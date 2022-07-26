package net.darkhax.botanypots.block.inv;

import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BotanyPotMenu extends AbstractContainerMenu {

    private static final CachedSupplier<MenuType<?>> MENU_TYPE = CachedSupplier.cache(() -> Registry.MENU.get(new ResourceLocation(Constants.MOD_ID, "pot_menu")));
    private final BotanyPotContainer potInv;
    private final Inventory playerInv;

    public static BotanyPotMenu fromNetwork(int windowId, Inventory inv, FriendlyByteBuf buf) {

        final BlockPos tilePos = Serializers.BLOCK_POS.fromByteBuf(buf);

        if (inv.player.level.getBlockEntity(tilePos) instanceof BlockEntityBotanyPot pot) {

            return new BotanyPotMenu(windowId, pot.getInventory(), inv);
        }

        Constants.LOG.error("Attempted to open botany pot at invalid position {}.", tilePos);
        throw new IllegalStateException("Attempted to open botany pot at invalid position " + tilePos);
    }

    public BotanyPotMenu(int id, BotanyPotContainer potInv, Inventory playerInv) {

        super(MENU_TYPE.get(), id);
        this.potInv = potInv;
        this.playerInv = playerInv;

        final int slotXOffset = this.isHopper() ? 44 : 80;

        // Add the pot's Soil and Crop slot.
        this.addSlot(new SlotSoil(potInv, BotanyPotContainer.SOIL_SLOT, slotXOffset, 48, this.potInv.getPotEntity()::isValidSoil));
        this.addSlot(new SlotCropSeed(potInv, BotanyPotContainer.CROP_SLOT, slotXOffset, 22, this.potInv.getPotEntity()::isValidSeed));

        // Add the hopper pot's 4x3 output slots.
        if (this.isHopper()) {

            for (int potOutputY = 0; potOutputY < 3; potOutputY++) {

                for (int potOutputX = 0; potOutputX < 4; potOutputX++) {

                    final int slotId = potOutputX + potOutputY * 4 + 2;
                    final int slotX = 80 + potOutputX * 18;
                    final int slotY = 17 + potOutputY * 18;

                    this.addSlot(new SlotPotOutput(potInv, slotId, slotX, slotY));
                }
            }
        }

        // Add the player's 3 rows of inventory
        for (int playerInvY = 0; playerInvY < 3; playerInvY++) {

            for (int playerInvX = 0; playerInvX < 9; playerInvX++) {

                this.addSlot(new Slot(playerInv, playerInvX + playerInvY * 9 + 9, 8 + playerInvX * 18, 84 + playerInvY * 18));
            }
        }

        // Add the player's 9 hotbar slots.
        for (int hotbarX = 0; hotbarX < 9; hotbarX++) {

            this.addSlot(new Slot(playerInv, hotbarX, 8 + hotbarX * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotId) {

        final BlockEntityBotanyPot pot = this.potInv.getPotEntity();
        final Level level = pot.getLevel();
        final BlockPos pos = pot.getBlockPos();
        final Slot slot = this.slots.get(slotId);

        final int firstSlot = this.isHopper() ? 14 : 2;
        final int lastSlot = this.isHopper() ? 50 : 38;

        ItemStack unmovedItems = ItemStack.EMPTY;

        if (slot.hasItem()) {

            ItemStack slotStack = slot.getItem();
            unmovedItems = slotStack.copy();

            // Output Slots
            if (this.isHopper() && slotId >= 2 && slotId <= 13) {

                // Attempt moving to player inventory.
                if (!this.moveItemStackTo(slotStack, firstSlot, lastSlot, true)) {

                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(slotStack, unmovedItems);
            }

            // Soil and Seed slots
            else if (slotId == 0 || slotId == 1) {

                // Attempt moving to player inventory.
                if (!this.moveItemStackTo(slotStack, firstSlot, lastSlot, true)) {

                    return ItemStack.EMPTY;
                }
            }

            else if (slotId >= firstSlot && slotId <= lastSlot) {

                final Slot soilSlot = this.slots.get(0);

                // Try to insert a soil
                if (!soilSlot.hasItem() && BotanyPotHelper.findSoil(level, pos, pot, slotStack) != null) {

                    soilSlot.set(slotStack.split(1));
                    slot.set(slotStack);

                    if (slotStack.isEmpty()) {

                        return ItemStack.EMPTY;
                    }
                }

                final Slot cropSlot = this.slots.get(1);

                if (!cropSlot.hasItem() && BotanyPotHelper.findCrop(level, pos, pot, slotStack) != null) {

                    cropSlot.set(slotStack.split(1));
                    slot.set(slotStack);

                    if (slotStack.isEmpty()) {

                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {

                slot.set(ItemStack.EMPTY);
            }

            else {

                slot.setChanged();
            }

            if (slotStack.getCount() == unmovedItems.getCount()) {

                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return unmovedItems;
    }

    public boolean isHopper() {

        return this.potInv.getPotEntity().isHopper();
    }

    @Override
    public boolean stillValid(Player player) {

        return this.potInv.stillValid(player);
    }

    public BotanyPotContainer getPotInventory() {

        return this.potInv;
    }
}
