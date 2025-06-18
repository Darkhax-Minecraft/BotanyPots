package net.darkhax.botanypots.common.impl;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.api.data.codecs.stream.StreamCodecs;
import net.darkhax.bookshelf.common.api.data.enchantment.EnchantmentLevel;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.bookshelf.common.api.util.MathsHelper;
import net.darkhax.botanypots.common.api.context.BlockEntityContext;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.block.BotanyPotBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class Helpers {

    public static final Codec<Block> BLOCK_CODEC = ResourceLocation.CODEC.xmap(BuiltInRegistries.BLOCK::get, BuiltInRegistries.BLOCK::getKey);
    public static final StreamCodec<ByteBuf, Block> BLOCK_STREAM = ResourceLocation.STREAM_CODEC.map(BuiltInRegistries.BLOCK::get, BuiltInRegistries.BLOCK::getKey);
    public static final Codec<Item> ITEM_CODEC = ResourceLocation.CODEC.xmap(BuiltInRegistries.ITEM::get, BuiltInRegistries.ITEM::getKey);
    public static final StreamCodec<ByteBuf, Item> ITEM_STREAM = ResourceLocation.STREAM_CODEC.map(BuiltInRegistries.ITEM::get, BuiltInRegistries.ITEM::getKey);
    public static final Codec<EntityType<?>> ENTITY_TYPE_CODEC = ResourceLocation.CODEC.xmap(BuiltInRegistries.ENTITY_TYPE::get, BuiltInRegistries.ENTITY_TYPE::getKey);
    public static final StreamCodec<ByteBuf, EntityType<?>> ENTITY_TYPE_STREAM = ResourceLocation.STREAM_CODEC.map(BuiltInRegistries.ENTITY_TYPE::get, BuiltInRegistries.ENTITY_TYPE::getKey);
    public static final StreamCodec<ByteBuf, BlockState> BLOCK_STATE_STREAM = ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<Ingredient>> OPTIONAL_INGREDIENT_STREAM = DataHelper.optionalStream(StreamCodecs.INGREDIENT_NON_EMPTY);
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<ItemStack>> OPTIONAL_ITEMSTACK_STREAM = DataHelper.optionalStream(ItemStack.STREAM_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<BlockPredicate>> OPTIONAL_BLOCK_PREDICATE = DataHelper.optionalStream(BlockPredicate.STREAM_CODEC);
    public static final StreamCodec<ByteBuf, TagKey<Block>> BLOCK_TAG = ResourceLocation.STREAM_CODEC.map(rl -> TagKey.create(Registries.BLOCK, rl), TagKey::location);
    public static final TagKey<Enchantment> INCREASE_POT_GROWTH_TAG = TagKey.create(Registries.ENCHANTMENT, BotanyPotsMod.id("increase_pot_growth"));
    public static final TagKey<Enchantment> NEGATE_HARVEST_DAMAGE_TAG = TagKey.create(Registries.ENCHANTMENT, BotanyPotsMod.id("negate_harvest_damage"));

    public static final Supplier<Holder<Attribute>> GROWTH_MOD_ATTRIBUTE = CachedSupplier.cache(() -> BuiltInRegistries.ATTRIBUTE.getHolder(BotanyPotsMod.id("growth")).orElseThrow());
    public static final String GROWTH_MODIFIER_KEY = "tooltip.botanypots.growth_modifier";
    public static final Supplier<Holder<Attribute>> YIELD_MOD_ATTRIBUTE = CachedSupplier.cache(() -> BuiltInRegistries.ATTRIBUTE.getHolder(BotanyPotsMod.id("yield")).orElseThrow());
    public static final String YIELD_MODIFIER_KEY = "tooltip.botanypots.yield_modifier";


    public static Component modifierComponent(String key, float modifier) {
        if (modifier > 0) {
            return Component.translatable(key, "+" + MathsHelper.DECIMAL_2.format(modifier * 100d) + "%").withStyle(ChatFormatting.BLUE);
        }
        return Component.translatable(key, "-" + MathsHelper.DECIMAL_2.format(modifier * 100d) + "%").withStyle(ChatFormatting.RED);
    }

    public static Component indent(Component base) {
        return Component.literal("  ").append(base);
    }

    public static int getRequiredGrowthTicks(BotanyPotContext context, Level level, Crop crop, @Nullable Soil soil) {
        final float cropTime = crop.getRequiredGrowthTicks(context, level);
        float growthModifier = BotanyPotsMod.CONFIG.get().gameplay.global_growth_modifier;
        growthModifier += soil != null ? soil.getGrowthModifier(context, level) : 0f;
        growthModifier += efficiencyModifier(level.registryAccess(), context.getHarvestItem());
        if (context instanceof BlockEntityContext beContext && beContext.pot().getBlockState().getBlock() instanceof BotanyPotBlock potBlock) {
            growthModifier += potBlock.getGrowthModifier(context, level, crop, soil);
        }
        return Mth.floor(cropTime / growthModifier);
    }

    public static float getTotalYield(BotanyPotContext context, Level level, Crop crop, @Nullable Soil soil) {
        final float yieldScale = crop.getYieldScale(context, level);
        float yield = crop.getBaseYield(context, level);
        if (context instanceof BlockEntityContext beContext && beContext.pot().getBlockState().getBlock() instanceof BotanyPotBlock potBlock) {
            yield += (yieldScale * potBlock.getYieldModifier(context, level, crop, soil));
        }
        if (soil != null) {
            yield += (yieldScale * soil.getYieldModifier(context, level));
        }
        yield += (yieldScale * (float) getAttributeValue(YIELD_MOD_ATTRIBUTE.get(), context.getHarvestItem(), 0f));
        return yield;
    }

    public static int getLootRolls(BotanyPotContext context, Level level, Crop crop, @Nullable Soil soil) {
        return determineRollCount(getTotalYield(context, level, crop, soil), level.getRandom());
    }

    /**
     * Calculates the amount of rolls that should take place. When chance is over 100% there will be a guaranteed roll
     * for each multiple of 100% plus an additional random chance using the remainder. The amount of rolls determined
     * will never be negative.
     * <p>
     * For example, if the chance is 550% the user will have 5 guaranteed rolls and a 50% chance for a sixth roll.
     *
     * @param chance The chance for rolls to happen.
     * @param rng    An RNG source used to calculate percentages.
     * @return The amount of rolls that was determined. Can never be negative.
     */
    public static int determineRollCount(float chance, RandomSource rng) {
        int guaranteedRolls = (int) Math.floor(chance);
        final float remainingChance = chance % 1f;
        if (rng.nextFloat() < remainingChance) {
            guaranteedRolls++;
        }
        return Math.max(0, guaranteedRolls);
    }

    public static int chanceToInt(float chance) {
        return (int) Math.floor(chance * 100f);
    }

    public static MutableComponent withScale(MutableComponent base, float scale) {
        if (scale != 1f) {
            base.append(" (").append(Component.translatable("tooltip.botanypots.percent", chanceToInt(scale)).withStyle(scale > 1f ? ChatFormatting.GREEN : ChatFormatting.RED)).append(")");
        }
        return base;
    }

    public static float efficiencyModifier(RegistryAccess registryAccess, ItemStack stack) {
        float modifier = 0f;
        modifier += EnchantmentLevel.HIGHEST.get(INCREASE_POT_GROWTH_TAG, stack) * BotanyPotsMod.CONFIG.get().gameplay.efficiency_growth_modifier;
        modifier += (float) getAttributeValue(GROWTH_MOD_ATTRIBUTE.get(), stack, 0f);
        return modifier;
    }

    public static double getAttributeValue(Holder<Attribute> attribute, ItemStack stack, float defaultValue) {
        double value = 0d;
        final ItemAttributeModifiers modifiers = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (entry.attribute() == attribute) {
                final double entryAmount = entry.modifier().amount();
                value += switch (entry.modifier().operation()) {
                    case ADD_VALUE -> entryAmount;
                    case ADD_MULTIPLIED_BASE -> entryAmount * defaultValue;
                    case ADD_MULTIPLIED_TOTAL -> entryAmount * value;
                };
            }
        }
        return value;
    }
}