package net.darkhax.botanypots.common.impl.data.itemdrops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.loot.LootPoolEntryDescriptions;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class LootTableDrops implements ItemDropProvider {

    public static final MapCodec<LootTableDrops> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("table_id").forGetter(LootTableDrops::getTableId)
    ).apply(instance, LootTableDrops::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LootTableDrops> STREAM = StreamCodec.of(
            (buf, val) -> {
                buf.writeResourceLocation(val.tableId);
                ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buf, val.displayItems.get());
            },
            buf -> {
                final ResourceLocation tableId = buf.readResourceLocation();
                final List<ItemStack> items = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buf);
                return new LootTableDrops(tableId, items);
            }
    );

    private final ResourceLocation tableId;
    private final CachedSupplier<LootTable> cachedTable;
    private final CachedSupplier<List<ItemStack>> displayItems;

    // Server
    public LootTableDrops(ResourceLocation tableId) {
        this.tableId = tableId;
        this.cachedTable = CachedSupplier.cache(() -> Objects.requireNonNull(BotanyPotsMod.REGISTRY_ACCESS.get()).registryOrThrow(Registries.LOOT_TABLE).get(ResourceKey.create(Registries.LOOT_TABLE, tableId)));
        this.displayItems = CachedSupplier.cache(() -> LootPoolEntryDescriptions.getUniqueItems(Objects.requireNonNull(BotanyPotsMod.REGISTRY_ACCESS.get()), this.cachedTable.get()));
    }

    // client
    public LootTableDrops(ResourceLocation tableId, List<ItemStack> displayItems) {
        this.tableId = tableId;
        this.cachedTable = CachedSupplier.singleton(null);
        this.displayItems = CachedSupplier.singleton(displayItems);
    }

    public ResourceLocation getTableId() {
        return this.tableId;
    }

    protected LootParams getLootParams(BotanyPotContext context) {
        return context.createLootParams(null);
    }

    @Override
    public void apply(BotanyPotContext context, Level level, Consumer<ItemStack> drops) {
        this.cachedTable.ifPresent(table -> table.getRandomItems(this.getLootParams(context), drops));
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return ItemDropProviderType.LOOT_TABLE;
    }

    @Override
    public final List<ItemStack> getDisplayItems() {
        return this.displayItems.get();
    }
}
