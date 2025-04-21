package net.darkhax.botanypots.common.impl.data.growthamount;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.util.MathsHelper;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.growthamount.GrowthAmount;
import net.darkhax.botanypots.common.api.data.growthamount.GrowthAmountType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record RangedGrowthAmount(int min, int max) implements GrowthAmount {

    public static final ResourceLocation ID = BotanyPotsMod.id("ranged");
    public static final Supplier<GrowthAmountType<?>> TYPE = GrowthAmountType.getLazy(ID);
    public static final MapCodec<RangedGrowthAmount> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("min").forGetter(RangedGrowthAmount::min),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf("max").forGetter(RangedGrowthAmount::max)
    ).apply(instance, RangedGrowthAmount::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, RangedGrowthAmount> STREAM = StreamCodec.composite(ByteBufCodecs.INT, RangedGrowthAmount::min, ByteBufCodecs.INT, RangedGrowthAmount::max, RangedGrowthAmount::new);

    @Override
    public int getAmount(@NotNull BotanyPotContext context, @NotNull Level level) {
        return MathsHelper.nextInt(level.random, this.min, this.max);
    }

    @Override
    public GrowthAmountType<?> getType() {
        return TYPE.get();
    }
}
