package net.darkhax.botanypots.block.inv;

import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.stream.IntStream;

public class BotanyPotContainer extends SimpleContainer implements WorldlyContainer  {

    public static final int SOIL_SLOT = 0;
    public static final int CROP_SLOT = 1;
    public static final int[] STORAGE_SLOT = IntStream.range(2, 14).toArray();
    public static final int[] EMPTY_SLOTS = new int[0];

    private final BlockEntityBotanyPot potEntity;

    public BotanyPotContainer(BlockEntityBotanyPot potEntity) {

        super(14);
        this.potEntity = potEntity;
        this.addListener(container -> potEntity.refreshGrowingInfo());
    }

    public ItemStack getSoil() {

        return this.getItem(SOIL_SLOT);
    }

    public ItemStack getCrop() {

        return this.getItem(CROP_SLOT);
    }

    public BlockEntityBotanyPot getPotEntity() {

        return this.potEntity;
    }

    @Override
    public boolean canPlaceItem(int slotId, ItemStack toPlace) {

        if (this.potEntity.isRemoved() || this.potEntity.getLevel() == null || toPlace.isEmpty()) {

            return false;
        }

        // Only allow soils in the soil slot.
        if (slotId == SOIL_SLOT) {

            return BotanyPotHelper.getSoil(this.potEntity.getLevel().getRecipeManager(), toPlace) != null;
        }

        // Only allow crop seeds in the crop slot.
        if (slotId == CROP_SLOT) {

            return BotanyPotHelper.getCrop(this.potEntity.getLevel().getRecipeManager(), toPlace) != null;
        }

        // Remaining slots only available to hoppers.
        return this.potEntity.isHopper();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {

        return side == Direction.DOWN ? STORAGE_SLOT : EMPTY_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slotId, ItemStack toInsert, Direction side) {

        return (side == null || side == Direction.DOWN) && this.potEntity.isHopper() && slotId != SOIL_SLOT && slotId != CROP_SLOT;
    }

    @Override
    public boolean canTakeItemThroughFace(int slotId, ItemStack toExtract, Direction side) {

        return (side == null || side == Direction.DOWN) && this.potEntity.isHopper() && slotId != SOIL_SLOT && slotId != CROP_SLOT;
    }

    @Override
    public boolean stillValid(Player player) {

        if (this.potEntity == null || this.potEntity.isRemoved()) {

            return false;
        }

        final BlockPos pos = this.potEntity.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f) <= 24d;
    }
}