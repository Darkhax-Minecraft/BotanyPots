package net.darkhax.botanypots.common.impl.block.entity;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.block.BotanyPotBlock;
import net.darkhax.botanypots.common.impl.block.PotType;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.IntStream;

public abstract class AbstractBotanyPotBlockEntity extends RandomizableContainerBlockEntity implements WorldlyContainer {

    public static final int SOIL_SLOT = 0;
    public static final int SEED_SLOT = 1;
    public static final int TOOL_SLOT = 2;
    public static final int SLOT_COUNT = 15;
    public static final int[] STORAGE_SLOTS = IntStream.range(3, SLOT_COUNT).toArray();
    public static final int[] EMPTY_SLOTS = new int[0];
    public static final Component DEFAULT_NAME = Component.translatable("container.botanypots.botany_pot");

    private NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    protected CachedSupplier<BlockPos> below;
    protected CachedSupplier<PotType> potType;

    public abstract void onSoilChanged(ItemStack newStack);

    public abstract void onSeedChanged(ItemStack newStack);

    public abstract void onToolChanged(ItemStack newStack);

    protected AbstractBotanyPotBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.below = CachedSupplier.cache(() -> this.getBlockPos().below());
        this.potType = CachedSupplier.cache(() -> ((BotanyPotBlock) this.getBlockState().getBlock()).type);
    }

    public PotType getPotType() {
        return this.potType.get();
    }

    public boolean isHopper() {
        return this.potType.get() == PotType.HOPPER;
    }

    public ItemStack getSoilItem() {
        return this.items.get(SOIL_SLOT);
    }

    public void setSoilItem(ItemStack newSoil) {
        this.setItem(SOIL_SLOT, newSoil);
    }

    public ItemStack getSeedItem() {
        return this.items.get(SEED_SLOT);
    }

    public void setSeed(ItemStack newSeed) {
        this.setItem(SEED_SLOT, newSeed);
    }

    public ItemStack getHarvestItem() {
        return this.getItem(TOOL_SLOT);
    }

    public void setHoe(ItemStack hoeStack) {
        this.setItem(TOOL_SLOT, hoeStack);
    }

    public void runFunction(ResourceLocation functionId) {
        if (this.level instanceof ServerLevel serverLevel) {
            final Optional<CommandFunction<CommandSourceStack>> function = serverLevel.getServer().getFunctions().get(functionId);
            final CommandSourceStack sourceStack = this.createCommandSourceStack();
            if (sourceStack != null) {
                function.ifPresentOrElse(func -> serverLevel.getServer().getFunctions().execute(func, sourceStack), () -> BotanyPotsMod.LOG.error("Pot at {} tried to run missing function {}. This is an issue with your datapacks.", this.worldPosition, functionId));
            }
        }
    }

    @Nullable
    public CommandSourceStack createCommandSourceStack() {
        if (this.level instanceof ServerLevel serverLevel) {
            final Direction direction = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            final Component name = this.getName();
            return new CommandSourceStack(CommandSource.NULL, Vec3.atCenterOf(this.worldPosition), new Vec2(0f, direction.toYRot()), serverLevel, 2, name.getString(), name, serverLevel.getServer(), null);
        }
        return null;
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, registries);
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, registries);
        }
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    public void setItem(int slotId, @NotNull ItemStack stack) {
        super.setItem(slotId, stack);
        if (slotId == SOIL_SLOT) {
            this.onSoilChanged(stack);
        }
        else if (slotId == SEED_SLOT) {
            this.onSeedChanged(stack);
        }
        else if (slotId == TOOL_SLOT) {
            this.onToolChanged(stack);
        }
    }

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        return side == Direction.DOWN ? STORAGE_SLOTS : EMPTY_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction side) {
        // Botany Pots only allow extracting items via automation.
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction side) {
        // Only storage slots (2-13) can be extracted, and only from the bottom face.
        return side == Direction.DOWN && this.isHopper() && slot > TOOL_SLOT && slot < SLOT_COUNT;
    }

    @NotNull
    @Override
    protected Component getDefaultName() {
        return DEFAULT_NAME;
    }

    @NotNull
    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> newItems) {
        this.items = newItems;
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }
}