package net.darkhax.botanypots.block;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.block.entity.WorldlyInventoryBlockEntity;
import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.bookshelf.api.inventory.ContainerInventoryAccess;
import net.darkhax.bookshelf.api.inventory.IInventoryAccess;
import net.darkhax.bookshelf.api.registry.RegistryObject;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.darkhax.bookshelf.api.util.WorldHelper;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.block.inv.BotanyPotContainer;
import net.darkhax.botanypots.block.inv.BotanyPotMenu;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockEntityBotanyPot extends WorldlyInventoryBlockEntity<BotanyPotContainer> {

    public static final CachedSupplier<BlockEntityType<BlockEntityBotanyPot>> POT_TYPE = RegistryObject.deferred(Registry.BLOCK_ENTITY_TYPE, Constants.MOD_ID, "botany_pot").cast();
    private static final Component DEFAULT_NAME = Component.translatable("block.botanypots.terracotta_botany_pot");

    protected int growthTime = -1;
    protected boolean doneGrowing = false;
    protected int prevComparatorLevel = 0;
    protected int comparatorLevel = 0;
    protected int harvestDelay = -1;
    protected int exportDelay = -1;

    final Random rng = new Random();
    private long rngSeed;

    public BlockEntityBotanyPot(BlockEntityType potType, BlockPos pos, BlockState state) {

        super(potType, pos, state);
        this.refreshRandom();
    }

    public boolean isHopper() {

        if (this.getLevel() != null && this.getLevel().getBlockState(this.getBlockPos()).getBlock() instanceof BlockBotanyPot potBlock) {

            return potBlock.hasInventory();
        }

        return false;
    }

    public void refreshRandom() {

        this.rngSeed = Constants.RANDOM.nextLong();
        this.rng.setSeed(rngSeed);
    }

    @Nullable
    public Crop getCrop() {

        return this.getInventory().getCrop();
    }

    @Nullable
    public Soil getSoil() {

        return this.getInventory().getSoil();
    }

    public boolean isGrowing() {

        return this.growthTime > 0;
    }

    public boolean areGrowthConditionsMet() {

        return BotanyPotHelper.canCropGrow(this.level, this.getBlockPos(), this, this.getSoil(), this.getCrop());
    }

    public boolean isCropHarvestable() {

        return this.doneGrowing;
    }

    public int getLightLevel() {

        final Soil soil = this.getSoil();
        final Crop crop = this.getCrop();

        final int soilLight = soil != null ? soil.getLightLevel(this.level, this.getBlockPos(), this) : 0;
        final int cropLight = crop != null ? crop.getLightLevel(this.level, this.getBlockPos(), this) : 0;
        return Math.max(soilLight, cropLight);
    }

    public int getGrowthTime() {

        return this.growthTime;
    }

    public int getComparatorLevel() {

        return this.comparatorLevel;
    }

    public boolean isValidSoil(ItemStack stack) {

        return BotanyPotHelper.findSoil(this.level, this.getBlockPos(), this, stack) != null;
    }

    public boolean isValidSeed(ItemStack stack) {

        return BotanyPotHelper.findCrop(this.level, this.getBlockPos(), this, stack) != null;
    }

    public boolean attemptAutoHarvest() {

        if (this.getLevel() != null && !this.getLevel().isClientSide && this.getCrop() != null) {

            final ContainerInventoryAccess<BotanyPotContainer> inventory = new ContainerInventoryAccess<>(this.getInventory());

            this.rng.setSeed(this.rngSeed);
            final List<ItemStack> drops = BotanyPotHelper.generateDrop(rng, this.level, this.getBlockPos(), this, this.getCrop());

            if (drops.isEmpty()) {

                return true;
            }

            boolean didCollect = false;

            for (ItemStack drop : drops) {

                if (!drop.isEmpty()) {

                    final int originalSize = drop.getCount();

                    for (int slot : BotanyPotContainer.STORAGE_SLOT) {

                        if (drop.isEmpty()) {

                            break;
                        }

                        drop = inventory.insert(slot, drop, Direction.UP, true, true);
                    }

                    if (drop.getCount() != originalSize) {

                        didCollect = true;
                    }
                }
            }

            return didCollect;
        }

        return false;
    }

    private void attemptExport() {

        if (this.getLevel() != null && !this.getLevel().isClientSide) {

            final IInventoryAccess exportTo = Services.INVENTORY_HELPER.getInventory(this.getLevel(), this.getBlockPos().below(), Direction.UP);

            if (exportTo != null) {

                for (int potSlotId : BotanyPotContainer.STORAGE_SLOT) {

                    final ItemStack potStack = this.getInventory().getItem(potSlotId);

                    if (!potStack.isEmpty()) {

                        for (int exportSlotId : exportTo.getAvailableSlots()) {

                            if (exportTo.insert(exportSlotId, potStack, Direction.UP, false).getCount() != potStack.getCount()) {

                                this.getInventory().setItem(potSlotId, exportTo.insert(exportSlotId, potStack, Direction.UP, true));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void tickPot(Level level, BlockPos pos, BlockState state, BlockEntityBotanyPot pot) {

        // Don't try to update unloaded pots.
        if (pot.isRemoved() || pot.getLevel() == null) {

            return;
        }

        pot.getInventory().update();

        final Soil soil = pot.getSoil();
        final Crop crop = pot.getCrop();

        if (soil != null) {

            soil.onTick(level, pos, pot);
        }

        if (crop != null) {

            crop.onTick(level, pos, pot);
        }

        // Harvesting Logic
        if (pot.isHopper()) {

            if (pot.exportDelay > 0) {

                pot.exportDelay--;
            }

            if (pot.harvestDelay > 0) {

                pot.harvestDelay--;
            }

            if (pot.isCropHarvestable() && crop != null && pot.harvestDelay < 1) {

                if (pot.attemptAutoHarvest()) {

                    pot.resetGrowth();
                }

                // Wait at least 5 seconds before trying to harvest again.
                pot.harvestDelay = 100;
            }

            if (pot.exportDelay < 1) {

                pot.attemptExport();

                // Wait at least 1 second before trying to export items again.
                pot.exportDelay = 20;
            }
        }

        // Growth Logic
        if (soil != null && crop != null && pot.areGrowthConditionsMet()) {

            if (!pot.doneGrowing) {

                pot.growthTime++;
                soil.onGrowthTick(level, pos, pot, crop);
                crop.onGrowthTick(level, pos, pot, soil);

                pot.prevComparatorLevel = pot.comparatorLevel;
                pot.comparatorLevel = Mth.floor(15f * ((float) pot.growthTime / pot.getInventory().getRequiredGrowthTime()));

                final boolean finishedGrowing = pot.growthTime >= pot.getInventory().getRequiredGrowthTime();

                if (pot.doneGrowing != finishedGrowing) {

                    pot.doneGrowing = finishedGrowing;
                    pot.markDirty();
                }
            }
        }

        else if (pot.growthTime != -1 || pot.doneGrowing || pot.comparatorLevel != 0) {

            pot.resetGrowth();
        }

        // Update Comparators if they need it.
        if (pot.comparatorLevel != pot.prevComparatorLevel) {

            pot.prevComparatorLevel = pot.comparatorLevel;
            pot.level.updateNeighbourForOutputSignal(pot.worldPosition, pot.getBlockState().getBlock());
        }
    }

    public void resetGrowth() {

        this.growthTime = -1;
        this.comparatorLevel = 0;
        this.prevComparatorLevel = 0;
        this.doneGrowing = false;
        this.refreshRandom();
        this.markDirty();
    }

    public void markDirty() {

        if (this.level != null && !this.level.isClientSide) {

            super.markDirty();
            WorldHelper.updateBlockEntity(this, false);
        }
    }

    @Override
    public void load(CompoundTag tag) {

        super.load(tag);

        this.growthTime = Serializers.INT.fromNBT(tag, "GrowthTime", 0);
        this.doneGrowing = Serializers.BOOLEAN.fromNBT(tag, "DoneGrowing", false);
        this.prevComparatorLevel = Serializers.INT.fromNBT(tag, "PrevComparatorLevel", 0);
        this.comparatorLevel = Serializers.INT.fromNBT(tag, "ComparatorLevel", 0);
        this.harvestDelay = Serializers.INT.fromNBT(tag, "HarvestDelay", -1);
        this.exportDelay = Serializers.INT.fromNBT(tag, "ExportDelay", -1);
        this.rngSeed = Serializers.LONG.fromNBT(tag, "RandomSeed", Constants.RANDOM.nextLong());
        this.rng.setSeed(this.rngSeed);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {

        super.saveAdditional(tag);

        Serializers.INT.toNBT(tag, "GrowthTime", this.growthTime);
        Serializers.BOOLEAN.toNBT(tag, "DoneGrowing", this.doneGrowing);
        Serializers.INT.toNBT(tag, "PrevComparatorLevel", this.prevComparatorLevel);
        Serializers.INT.toNBT(tag, "ComparatorLevel", this.comparatorLevel);
        Serializers.INT.toNBT(tag, "HarvestDelay", this.harvestDelay);
        Serializers.INT.toNBT(tag, "ExportDelay", this.exportDelay);
        Serializers.LONG.toNBT(tag, "RandomSeed", this.rngSeed);
    }

    @Override
    public CompoundTag getUpdateTag() {

        final CompoundTag updateTag = super.getUpdateTag();
        this.saveAdditional(updateTag);
        return updateTag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public BotanyPotContainer createInventory() {

        return new BotanyPotContainer(this);
    }

    @Override
    protected Component getDefaultName() {

        return DEFAULT_NAME;
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory) {

        return new BotanyPotMenu(windowId, this.getInventory(), inventory);
    }

    public void addGrowth(int nextIntInclusive) {

        this.growthTime += nextIntInclusive;
        this.markDirty();
    }
}