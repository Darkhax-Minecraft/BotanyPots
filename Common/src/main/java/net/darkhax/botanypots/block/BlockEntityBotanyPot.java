package net.darkhax.botanypots.block;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.block.entity.WorldlyInventoryBlockEntity;
import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.bookshelf.api.inventory.ContainerInventoryAccess;
import net.darkhax.bookshelf.api.inventory.IInventoryAccess;
import net.darkhax.bookshelf.api.serialization.Serializers;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.block.inv.BotanyPotContainer;
import net.darkhax.botanypots.block.inv.BotanyPotMenu;
import net.darkhax.botanypots.data.crop.CropInfo;
import net.darkhax.botanypots.data.soil.SoilInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlockEntityBotanyPot extends WorldlyInventoryBlockEntity<BotanyPotContainer> {

    protected static final CachedSupplier<BlockEntityType<BlockEntityBotanyPot>> POT_TYPE = CachedSupplier.cache(() -> (BlockEntityType<BlockEntityBotanyPot>) Services.REGISTRIES.blockEntities().get(new ResourceLocation(Constants.MOD_ID, "botany_pot")));
    private static final Component DEFAULT_NAME = new TranslatableComponent("block.botanypots.terracotta_botany_pot");

    protected int growthTime = -1;
    protected boolean doneGrowing = false;
    protected int prevComparatorLevel = 0;
    protected int comparatorLevel = 0;

    public BlockEntityBotanyPot(BlockPos pos, BlockState state) {

        super(POT_TYPE.get(), pos, state);
    }

    public boolean isHopper() {

        if (this.getLevel() != null && this.getLevel().getBlockState(this.getBlockPos()).getBlock() instanceof BlockBotanyPot potBlock) {

            return potBlock.hasInventory();
        }

        return false;
    }

    @Nullable
    public CropInfo getCropInfo() {

        return this.getInventory().getCropInfo();
    }

    @Nullable
    public SoilInfo getSoilInfo() {

        return this.getInventory().getSoilInfo();
    }

    public boolean isGrowing() {

        return this.growthTime > 0;
    }

    public boolean areGrowthConditionsMet() {

        return BotanyPotHelper.isSoilValidForCrop(this.getSoilInfo(), this.getCropInfo());
    }

    public boolean isCropHarvestable() {

        return this.doneGrowing;
    }

    public int getLightLevel() {

        final SoilInfo soil = this.getSoilInfo();
        final CropInfo crop = this.getCropInfo();

        final int soilLight = soil != null ? soil.getLightLevel() : 0;
        final int cropLight = crop != null ? crop.getLightLevel() : 0;
        return Math.max(soilLight, cropLight);
    }

    public int getGrowthTime() {

        return this.growthTime;
    }

    public int getComparatorLevel() {

        return this.comparatorLevel;
    }

    public boolean isValidSoil(ItemStack stack) {

        return BotanyPotHelper.getSoil(this.getLevel(), stack) != null;
    }

    public boolean isValidSeed(ItemStack stack) {

        return BotanyPotHelper.getCrop(this.getLevel(), stack) != null;
    }

    public void attemptAutoHarvest() {

        // TODO Spawn Block Break Particles
        if (this.getLevel() != null && !this.getLevel().isClientSide) {

            final ContainerInventoryAccess<BotanyPotContainer> inventory = new ContainerInventoryAccess<>(this.getInventory());

            for (ItemStack drop : this.drops) {

                for (int slot : BotanyPotContainer.STORAGE_SLOT) {

                    if (drop.isEmpty()) {

                        break;
                    }

                    drop = inventory.insert(slot, drop, Direction.UP, true, true);
                }
            }
        }
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

    private List<ItemStack> drops = new ArrayList<>();

    public static void tickPot(Level level, BlockPos pos, BlockState state, BlockEntityBotanyPot pot) {

        // Don't try to update unloaded pots.
        if (pot.isRemoved() || pot.getLevel() == null) {

            return;
        }

        // Harvesting Logic
        if (pot.isHopper()) {

            if (pot.isCropHarvestable() && pot.getCropInfo() != null) {

                pot.drops = BotanyPotHelper.generateDrop(pot.getLevel().random, pot.getCropInfo());
                pot.attemptAutoHarvest();

                // Reset Growth
                pot.growthTime = -1;
                pot.comparatorLevel = 0;
                pot.doneGrowing = false;
            }

            if (pot.getLevel().getGameTime() % 20 == 0) {

                pot.attemptExport();
            }
        }

        // Growth Logic
        if (pot.areGrowthConditionsMet()) {

            if (!pot.doneGrowing) {

                pot.growthTime++;
                pot.prevComparatorLevel = pot.comparatorLevel;

                pot.comparatorLevel = Mth.floor(15f * ((float) pot.growthTime / pot.getInventory().getRequiredGrowthTime()));
                pot.doneGrowing = pot.growthTime >= pot.getInventory().getRequiredGrowthTime();
            }
        }

        else {

            pot.growthTime = -1;
            pot.comparatorLevel = 0;
            pot.doneGrowing = false;
        }

        // Update Comparators if they need it.
        if (pot.comparatorLevel != pot.prevComparatorLevel) {

            pot.level.updateNeighbourForOutputSignal(pot.worldPosition, pot.getBlockState().getBlock());
        }
    }

    @Override
    public void load(CompoundTag tag) {

        super.load(tag);

        this.growthTime = Serializers.INT.fromNBT(tag, "GrowthTime", 0);
        this.doneGrowing = Serializers.BOOLEAN.fromNBT(tag, "DoneGrowing", false);
        this.prevComparatorLevel = Serializers.INT.fromNBT(tag, "PrevComparatorLevel", 0);
        this.comparatorLevel = Serializers.INT.fromNBT(tag, "ComparatorLevel", 0);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {

        super.saveAdditional(tag);

        Serializers.INT.toNBT(tag, "GrowthTime", this.growthTime);
        Serializers.BOOLEAN.toNBT(tag, "DoneGrowing", this.doneGrowing);
        Serializers.INT.toNBT(tag, "PrevComparatorLevel", this.prevComparatorLevel);
        Serializers.INT.toNBT(tag, "ComparatorLevel", this.comparatorLevel);
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

        return new BotanyPotMenu(windowId, this.getInventory(),inventory);
    }
}