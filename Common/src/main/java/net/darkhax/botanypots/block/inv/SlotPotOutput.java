package net.darkhax.botanypots.block.inv;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotPotOutput extends Slot {

    public SlotPotOutput(Container container, int slotId, int xPos, int yPos) {

        super(container, slotId, xPos, yPos);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {

        return false;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {

        // TODO add advancement hook?
        super.onTake(player, stack);
    }
}