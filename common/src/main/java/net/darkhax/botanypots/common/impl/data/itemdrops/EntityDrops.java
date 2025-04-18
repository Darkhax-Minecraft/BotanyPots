package net.darkhax.botanypots.common.impl.data.itemdrops;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.function.ReloadableCache;
import net.darkhax.bookshelf.common.api.loot.LootPoolEntryDescriptions;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityDrops implements ItemDropProvider {

    public static final Supplier<ItemDropProviderType<?>> TYPE = ItemDropProviderType.getLazy(BotanyPotsMod.id("entity"));
    public static final MapCodec<EntityDrops> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CompoundTag.CODEC.fieldOf("entity").forGetter(EntityDrops::getEntityData),
            DamageType.CODEC.optionalFieldOf("damage_source").forGetter(EntityDrops::getDamageType)
    ).apply(instance, EntityDrops::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityDrops> STREAM = StreamCodec.of(
            (buf, value) -> {
                ByteBufCodecs.COMPOUND_TAG.encode(buf, value.entityData);
                if (value.damageType.isPresent()) {
                    buf.writeBoolean(true);
                    DamageType.STREAM_CODEC.encode(buf, value.damageType.get());
                }
                else {
                    buf.writeBoolean(false);
                }
                buf.writeResourceKey(value.lootTableId.get());
                ItemStack.OPTIONAL_LIST_STREAM_CODEC.encode(buf, value.getDisplayItems());
            },
            buf -> {
                final CompoundTag entityData = ByteBufCodecs.COMPOUND_TAG.decode(buf);
                final boolean hasDamageType = buf.readBoolean();
                final Optional<Holder<DamageType>> damageType = hasDamageType ? Optional.of(DamageType.STREAM_CODEC.decode(buf)) : Optional.empty();
                final ResourceKey<LootTable> tableId = buf.readResourceKey(Registries.LOOT_TABLE);
                final List<ItemStack> displayItems = ItemStack.OPTIONAL_LIST_STREAM_CODEC.decode(buf);
                return new EntityDrops(entityData, damageType, tableId, displayItems);
            }
    );

    private final CompoundTag entityData;
    private final Optional<Holder<DamageType>> damageType;
    private final ReloadableCache<LivingEntity> entity;
    private final CachedSupplier<ResourceKey<LootTable>> lootTableId;
    private final CachedSupplier<List<ItemStack>> displayItems;

    // Server
    public EntityDrops(CompoundTag entityData, Optional<Holder<DamageType>> damageType) {
        this.entityData = entityData;
        this.damageType = damageType;
        this.entity = ReloadableCache.living(entityData);
        this.lootTableId = CachedSupplier.cache(() -> {
            if (entityData.contains("DeathLootTable", Tag.TAG_STRING)) {
                return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(entityData.getString("DeathLootTable")));
            }
            else if (entityData.contains("id", Tag.TAG_STRING)) {
                final ResourceLocation entityId = ResourceLocation.tryParse(entityData.getString("id"));
                if (entityId != null && BuiltInRegistries.ENTITY_TYPE.containsKey(entityId)) {
                    return BuiltInRegistries.ENTITY_TYPE.get(entityId).getDefaultLootTable();
                }
            }
            return BuiltInLootTables.EMPTY;
        });
        this.displayItems = CachedSupplier.cache(() -> {
            final LootTable table = Objects.requireNonNull(BotanyPotsMod.REGISTRY_ACCESS.get()).registryOrThrow(Registries.LOOT_TABLE).get(this.lootTableId.get());
            return table != null ? LootPoolEntryDescriptions.getUniqueItems(Objects.requireNonNull(BotanyPotsMod.REGISTRY_ACCESS.get()), table) : List.of();
        });
    }

    // Client
    public EntityDrops(CompoundTag entityData, Optional<Holder<DamageType>> damageType, ResourceKey<LootTable> tableId, List<ItemStack> displayItems) {
        this.entityData = entityData;
        this.damageType = damageType;
        this.entity = ReloadableCache.living(entityData);
        this.lootTableId = CachedSupplier.singleton(tableId);
        this.displayItems = CachedSupplier.singleton(displayItems);
    }

    public CompoundTag getEntityData() {
        return this.entityData;
    }

    public Optional<Holder<DamageType>> getDamageType() {
        return this.damageType;
    }

    @Override
    public void apply(BotanyPotContext context, Level level, Consumer<ItemStack> drops) {
        if (level instanceof ServerLevel serverLevel) {
            this.entity.ifPresent(level, living -> {
                final ResourceKey<LootTable> resourcekey = living.getLootTable();
                final LootTable loottable = serverLevel.getServer().reloadableRegistries().getLootTable(resourcekey);
                final LootParams.Builder paramBuilder = new LootParams.Builder(serverLevel);
                paramBuilder.withParameter(LootContextParams.THIS_ENTITY, living);
                paramBuilder.withParameter(LootContextParams.ORIGIN, living.position());
                paramBuilder.withParameter(LootContextParams.DAMAGE_SOURCE, this.damageType.map(DamageSource::new).orElseGet(() -> serverLevel.damageSources().generic()));
                loottable.getRandomItems(paramBuilder.create(LootContextParamSets.ENTITY), living.getLootTableSeed(), drops);
            });
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return TYPE.get();
    }

    @Override
    public List<ItemStack> getDisplayItems() {
        return this.displayItems.get();
    }
}