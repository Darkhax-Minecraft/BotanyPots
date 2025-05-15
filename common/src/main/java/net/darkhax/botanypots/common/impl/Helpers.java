package net.darkhax.botanypots.common.impl;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.api.data.codecs.stream.StreamCodecs;
import net.darkhax.bookshelf.common.api.data.enchantment.EnchantmentLevel;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.bookshelf.common.api.util.MathsHelper;
import net.darkhax.botanypots.common.api.context.BlockEntityContext;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.block.BotanyPotBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

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
    public static final TagKey<Enchantment> INCREASE_POT_GROWTH_TAG = TagKey.create(Registries.ENCHANTMENT, BotanyPotsMod.id("increase_pot_growth"));
    public static final TagKey<Enchantment> NEGATE_HARVEST_DAMAGE_TAG = TagKey.create(Registries.ENCHANTMENT, BotanyPotsMod.id("negate_harvest_damage"));

    public static Component growthModifierComponent(float modifier) {
        if (modifier > 0) {
            return Component.translatable("tooltip.botanypots.growth_modifier", "+" + MathsHelper.DECIMAL_2.format(modifier * 100d) + "%").withStyle(ChatFormatting.BLUE);
        }
        return Component.translatable("tooltip.botanypots.growth_modifier", "-" + MathsHelper.DECIMAL_2.format(modifier * 100d) + "%").withStyle(ChatFormatting.RED);
    }

    public static int getRequiredGrowthTicks(BotanyPotContext context, Level level, Crop crop, @Nullable Soil soil) {
        final float cropTime = crop.getRequiredGrowthTicks(context, level);
        float growthModifier = BotanyPotsMod.CONFIG.get().gameplay.global_growth_modifier;
        growthModifier += soil != null ? soil.getGrowthModifier(context, level) : 0f;
        growthModifier += efficiencyModifier(level.registryAccess(), context.getHarvestItem());
        if (context instanceof BlockEntityContext beContext) {
            growthModifier += beContext.pot().getGrowthModifier(context, level, crop, soil);
            if (beContext.pot().getBlockState().getBlock() instanceof BotanyPotBlock potBlock) {
                growthModifier += potBlock.getGrowthModifier(context, level, crop, soil);
            }
        }
        return Mth.floor(cropTime / growthModifier);
    }

    public static float efficiencyModifier(RegistryAccess registryAccess, ItemStack stack) {
        return EnchantmentLevel.HIGHEST.get(INCREASE_POT_GROWTH_TAG, stack) * BotanyPotsMod.CONFIG.get().gameplay.efficiency_growth_modifier;
    }
}