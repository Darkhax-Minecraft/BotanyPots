package net.darkhax.botanypots.common.impl.data.recipe.interaction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.stream.StreamCodecs;
import net.darkhax.bookshelf.common.api.service.Services;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.botanypots.common.api.data.SoundEffect;
import net.darkhax.botanypots.common.api.data.context.BlockEntityContext;
import net.darkhax.botanypots.common.api.data.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.recipes.CacheableRecipe;
import net.darkhax.botanypots.common.api.data.recipes.interaction.PotInteraction;
import net.darkhax.botanypots.common.impl.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class BasicPotInteraction extends PotInteraction implements CacheableRecipe {

    public static final MapCodec<BasicPotInteraction> CODEC = Properties.CODEC.xmap(BasicPotInteraction::new, BasicPotInteraction::getProperties);
    public static final StreamCodec<RegistryFriendlyByteBuf, BasicPotInteraction> STREAM = Properties.STREAM.map(BasicPotInteraction::new, BasicPotInteraction::getProperties);
    public static final RecipeSerializer<BasicPotInteraction> SERIALIZER = DataHelper.recipeSerializer(CODEC, STREAM);

    public final Properties properties;

    public BasicPotInteraction(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public void apply(@NotNull BotanyPotContext context) {
        if (context instanceof BlockEntityContext input && input.player() instanceof ServerPlayer player && player.level() instanceof ServerLevel level) {
            this.properties.newSoil.ifPresent(soilStack -> {
                Services.GAMEPLAY.dropRemainders(level, input.pot().getBlockPos(), input.getSoilItem());
                input.pot().setSoilItem(soilStack.copy());
            });
            this.properties.newSeed.ifPresent(seedStack -> {
                Services.GAMEPLAY.dropRemainders(level, input.pot().getBlockPos(), input.getSoilItem());
                input.pot().setSeed(seedStack.copy());
            });
            final ItemStack usedItem = input.getInteractionItem();
            if (!usedItem.isEmpty()) {
                if (this.properties.damageHeld) {
                    usedItem.hurtAndBreak(1, player, input.hand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                }
                else if (this.properties.consumeHeld) {
                    Services.GAMEPLAY.dropRemainders(level, input.pot().getBlockPos(), usedItem.split(1));
                }
            }
            this.properties.extraDrops.ifPresent(tableKey -> {
                final LootTable table = level.getServer().reloadableRegistries().getLootTable(tableKey);
                final BlockPos pos = input.pot().getBlockPos();
                if (table != LootTable.EMPTY) {
                    table.getRandomItems(input.createLootParams(null)).forEach(drop -> Block.popResource(level, pos, drop));
                }
            });
            this.properties.soundEffect.ifPresent(soundEffect -> soundEffect.playSound(level, null, input.pot().getBlockPos()));
            if (this.properties.notifySculk) {
                level.gameEvent(context.getPlayer(), GameEvent.BLOCK_CHANGE, input.pot().getBlockPos());
            }
        }
    }

    @Override
    public boolean matches(@NotNull BotanyPotContext input, @NotNull Level level) {
        return this.properties.validateInputs(input.getInteractionItem(), input.getSoilItem(), input.getSeedItem());
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public boolean couldMatch(ItemStack candidate, BotanyPotContext context, Level level) {
        return this.properties.validateInputs(candidate, context.getSoilItem(), context.getSeedItem());
    }

    @Override
    public boolean canBeCached() {
        return true;
    }

    @Override
    public boolean isCacheKey(ItemStack stack) {
        return this.properties.heldTest.test(stack);
    }

    public record Properties(Ingredient heldTest, boolean damageHeld, boolean consumeHeld, Optional<Ingredient> soilTest, Optional<Ingredient> seedTest, Optional<ItemStack> newSoil, Optional<ItemStack> newSeed, Optional<ResourceKey<LootTable>> extraDrops, Optional<SoundEffect> soundEffect, boolean notifySculk) {
        public static final MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("held_item").forGetter(Properties::heldTest),
                Codec.BOOL.optionalFieldOf("damage_held", true).forGetter(Properties::damageHeld),
                Codec.BOOL.optionalFieldOf("consume_held", false).forGetter(Properties::consumeHeld),
                Ingredient.CODEC.optionalFieldOf("soil_item").forGetter(Properties::soilTest),
                Ingredient.CODEC.optionalFieldOf("seed_item").forGetter(Properties::seedTest),
                ItemStack.CODEC.optionalFieldOf("new_soil").forGetter(Properties::newSoil),
                ItemStack.CODEC.optionalFieldOf("new_seed").forGetter(Properties::newSeed),
                ResourceLocation.CODEC.optionalFieldOf("extra_drops").xmap(rl -> rl.map(arl -> ResourceKey.create(Registries.LOOT_TABLE, arl)), arl -> arl.map(ResourceKey::location)).forGetter(Properties::extraDrops),
                SoundEffect.CODEC.codec().optionalFieldOf("sound_effect").forGetter(Properties::soundEffect),
                Codec.BOOL.optionalFieldOf("notify_sculk", true).forGetter(Properties::notifySculk)
        ).apply(instance, Properties::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Properties> STREAM = new StreamCodec<>() {
            @NotNull
            @Override
            public Properties decode(@NotNull RegistryFriendlyByteBuf buf) {
                final Ingredient ingredient = StreamCodecs.INGREDIENT_NON_EMPTY.decode(buf);
                final boolean damageHeld = buf.readBoolean();
                final boolean consumeHeld = buf.readBoolean();
                final Optional<Ingredient> soilTest = Helpers.OPTIONAL_INGREDIENT_STREAM.decode(buf);
                final Optional<Ingredient> seedTest = Helpers.OPTIONAL_INGREDIENT_STREAM.decode(buf);
                final Optional<ItemStack> newSoil = Helpers.OPTIONAL_ITEMSTACK_STREAM.decode(buf);
                final Optional<ItemStack> newSeed = Helpers.OPTIONAL_ITEMSTACK_STREAM.decode(buf);
                final Optional<ResourceKey<LootTable>> extraDrops = buf.readOptional(ResourceLocation.STREAM_CODEC).map(rl -> ResourceKey.create(Registries.LOOT_TABLE, rl));
                final Optional<SoundEffect> soundEffect = SoundEffect.OPTIONAL_STREAM.decode(buf);
                final boolean notifySculk = buf.readBoolean();
                return new Properties(ingredient, damageHeld, consumeHeld, soilTest, seedTest, newSoil, newSeed, extraDrops, soundEffect, notifySculk);
            }

            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull Properties properties) {
                StreamCodecs.INGREDIENT_NON_EMPTY.encode(buf, properties.heldTest);
                buf.writeBoolean(properties.damageHeld);
                buf.writeBoolean(properties.consumeHeld);
                Helpers.OPTIONAL_INGREDIENT_STREAM.encode(buf, properties.soilTest);
                Helpers.OPTIONAL_INGREDIENT_STREAM.encode(buf, properties.seedTest);
                Helpers.OPTIONAL_ITEMSTACK_STREAM.encode(buf, properties.newSoil);
                Helpers.OPTIONAL_ITEMSTACK_STREAM.encode(buf, properties.newSeed);
                buf.writeOptional(properties.extraDrops, (b, v) -> b.writeResourceLocation(v.location()));
                SoundEffect.OPTIONAL_STREAM.encode(buf, properties.soundEffect);
                buf.writeBoolean(properties.notifySculk);
            }
        };

        public boolean validateInputs(ItemStack held, ItemStack soil, ItemStack seed) {
            return this.heldTest.test(held) && (this.soilTest.isEmpty() || this.soilTest.get().test(soil)) && (this.seedTest.isEmpty() || this.seedTest.get().test(seed));
        }
    }
}