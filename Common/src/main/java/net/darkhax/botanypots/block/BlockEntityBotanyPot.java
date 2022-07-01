package net.darkhax.botanypots.block;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.block.entity.WorldlyInventoryBlockEntity;
import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.block.inv.BotanyPotContainer;
import net.darkhax.botanypots.block.inv.BotanyPotMenu;
import net.darkhax.botanypots.data.crop.CropInfo;
import net.darkhax.botanypots.data.soil.SoilInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityBotanyPot extends WorldlyInventoryBlockEntity<BotanyPotContainer> {

    private static final CachedSupplier<BlockEntityType<?>> POT_TYPE = CachedSupplier.cache(() -> Services.REGISTRIES.blockEntities().get(new ResourceLocation(Constants.MOD_ID, "botany_pot")));
    private static final Component DEFAULT_NAME = new TranslatableComponent("block.botanypots.terracotta_botany_pot");

    private boolean isHopper = false;
    private SoilInfo soil = null;
    private CropInfo crop = null;

    protected int growthDuration = -1;
    protected boolean doneGrowing = false;
    protected int prevComparatorLevel = 0;
    protected int comparatorLevel = 0;

    public BlockEntityBotanyPot(BlockPos pos, BlockState state) {

        super(POT_TYPE.get(), pos, state);
    }

    public boolean isHopper() {

        return this.isHopper;
    }

    protected void setHopper(boolean isHopper) {

        this.isHopper = isHopper;
    }

    public boolean isGrowing() {

        return this.growthDuration > 0;
    }

    public boolean areGrowthConditionsMet() {

        return this.soil != null && this.crop != null;
    }

    public boolean isCropHarvestable() {

        return this.doneGrowing;
    }

    public int getLightLevel() {

        final int soilLight = this.soil != null ? this.soil.getLightLevel() : 0;
        final int cropLight = this.crop != null ? this.crop.getLightLevel() : 0;
        return Math.max(soilLight, cropLight);
    }

    public int getComparatorLevel() {

        return this.comparatorLevel;
    }

    public void refreshGrowingInfo() {

        if (this.getLevel() != null) {

            final RecipeManager manager = this.getLevel().getRecipeManager();
            this.soil = BotanyPotHelper.getSoil(manager, this.getInventory().getSoil());
            this.crop = BotanyPotHelper.getCrop(manager, this.getInventory().getCrop());
        }

        this.markDirty();
    }

    public boolean isValidSoil(ItemStack stack) {

        return !stack.isEmpty() && !this.isRemoved() && this.getLevel() != null && BotanyPotHelper.getSoil(this.getLevel().getRecipeManager(), stack) != null;
    }

    public boolean isValidSeed(ItemStack stack) {

        return !stack.isEmpty() && !this.isRemoved() && this.getLevel() != null && BotanyPotHelper.getCrop(this.getLevel().getRecipeManager(), stack) != null;
    }

    public static void tickPot(Level level, BlockPos pos, BlockState state, BlockEntityBotanyPot pot) {

        // Don't try to update unloaded pots.
        if (pot.isRemoved() || pot.level == null) {

            return;
        }

        // Harvesting Logic
        if (pot.isHopper && pot.isCropHarvestable()) {

            // TODO harvest the crop
        }

        // Growth Logic
        if (pot.areGrowthConditionsMet()) {

            if (!pot.doneGrowing) {

                pot.growthDuration++;
                pot.prevComparatorLevel = pot.comparatorLevel;

                final int requiredGrowthTicks = pot.crop.getGrowthTicksForSoil(pot.soil);
                pot.comparatorLevel = Mth.floor(15f * ((float) pot.growthDuration / requiredGrowthTicks));
                pot.doneGrowing = pot.growthDuration >= requiredGrowthTicks;
            }
        }

        else {

            pot.growthDuration = -1;
            pot.comparatorLevel = 0;
            pot.doneGrowing = false;
        }

        // Update Comparators if they need it.
        if (pot.comparatorLevel != pot.prevComparatorLevel) {

            pot.level.updateNeighbourForOutputSignal(pot.worldPosition, pot.getBlockState().getBlock());
        }
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