package net.darkhax.botanypots.common.impl.data.itemdrops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.mixin.access.block.AccessorCropBlock;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BlockDrops extends LootTableDrops {

    public static final Supplier<ItemDropProviderType<?>> TYPE = ItemDropProviderType.getLazy(BotanyPotsMod.id("block"));
    public static final MapCodec<BlockDrops> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(BlockDrops::getBlock)
    ).apply(instance, BlockDrops::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockDrops> STREAM = StreamCodec.of(
            (buf, val) -> {
                buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(val.getBlock()));
                buf.writeResourceLocation(val.getTableId());
                ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buf, val.getDisplayItems());
            },
            buf -> {
                final Block block = BuiltInRegistries.BLOCK.get(buf.readResourceLocation());
                final ResourceLocation tableId = buf.readResourceLocation();
                final List<ItemStack> items = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buf);
                return new BlockDrops(block, tableId, items);
            }
    );

    private final Block block;
    private final BlockState harvestState;
    private final CachedSupplier<ItemStack> fallbackDrop = CachedSupplier.cache(this::findFallbackItem);

    // server
    public BlockDrops(Block block) {
        this(block, block.defaultBlockState());
    }

    // server
    public BlockDrops(Block block, BlockState harvestState) {
        super(block.getLootTable().location());
        this.block = block;
        this.harvestState = harvestState;
    }

    // client
    public BlockDrops(Block block, ResourceLocation tableId, List<ItemStack> display) {
        this(block, block.defaultBlockState(), tableId, display);
    }

    // client
    public BlockDrops(Block block, BlockState harvestState, ResourceLocation tableId, List<ItemStack> display) {
        super(tableId, display);
        this.block = block;
        this.harvestState = harvestState;
    }

    public Block getBlock() {
        return this.block;
    }

    public BlockState getHarvestState() {
        return this.harvestState;
    }

    @Override
    protected LootParams getLootParams(BotanyPotContext context) {
        return context.createLootParams(this.harvestState);
    }

    @Override
    public List<ItemStack> buildDisplayItems() {
        return this.hasCachedTable() ? super.buildDisplayItems() : List.of(this.fallbackDrop.get());
    }

    @Override
    public void fallbackDrops(BotanyPotContext context, Level level, Consumer<ItemStack> drops) {
        drops.accept(this.fallbackDrop.get());
    }

    public ItemStack findFallbackItem() {
        if (block instanceof AccessorCropBlock crop) {
            final ItemLike seedItem = crop.bookshelf$getSeed();
            if (seedItem != null && seedItem != Items.AIR) {
                return seedItem.asItem().getDefaultInstance();
            }
        }
        final Item placer = block.asItem();
        if (placer != Items.AIR) {
            return placer.getDefaultInstance();
        }
        BotanyPotsMod.LOG.warn("Can not derive seed from block " + block + " id=" + BuiltInRegistries.BLOCK.getKey(block));
        return ItemStack.EMPTY;
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return TYPE.get();
    }
}