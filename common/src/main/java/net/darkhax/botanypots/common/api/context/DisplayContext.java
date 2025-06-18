package net.darkhax.botanypots.common.api.context;

import net.darkhax.bookshelf.common.api.PhysicalSide;
import net.darkhax.bookshelf.common.api.annotation.OnlyFor;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.Helpers;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Recipe context that is provided when a pot is not available. This context should only be used on the client in
 * contexts like a JEI plugin.
 *
 * @param inventory A list of items simulating the inventory of a botany pot. This is not an actual inventory, only an
 *                  immutable simulation.
 */
@OnlyFor(PhysicalSide.CLIENT)
public record DisplayContext(List<ItemStack> inventory) implements BotanyPotContext {

    @Override
    public ItemStack getSoilItem() {
        return inventory.get(BotanyPotBlockEntity.SOIL_SLOT);
    }

    @Override
    public ItemStack getSeedItem() {
        return inventory.get(BotanyPotBlockEntity.SEED_SLOT);
    }

    @Override
    public ItemStack getHarvestItem() {
        return inventory.get(BotanyPotBlockEntity.TOOL_SLOT);
    }

    @Override
    public LootParams createLootParams(@Nullable BlockState state) {
        throw new IllegalStateException("Display context can not generate loot.");
    }

    @Override
    public void runFunction(ResourceLocation functionId) {
        throw new IllegalStateException("Display context can not run functions! Function=" + functionId.toString());
    }

    @Nullable
    @Override
    public Player getPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public ItemStack getInteractionItem() {
        return this.getPlayer() != null ? getPlayer().getMainHandItem() : ItemStack.EMPTY;
    }

    @Override
    public int getRequiredGrowthTicks() {
        if (this.getPlayer() != null) {
            final Level level = this.getPlayer().level();
            final RecipeHolder<Soil> soil = Objects.requireNonNull(Soil.CACHE.apply(level)).lookup(this.getSoilItem(), this, level);
            final RecipeHolder<Crop> crop = Objects.requireNonNull(Crop.CACHE.apply(level)).lookup(this.getSeedItem(), this, level);
            if (crop != null) {
                return Helpers.getRequiredGrowthTicks(this, level, crop.value(), soil != null ? soil.value() : null);
            }
        }
        return -1;
    }

    @Override
    public boolean isServerThread() {
        return false;
    }

    @Nullable
    @Override
    public Crop getCrop() {
        if (this.getPlayer() != null) {
            final Level level = this.getPlayer().level();
            final RecipeHolder<Crop> crop = Objects.requireNonNull(Crop.CACHE.apply(level)).lookup(this.getSeedItem(), this, level);
            if (crop != null) {
                return crop.value();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Soil getSoil() {
        if (this.getPlayer() != null) {
            final Level level = this.getPlayer().level();
            final RecipeHolder<Soil> soil = Objects.requireNonNull(Soil.CACHE.apply(level)).lookup(this.getSoilItem(), this, level);
            if (soil != null) {
                return soil.value();
            }
        }
        return null;
    }

    @NotNull
    @Override
    public ItemStack getItem(int i) {
        return this.inventory.get(i);
    }

    @Override
    public int size() {
        return this.inventory.size();
    }
}