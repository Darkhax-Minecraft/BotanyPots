package net.darkhax.botanypots.common.impl.block.entity;

import net.darkhax.bookshelf.common.api.data.enchantment.EnchantmentLevel;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.function.ReloadableCache;
import net.darkhax.bookshelf.common.api.service.Services;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.bookshelf.common.api.util.TickAccumulator;
import net.darkhax.botanypots.common.api.context.BlockEntityContext;
import net.darkhax.botanypots.common.api.data.components.CropOverride;
import net.darkhax.botanypots.common.api.data.components.SoilOverride;
import net.darkhax.botanypots.common.api.data.recipes.BotanyPotRecipe;
import net.darkhax.botanypots.common.api.data.recipes.RecipeCache;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.Helpers;
import net.darkhax.botanypots.common.impl.block.PotType;
import net.darkhax.botanypots.common.impl.block.menu.BotanyPotMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BotanyPotBlockEntity extends AbstractBotanyPotBlockEntity {

    public static final CachedSupplier<BlockEntityType<BotanyPotBlockEntity>> TYPE = CachedSupplier.of(BuiltInRegistries.BLOCK_ENTITY_TYPE, BotanyPotsMod.id("botany_pot")).cast();

    private final BlockEntityContext recipeContext = new BlockEntityContext(this, null, null);

    private final ReloadableCache<RecipeHolder<Soil>> soil = ReloadableCache.of(level -> {
        final Optional<SoilOverride> override = SoilOverride.get(this.getSoilItem());
        if (override.isPresent()) {
            return new RecipeHolder<>(BotanyPotsMod.BUILTIN_COMPONENT_ID, override.get().soil());
        }
        final RecipeCache<Soil> cache = Soil.CACHE.apply(level);
        return cache != null ? cache.lookup(this.getSoilItem(), recipeContext, level) : null;
    });

    private final ReloadableCache<RecipeHolder<Crop>> crop = ReloadableCache.of(level -> {
        final Optional<CropOverride> override = CropOverride.get(this.getSeedItem());
        if (override.isPresent()) {
            return new RecipeHolder<>(BotanyPotsMod.BUILTIN_COMPONENT_ID, override.get().crop());
        }
        final RecipeCache<Crop> cache = Crop.CACHE.apply(level);
        return cache != null ? cache.lookup(this.getSeedItem(), recipeContext, level) : null;
    });

    public TickAccumulator growthTime = new TickAccumulator(-1f);
    public int comparatorLevel = 0;
    protected TickAccumulator exportCooldown = new TickAccumulator(0f);
    protected TickAccumulator growCooldown = new TickAccumulator(0f);
    private int bonemealCooldown = 0;

    public BotanyPotBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE.get(), pos, state);
    }

    public static void tickPot(Level level, BlockPos pos, BlockState state, BotanyPotBlockEntity pot) {
        if (pot.isRemoved() || pot.level == null) {
            return;
        }
        if (pot.potType.get() == PotType.WAXED) {
            pot.growthTime.setTicks(Float.MAX_VALUE);
            return;
        }
        final BlockEntityContext context = pot.recipeContext;
        if (pot.bonemealCooldown > 0) {
            pot.bonemealCooldown--;
        }
        // Update soil
        final Soil soil = pot.getOrInvalidateSoil();
        if (soil != null) {
            soil.onTick(context, level);
        }
        // Update crop
        final Crop crop = pot.getOrInvalidateCrop();
        if (crop != null) {
            crop.onTick(context, level);
            if (pot.growCooldown.getTicks() > 0) {
                pot.growCooldown.tickDown(level);
            }
            if (pot.growCooldown.getTicks() <= 0 && crop.isGrowthSustained(context, level)) {
                pot.growthTime.tickUp(level);
                crop.onGrowthTick(context, level);
                final int requiredGrowthTicks = Helpers.getRequiredGrowthTicks(pot.recipeContext, pot.level, crop, soil);
                if (pot.growthTime.getTicks() >= requiredGrowthTicks) {
                    pot.updateComparatorLevel(15);
                    pot.growCooldown.setTicks(5f);
                    if (pot.isHopper() && crop.canHarvest(context, level)) {
                        if (level instanceof ServerLevel serverLevel) {
                            crop.onHarvest(context, level, stack -> Services.GAMEPLAY.addItem(stack, pot.getItems(), BotanyPotBlockEntity.STORAGE_SLOTS));
                            if (BotanyPotsMod.CONFIG.get().gameplay.damage_harvest_tool && EnchantmentLevel.FIRST.get(Helpers.NEGATE_HARVEST_DAMAGE_TAG, pot.getHarvestItem()) <= 0) {
                                pot.getHarvestItem().hurtAndBreak(1, serverLevel, null, stack -> {
                                });
                            }
                            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(pot.getBlockState()));
                        }
                        pot.growthTime.reset();
                    }
                }
                else {
                    pot.updateComparatorLevel(Mth.ceil(14f * (pot.growthTime.getTicks() / requiredGrowthTicks)));
                }
            }
        }
        // Update Inventory
        if (pot.isHopper()) {
            pot.exportCooldown.tickDown(level);
            if (pot.exportCooldown.getTicks() <= 0) {
                if (level instanceof ServerLevel serverLevel && !serverLevel.getBlockState(pot.below.get()).isAir()) {
                    for (int slot : BotanyPotBlockEntity.STORAGE_SLOTS) {
                        final ItemStack stack = pot.getItem(slot);
                        if (!stack.isEmpty()) {
                            final ItemStack result = Services.GAMEPLAY.inventoryInsert(serverLevel, pot.below.get(), Direction.UP, stack);
                            pot.setItem(slot, result);
                        }
                    }
                }
                pot.exportCooldown.reset();
            }
        }
    }

    public boolean canHarvest() {
        final Soil soil = this.getOrInvalidateSoil();
        final Crop crop = this.getOrInvalidateCrop();
        return soil != null && crop != null && this.growthTime.getTicks() >= Helpers.getRequiredGrowthTicks(this.getRecipeContext(), this.level, crop, soil) && crop.canHarvest(this.getRecipeContext(), this.level);
    }

    public void reset() {
        this.growthTime.reset();
        this.updateComparatorLevel(0);
        this.crop.invalidate();
        this.soil.invalidate();
        this.bonemealCooldown = 0;
    }

    public void updateGrowthTime(float newTime) {
        this.growthTime.setTicks(newTime);
        this.markUpdated();
    }

    public void updateComparatorLevel(int newLevel) {
        if (newLevel != this.comparatorLevel && !this.isRemoved() && this.level instanceof ServerLevel serverLevel) {
            this.comparatorLevel = newLevel;
            serverLevel.updateNeighbourForOutputSignal(this.worldPosition, this.getBlockState().getBlock());
        }
    }

    public float growthTime() {
        return this.growthTime.getTicks();
    }

    public BlockEntityContext getRecipeContext() {
        return this.recipeContext;
    }

    public boolean canBonemeal() {
        return this.bonemealCooldown <= 0;
    }

    public void setBonemealCooldown(int cooldown) {
        this.bonemealCooldown = cooldown;
    }

    @Override
    public void onSoilChanged(ItemStack newStack) {
        this.reset();
        this.markUpdated();
    }

    @Override
    public void onSeedChanged(ItemStack newStack) {
        this.reset();
        this.markUpdated();
    }

    @Override
    public void onToolChanged(ItemStack newStack) {
        this.markUpdated();
    }

    public Soil getOrInvalidateSoil() {
        return this.getOrInvalidate(this.getSoilItem(), this.soil);
    }

    public Crop getOrInvalidateCrop() {
        return this.getOrInvalidate(this.getSeedItem(), this.crop);
    }

    @Nullable
    public <T extends BotanyPotRecipe> T getOrInvalidate(ItemStack stack, ReloadableCache<RecipeHolder<T>> cache) {
        if (this.level == null || stack.isEmpty()) {
            cache.invalidate();
            return null;
        }
        final RecipeHolder<T> value = cache.apply(level);
        if (value != null) {
            final T innerValue = value.value();
            if (!innerValue.matches(this.recipeContext, this.level)) {
                cache.invalidate();
                return null;
            }
            return innerValue;
        }
        return null;
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.growthTime.setTicks(tag.getFloat("growth_time"));
        this.comparatorLevel = tag.getInt("comparator_level");
        this.exportCooldown = new TickAccumulator(tag.getFloat("export_delay"));
        this.growCooldown = new TickAccumulator(tag.getFloat("grow_cooldown"));
        this.bonemealCooldown = tag.getInt("bonemeal_cooldown");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("growth_time", this.growthTime.getTicks());
        tag.putInt("comparator_level", this.comparatorLevel);
        tag.putFloat("export_delay", this.exportCooldown.getTicks());
        tag.putFloat("grow_cooldown", this.growCooldown.getTicks());
        tag.putInt("bonemeal_cooldown", this.bonemealCooldown);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        final CompoundTag syncTag = this.saveCustomOnly(registries);
        syncTag.remove("comparator_level");
        syncTag.put("Items", DataHelper.containerSubList(syncTag.getList("Items", Tag.TAG_COMPOUND), slot -> slot <= TOOL_SLOT));
        return syncTag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInv) {
        return BotanyPotMenu.potMenuServer(containerId, playerInv, this);
    }

    public int getRequiredGrowthTicks() {
        final RecipeHolder<Crop> crop = this.crop.apply(this.level);
        return crop == null ? -1 : Helpers.getRequiredGrowthTicks(this.recipeContext, this.level, crop.value(), this.soil.map(this.level, RecipeHolder::value));
    }
}