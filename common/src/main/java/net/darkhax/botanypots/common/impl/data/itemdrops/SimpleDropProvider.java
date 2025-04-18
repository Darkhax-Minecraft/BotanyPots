package net.darkhax.botanypots.common.impl.data.itemdrops;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.darkhax.bookshelf.common.api.util.MathsHelper;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record SimpleDropProvider(List<SimpleDrop> drops) implements ItemDropProvider {

    public static final Supplier<ItemDropProviderType<?>> TYPE = ItemDropProviderType.getLazy(BotanyPotsMod.id("items"));
    public static final MapCodec<SimpleDropProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MapCodecs.flexibleList(SimpleDrop.CODEC.codec()).fieldOf("items").forGetter(SimpleDropProvider::drops)
    ).apply(instance, SimpleDropProvider::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleDropProvider> STREAM = StreamCodec.of(
            (buffer, value) -> {
                buffer.writeInt(value.drops.size());
                for (SimpleDrop drop : value.drops) {
                    SimpleDrop.STREAM.encode(buffer, drop);
                }
            },
            (buffer) -> {
                final int size = buffer.readInt();
                final List<SimpleDrop> drops = new LinkedList<>();
                for (int i = 0; i < size; i++) {
                    drops.add(SimpleDrop.STREAM.decode(buffer));
                }
                return new SimpleDropProvider(drops);
            });

    @Override
    public void apply(BotanyPotContext context, Level level, Consumer<ItemStack> drops) {
        for (SimpleDrop drop : this.drops) {
            if (MathsHelper.percentChance(drop.chance)) {
                drops.accept(drop.drop().copy());
            }
        }
    }

    @Override
    public ItemDropProviderType<?> getType() {
        return TYPE.get();
    }

    @Override
    public List<ItemStack> getDisplayItems() {
        return this.drops.stream().map(d -> d.drop).collect(Collectors.toList());
    }

    public record SimpleDrop(ItemStack drop, float chance) {
        public static final MapCodec<SimpleDrop> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStack.CODEC.fieldOf("result").forGetter(SimpleDrop::drop),
                Codec.floatRange(0f, 1f).optionalFieldOf("chance", 1f).forGetter(SimpleDrop::chance)
        ).apply(instance, SimpleDrop::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, SimpleDrop> STREAM = StreamCodec.composite(ItemStack.STREAM_CODEC, SimpleDrop::drop, ByteBufCodecs.FLOAT, SimpleDrop::chance, SimpleDrop::new);
    }
}