package net.darkhax.botanypots.block.inv;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class SlotCropSeed extends Slot {

    private final Predicate<ItemStack> seedTest;

    public SlotCropSeed(Container container, int slotId, int xPos, int yPos, Predicate<ItemStack> seedTest) {

        super(container, slotId, xPos, yPos);
        this.seedTest = seedTest;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        return this.seedTest.test(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {

        return 1;
    }
}