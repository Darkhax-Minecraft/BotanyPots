package net.darkhax.botanypots.common.impl.data.recipe.fertilizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.stream.StreamCodecs;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.bookshelf.common.api.util.FunctionHelper;
import net.darkhax.bookshelf.common.api.util.MathsHelper;
import net.darkhax.botanypots.common.api.data.SoundEffect;
import net.darkhax.botanypots.common.api.data.context.BlockEntityContext;
import net.darkhax.botanypots.common.api.data.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.recipes.CacheableRecipe;
import net.darkhax.botanypots.common.api.data.recipes.fertilizer.Fertilizer;
import net.darkhax.botanypots.common.impl.Helpers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class BasicFertilizer extends Fertilizer implements CacheableRecipe {

    public static final MapCodec<BasicFertilizer> CODEC = Properties.CODEC.xmap(BasicFertilizer::new, BasicFertilizer::getProperties);
    public static final StreamCodec<RegistryFriendlyByteBuf, BasicFertilizer> STREAM = Properties.STREAM.map(BasicFertilizer::new, BasicFertilizer::getProperties);
    public static final RecipeSerializer<BasicFertilizer> SERIALIZER = DataHelper.recipeSerializer(CODEC, STREAM);

    private final Properties properties;

    public BasicFertilizer(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public void apply(@NotNull BotanyPotContext input, @NotNull Level level) {
        if (input instanceof BlockEntityContext context && context.pot().canBonemeal() && level instanceof ServerLevel sLevel) {
            final int requiredGrowthTicks = context.getRequiredGrowthTicks();
            final int maxBonemealableTicks = requiredGrowthTicks - 20;
            if (requiredGrowthTicks > 20 && context.pot().growthTime.getTicks() < maxBonemealableTicks) {
                context.pot().growthTime.setTicks(Math.min(context.pot().growthTime.getTicks() + MathsHelper.nextInt(sLevel.random, properties.minGrowth, properties.maxGrowth), maxBonemealableTicks));
                context.pot().setBonemealCooldown(properties.cooldown);
                if (properties.spawnsParticles) {
                    level.levelEvent(LevelEvent.PARTICLES_BEE_GROWTH, context.pot().getBlockPos(), 15);
                }
                if (properties.notifySculk) {
                    level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, context.pot().getBlockPos());
                }
                if (!Objects.requireNonNull(context.player()).isCreative()) {
                    context.getInteractionItem().shrink(1);
                }
                this.properties.soundEffect.ifPresent(soundEffect -> soundEffect.playSound(sLevel, null, context.pot().getBlockPos()));
                context.pot().markUpdated();
            }
        }
    }

    @Override
    public boolean matches(@NotNull BotanyPotContext input, @NotNull Level level) {
        return properties.heldItem.test(input.getInteractionItem()) && FunctionHelper.test(this.properties.soilIngredient, i -> i.test(input.getSoilItem())) && FunctionHelper.test(this.properties.seedIngredient, i -> i.test(input.getSeedItem()));
    }

    @Override
    public boolean canBeCached() {
        return true;
    }

    @Override
    public boolean isCacheKey(ItemStack stack) {
        return this.properties.heldItem.test(stack);
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean couldMatch(ItemStack candidate, BotanyPotContext context, Level level) {
        return this.properties.heldItem.test(candidate);
    }

    public record Properties(Ingredient heldItem, Optional<Ingredient> soilIngredient, Optional<Ingredient> seedIngredient, int minGrowth, int maxGrowth, int cooldown, boolean spawnsParticles, boolean notifySculk, Optional<SoundEffect> soundEffect) {
        public static final MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("held_item").forGetter(Properties::heldItem),
                Ingredient.CODEC.optionalFieldOf("soil_item").forGetter(Properties::soilIngredient),
                Ingredient.CODEC.optionalFieldOf("seed_item").forGetter(Properties::seedIngredient),
                Codec.INT.optionalFieldOf("min_growth", 400).forGetter(Properties::minGrowth),
                Codec.INT.optionalFieldOf("max_growth", 800).forGetter(Properties::maxGrowth),
                Codec.INT.optionalFieldOf("cooldown", 20).forGetter(Properties::cooldown),
                Codec.BOOL.optionalFieldOf("spawn_particles", true).forGetter(Properties::spawnsParticles),
                Codec.BOOL.optionalFieldOf("notify_sculk", true).forGetter(Properties::notifySculk),
                SoundEffect.CODEC.codec().optionalFieldOf("sound_effect").forGetter(Properties::soundEffect)
        ).apply(instance, Properties::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Properties> STREAM = new StreamCodec<>() {
            @NotNull
            @Override
            public Properties decode(@NotNull RegistryFriendlyByteBuf buf) {
                final Ingredient heldItem = StreamCodecs.INGREDIENT_NON_EMPTY.decode(buf);
                final Optional<Ingredient> soil = Helpers.OPTIONAL_INGREDIENT_STREAM.decode(buf);
                final Optional<Ingredient> seed = Helpers.OPTIONAL_INGREDIENT_STREAM.decode(buf);
                final int min = ByteBufCodecs.INT.decode(buf);
                final int max = ByteBufCodecs.INT.decode(buf);
                final int cooldown = ByteBufCodecs.INT.decode(buf);
                final boolean particles = buf.readBoolean();
                final boolean sculk = buf.readBoolean();
                final Optional<SoundEffect> soundEffect = SoundEffect.OPTIONAL_STREAM.decode(buf);
                return new Properties(heldItem, soil, seed, min, max, cooldown, particles, sculk, soundEffect);
            }

            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull Properties properties) {
                StreamCodecs.INGREDIENT_NON_EMPTY.encode(buf, properties.heldItem);
                Helpers.OPTIONAL_INGREDIENT_STREAM.encode(buf, properties.soilIngredient);
                Helpers.OPTIONAL_INGREDIENT_STREAM.encode(buf, properties.seedIngredient);
                ByteBufCodecs.INT.encode(buf, properties.minGrowth);
                ByteBufCodecs.INT.encode(buf, properties.maxGrowth);
                ByteBufCodecs.INT.encode(buf, properties.cooldown);
                buf.writeBoolean(properties.spawnsParticles);
                buf.writeBoolean(properties.notifySculk);
                SoundEffect.OPTIONAL_STREAM.encode(buf, properties.soundEffect);
            }
        };
    }
}
