package net.darkhax.botanypots.block.inv;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class SlotSoil extends Slot {

    private final Predicate<ItemStack> soilTest;

    public SlotSoil(Container container, int slotId, int xPos, int yPos, Predicate<ItemStack> soilTest) {

        super(container, slotId, xPos, yPos);
        this.soilTest = soilTest;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        return this.soilTest.test(stack);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {

        return 1;
    }
}
