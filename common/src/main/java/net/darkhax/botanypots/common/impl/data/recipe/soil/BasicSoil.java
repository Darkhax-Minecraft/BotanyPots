package net.darkhax.botanypots.common.impl.data.recipe.soil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.stream.StreamCodecs;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.api.data.recipes.CacheableRecipe;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.Helpers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BasicSoil extends Soil implements CacheableRecipe {

    public static final MapCodec<BasicSoil> CODEC = Properties.CODEC.xmap(BasicSoil::new, BasicSoil::getProperties);
    public static final StreamCodec<RegistryFriendlyByteBuf, BasicSoil> STREAM = Properties.STREAM.map(BasicSoil::new, BasicSoil::getProperties);
    public static final RecipeSerializer<BasicSoil> SERIALIZER = DataHelper.recipeSerializer(CODEC, STREAM);

    private final Properties properties;

    public BasicSoil(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public boolean matches(@NotNull BotanyPotContext input, @NotNull Level level) {
        return this.properties.input.test(input.getSoilItem());
    }

    @Override
    public boolean canBeCached() {
        return true;
    }

    @Override
    public boolean isCacheKey(ItemStack stack) {
        return this.properties.input.test(stack);
    }

    @Override
    public float getGrowthModifier(@NotNull BotanyPotContext context, @NotNull Level level) {
        return this.properties.growthModifier;
    }

    @Override
    public int getLightLevel(@NotNull BotanyPotContext context, @NotNull Level level) {
        return this.properties.lightLevel;
    }

    @Override
    public Display getDisplay(BotanyPotContext context, @NotNull Level level) {
        return this.properties.display;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public void hoverTooltip(ItemStack stack, BotanyPotContext context, Level level, Consumer<Component> tooltipLines) {
        if (this.properties.growthModifier != 0f) {
            tooltipLines.accept(Helpers.growthModifierComponent(this.properties.growthModifier));
        }
    }

    @Override
    public boolean couldMatch(ItemStack candidate, BotanyPotContext context, Level level) {
        return this.properties.input.test(candidate);
    }

    public record Properties(Ingredient input, Display display, float growthModifier, int lightLevel) {
        public static final MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(Properties::input),
                DisplayType.DISPLAY_STATE_CODEC.fieldOf("display").forGetter(Properties::display),
                Codec.FLOAT.optionalFieldOf("growth_modifier", 0f).forGetter(Properties::growthModifier),
                Codec.INT.optionalFieldOf("light_level", 0).forGetter(Properties::lightLevel)
        ).apply(instance, Properties::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Properties> STREAM = StreamCodec.composite(
                StreamCodecs.INGREDIENT_NON_EMPTY, Properties::input,
                DisplayType.DISPLAY_STATE_STREAM, Properties::display,
                ByteBufCodecs.FLOAT, Properties::growthModifier,
                ByteBufCodecs.INT, Properties::lightLevel,
                Properties::new
        );
    }
}