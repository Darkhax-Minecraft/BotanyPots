package net.darkhax.botanypots.common.impl.data.recipe.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.darkhax.bookshelf.common.api.data.codecs.stream.StreamCodecs;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.botanypots.common.api.context.BlockEntityContext;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.darkhax.botanypots.common.api.data.recipes.CacheableRecipe;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.Helpers;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BasicCrop extends Crop implements CacheableRecipe {

    public static final Ingredient DIRT = Ingredient.of(TagKey.create(Registries.ITEM, BotanyPotsMod.id("soil/dirt")));
    public static final MapCodec<BasicCrop> CODEC = Properties.CODEC.xmap(BasicCrop::new, BasicCrop::getBasicProperties);
    public static final StreamCodec<RegistryFriendlyByteBuf, BasicCrop> STREAM = Properties.STREAM.map(BasicCrop::new, BasicCrop::getBasicProperties);
    public static final RecipeSerializer<BasicCrop> SERIALIZER = DataHelper.recipeSerializer(CODEC, STREAM);
    public static final Component TOOLTIP_WRONG_SOIL = Component.translatable("tooltip.botanypots.wrong_soil").withStyle(ChatFormatting.RED);

    private final Properties properties;

    public BasicCrop(Properties properties) {
        this.properties = properties;
    }

    public Properties getBasicProperties() {
        return this.properties;
    }

    @Override
    public boolean matches(@NotNull BotanyPotContext input, @NotNull Level level) {
        if (input instanceof BlockEntityContext context && this.properties.potPredicate.isPresent() && !this.properties.potPredicate.get().matches(context.blockInWorld())) {
            return false;
        }
        return this.properties.input.test(input.getSeedItem());
    }

    @Override
    public boolean couldMatch(ItemStack candidate, BotanyPotContext context, Level level) {
        return this.properties.input.test(candidate);
    }

    @Override
    public void onHarvest(BotanyPotContext context, Level level, Consumer<ItemStack> drops) {
        for (ItemDropProvider provider : this.properties.drops) {
            provider.apply(context, level, drops);
        }
        if (level instanceof ServerLevel serverLevel) {
            this.properties.functionId.ifPresent(context::runFunction);
        }
    }

    @Override
    public List<Display> getDisplayState(BotanyPotContext context, Level level) {
        return this.properties.display;
    }

    @Override
    public int getRequiredGrowthTicks(BotanyPotContext context, Level level) {
        return this.properties.growTime;
    }

    @Override
    public boolean isGrowthSustained(BotanyPotContext context, Level level) {
        return this.properties.soil.test(context.getSoilItem());
    }

    @Override
    public boolean canBeCached() {
        return true;
    }

    @Override
    public boolean isCacheKey(ItemStack stack) {
        return properties.input.test(stack);
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public void hoverTooltip(ItemStack stack, BotanyPotContext context, Level level, Consumer<Component> tooltipLines) {
        if (stack == context.getItem(BotanyPotBlockEntity.SEED_SLOT)) {
            if (this.isGrowthSustained(context, level)) {
                tooltipLines.accept(Component.translatable("tooltip.botanypots.growth_time", StringUtil.formatTickDuration(context.getRequiredGrowthTicks(), level.tickRateManager().tickrate())).withStyle(ChatFormatting.GRAY));
            }
            if (!this.properties.soil.test(context.getItem(BotanyPotBlockEntity.SOIL_SLOT))) {
                tooltipLines.accept(TOOLTIP_WRONG_SOIL);
            }
            if (context instanceof BlockEntityContext beContext && this.properties.potPredicate.isPresent() && !this.properties.potPredicate.get().matches(beContext.blockInWorld())) {
                tooltipLines.accept(Component.translatable("tooltip.botanypots.wrong_pot", beContext.pot().getBlockState().getBlock().getName()).withStyle(ChatFormatting.RED));
            }
        }
        else {
            tooltipLines.accept(Component.translatable("tooltip.botanypots.growth_time", StringUtil.formatTickDuration(this.properties.growTime, level.tickRateManager().tickrate())).withStyle(ChatFormatting.GRAY));
        }
    }

    public boolean isValidSoil(ItemStack stack) {
        return this.properties.soil.test(stack);
    }

    public record Properties(Ingredient input, Ingredient soil, int growTime, List<Display> display, int lightLevel, List<ItemDropProvider> drops, Optional<ResourceLocation> functionId, Optional<BlockPredicate> potPredicate) {
        public static final MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(Properties::input),
                Ingredient.CODEC.optionalFieldOf("soil", DIRT).forGetter(Properties::soil),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("grow_time", 1200).forGetter(Properties::growTime),
                DisplayType.LIST_CODEC.fieldOf("display").forGetter(Properties::display),
                Codec.intRange(0, 15).optionalFieldOf("light_level", 0).forGetter(Properties::lightLevel),
                MapCodecs.flexibleList(ItemDropProviderType.DROP_PROVIDER_CODEC).optionalFieldOf("drops", List.of()).forGetter(Properties::drops),
                ResourceLocation.CODEC.optionalFieldOf("function").forGetter(Properties::functionId),
                BlockPredicate.CODEC.optionalFieldOf("pot_predicate").forGetter(Properties::potPredicate)
        ).apply(instance, Properties::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Properties> STREAM = new StreamCodec<>() {
            @NotNull
            @Override
            public Properties decode(@NotNull RegistryFriendlyByteBuf buf) {
                final Ingredient input = StreamCodecs.INGREDIENT_NON_EMPTY.decode(buf);
                final Ingredient soil = StreamCodecs.INGREDIENT_NON_EMPTY.decode(buf);
                final int growTime = ByteBufCodecs.INT.decode(buf);
                final List<Display> display = buf.readList(DisplayType.DISPLAY_STATE_STREAM);
                final int light = ByteBufCodecs.INT.decode(buf);
                final Optional<ResourceLocation> functionId = buf.readOptional(ResourceLocation.STREAM_CODEC);

                final int dropSize = buf.readInt();
                final List<ItemDropProvider> drops = new LinkedList<>();
                for (int i = 0; i < dropSize; i++) {
                    drops.add(ItemDropProviderType.DROP_PROVIDER_STREAM.decode(buf));
                }
                final Optional<BlockPredicate> potPredicate = Helpers.OPTIONAL_BLOCK_PREDICATE.decode(buf);
                return new Properties(input, soil, growTime, display, light, drops, functionId, potPredicate);
            }

            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull Properties properties) {
                StreamCodecs.INGREDIENT_NON_EMPTY.encode(buf, properties.input);
                StreamCodecs.INGREDIENT_NON_EMPTY.encode(buf, properties.soil);
                ByteBufCodecs.INT.encode(buf, properties.growTime);
                buf.writeCollection(properties.display, DisplayType.DISPLAY_STATE_STREAM);
                ByteBufCodecs.INT.encode(buf, properties.lightLevel);
                buf.writeOptional(properties.functionId, ResourceLocation.STREAM_CODEC);
                buf.writeInt(properties.drops.size());
                for (ItemDropProvider provider : properties.drops) {
                    ItemDropProviderType.DROP_PROVIDER_STREAM.encode(buf, provider);
                }
                Helpers.OPTIONAL_BLOCK_PREDICATE.encode(buf, properties.potPredicate);
            }
        };
    }
}