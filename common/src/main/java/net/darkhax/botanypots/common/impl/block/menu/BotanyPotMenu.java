package net.darkhax.botanypots.common.impl.block.menu;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.menu.data.BlockPosData;
import net.darkhax.bookshelf.common.api.menu.slot.InputSlot;
import net.darkhax.bookshelf.common.api.menu.slot.OutputSlot;
import net.darkhax.botanypots.common.api.context.BlockEntityContext;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.block.entity.AbstractBotanyPotBlockEntity;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BotanyPotMenu extends AbstractContainerMenu {

    public static final ResourceLocation EMPTY_SLOT_SOIL = BotanyPotsMod.id("item/empty_slot_soil");
    public static final ResourceLocation EMPTY_SLOT_SEED = BotanyPotsMod.id("item/empty_slot_seed");
    public static final ResourceLocation EMPTY_SLOT_HOE = ResourceLocation.withDefaultNamespace("item/empty_slot_hoe");
    public static final CachedSupplier<MenuType<BotanyPotMenu>> BASIC_MENU = CachedSupplier.cache(() -> BuiltInRegistries.MENU.get(BotanyPotsMod.id("basic_pot_menu"))).cast();
    public static final CachedSupplier<MenuType<BotanyPotMenu>> HOPPER_MENU = CachedSupplier.cache(() -> BuiltInRegistries.MENU.get(BotanyPotsMod.id("hopper_pot_menu"))).cast();
    public static final TagKey<Item> HARVEST_ITEM = TagKey.create(Registries.ITEM, BotanyPotsMod.id("harvest_items"));

    public static BotanyPotMenu basicMenuClient(int containerId, Inventory playerInv) {
        return new BotanyPotMenu(BASIC_MENU.get(), containerId, playerInv, new SimpleContainer(2), false, new SimpleContainerData(3));
    }

    public static BotanyPotMenu hopperMenuClient(int containerId, Inventory playerInv) {
        return new BotanyPotMenu(HOPPER_MENU.get(), containerId, playerInv, new SimpleContainer(AbstractBotanyPotBlockEntity.SLOT_COUNT), true, new SimpleContainerData(3));
    }

    public static BotanyPotMenu potMenuServer(int containerId, Inventory playerInv, BotanyPotBlockEntity pot) {
        return new BotanyPotMenu(pot.isHopper() ? HOPPER_MENU.get() : BASIC_MENU.get(), containerId, playerInv, pot, pot.isHopper(), new BlockPosData(pot.getBlockPos()));
    }

    private final Level level;
    private final Container potContainer;
    private final Inventory playerInv;
    private final ContainerData blockPos;
    protected final boolean isHopper;

    public BotanyPotMenu(MenuType<?> menuType, int id, Inventory playerInv, Container potContainer, boolean isHopper, ContainerData data) {
        super(menuType, id);
        this.level = playerInv.player.level();
        this.playerInv = playerInv;
        this.potContainer = potContainer;
        this.isHopper = isHopper;
        this.blockPos = data;
        this.addDataSlots(this.blockPos);

        // Add the pot's Soil and Crop slot.
        final int slotXOffset = isHopper ? 44 : 80;
        this.addSlot(new InputSlot(potContainer, BotanyPotBlockEntity.SOIL_SLOT, slotXOffset, 48, EMPTY_SLOT_SOIL));
        this.addSlot(new InputSlot(potContainer, BotanyPotBlockEntity.SEED_SLOT, slotXOffset, 22, EMPTY_SLOT_SEED));

        // Hoe item only works on hoppers
        if (isHopper) {
            this.addSlot(new InputSlot(potContainer, BotanyPotBlockEntity.TOOL_SLOT, 18, 35, EMPTY_SLOT_HOE, stack -> stack.is(HARVEST_ITEM)));
        }

        // Add the hopper pot's 4x3 output slots.
        if (isHopper) {
            for (int potOutputY = 0; potOutputY < 3; potOutputY++) {
                for (int potOutputX = 0; potOutputX < 4; potOutputX++) {
                    final int slotId = 3 + (potOutputX + potOutputY * 4);
                    final int slotX = 80 + potOutputX * 18;
                    final int slotY = 17 + potOutputY * 18;
                    this.addSlot(new OutputSlot(potContainer, slotId, slotX, slotY));
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

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int clickedSlotId) {
        final BlockEntityContext context = this.getContext();
        final Slot clickedSlot = this.slots.get(clickedSlotId);

        if (context == null) {
            return ItemStack.EMPTY;
        }

        final int firstSlot = isHopper ? 14 : 2;
        final int lastSlot = isHopper ? 51 : 38;
        ItemStack unplacedItems = ItemStack.EMPTY;

        if (clickedSlot.hasItem()) {
            final ItemStack clickedStack = clickedSlot.getItem();
            unplacedItems = clickedStack.copy();

            // Attempt to move an output to the player inventory.
            if (isHopper && clickedSlotId > AbstractBotanyPotBlockEntity.TOOL_SLOT && clickedSlotId <= firstSlot) {
                if (!this.moveItemStackTo(clickedStack, firstSlot, lastSlot, true)) {
                    return ItemStack.EMPTY;
                }
                clickedSlot.onQuickCraft(clickedStack, unplacedItems);
            }

            // Attempt moving the soil or seed slot to the player inventory.
            else if (clickedSlotId == BotanyPotBlockEntity.SOIL_SLOT || clickedSlotId == BotanyPotBlockEntity.SEED_SLOT || (isHopper && clickedSlotId == BotanyPotBlockEntity.TOOL_SLOT)) {
                if (!this.moveItemStackTo(clickedStack, firstSlot, lastSlot, true)) {
                    return ItemStack.EMPTY;
                }
            }

            // Attempt transferring a seed or soil into the pot.
            else if (clickedSlotId >= firstSlot && clickedSlotId <= lastSlot) {
                // Try to insert a tool
                final Slot toolSlot = this.slots.get(BotanyPotBlockEntity.TOOL_SLOT);
                if (!toolSlot.hasItem() && clickedStack.is(HARVEST_ITEM)) {
                    toolSlot.set(clickedStack.split(1));
                    clickedSlot.set(clickedStack);
                    return ItemStack.EMPTY;
                }
                // Try to insert a soil
                final Slot soilSlot = this.slots.get(BotanyPotBlockEntity.SOIL_SLOT);
                if (!soilSlot.hasItem() && Objects.requireNonNull(Soil.CACHE.apply(level)).lookup(clickedStack, context, player.level()) != null) {
                    soilSlot.set(clickedStack.split(1));
                    clickedSlot.set(clickedStack);
                    return ItemStack.EMPTY;
                }

                // Try to insert a seed
                final Slot cropSlot = this.slots.get(BotanyPotBlockEntity.SEED_SLOT);
                if (!cropSlot.hasItem() && Objects.requireNonNull(Crop.CACHE.apply(level)).lookup(clickedStack, context, player.level()) != null) {
                    cropSlot.set(clickedStack.split(1));
                    clickedSlot.set(clickedStack);
                    return ItemStack.EMPTY;
                }
            }

            if (clickedStack.isEmpty()) {
                clickedSlot.set(ItemStack.EMPTY);
            }
            else {
                clickedSlot.setChanged();
            }
            // Stop when no items left to move
            if (clickedStack.getCount() == unplacedItems.getCount()) {
                return ItemStack.EMPTY;
            }
            clickedSlot.onTake(player, clickedStack);
        }
        return unplacedItems;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.potContainer.stillValid(player);
    }

    @Nullable
    public BlockEntityContext getContext() {
        final BlockPos pos = this.blockPos instanceof BlockPosData posData ? posData.getPos() : BlockPosData.readPos(this.blockPos);
        return this.playerInv.player.level().getBlockEntity(pos) instanceof BotanyPotBlockEntity pot ? pot.getRecipeContext() : null;
    }
}