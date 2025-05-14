package net.darkhax.botanypots.common.api.context;

import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Recipe context that is provided when an actual botany pot is available.
 *
 * @param pot    The botany pot that is available.
 * @param player An optional player that is relevant to the current context.
 * @param hand   The hand being used by the player, if one was used.
 */
public record BlockEntityContext(BotanyPotBlockEntity pot, @Nullable Player player, @Nullable InteractionHand hand) implements BotanyPotContext {

    @NotNull
    @Override
    public ItemStack getItem(int slotId) {
        return pot.getItem(slotId);
    }

    @Override
    public int size() {
        return pot.getContainerSize();
    }

    @Override
    public ItemStack getSoilItem() {
        return pot.getSoilItem();
    }

    @Override
    public ItemStack getSeedItem() {
        return pot.getSeedItem();
    }

    @Override
    public ItemStack getHarvestItem() {
        return pot.getHarvestItem();
    }

    @Override
    public LootParams createLootParams(@Nullable BlockState state) {
        if (this.pot.getLevel() instanceof ServerLevel level) {
            final LootParams.Builder paramBuilder = new LootParams.Builder(level);
            paramBuilder.withParameter(LootContextParams.BLOCK_STATE, state != null ? state : level.getBlockState(this.pot.getBlockPos()));
            paramBuilder.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.pot.getBlockPos()));
            paramBuilder.withParameter(LootContextParams.TOOL, this.getInteractionItem());
            paramBuilder.withOptionalParameter(LootContextParams.THIS_ENTITY, this.player);
            paramBuilder.withOptionalParameter(LootContextParams.BLOCK_ENTITY, this.pot);
            paramBuilder.withOptionalParameter(LootContextParams.TOOL, this.pot.getHarvestItem().isEmpty() ? BotanyPotsMod.CONFIG.get().gameplay.default_harvest_tool.apply(level) : this.pot.getHarvestItem());
            return paramBuilder.create(LootContextParamSets.BLOCK);
        }
        throw new IllegalStateException("Can not create LootParams on the client!");
    }

    @Override
    public void runFunction(ResourceLocation functionId) {
        this.pot.runFunction(functionId);
    }

    @Override
    public @Nullable Player getPlayer() {
        return this.player;
    }

    @Override
    public ItemStack getInteractionItem() {
        return (this.player != null && this.hand != null) ? this.player.getItemInHand(this.hand) : ItemStack.EMPTY;
    }

    @Override
    public int getRequiredGrowthTicks() {
        return this.pot.getRequiredGrowthTicks();
    }

    @Override
    public boolean isServerThread() {
        Level level = this.pot.getLevel();
        if (level == null && this.player != null) {
            level = this.player.level();
        }
        return level != null && !level.isClientSide;
    }
}