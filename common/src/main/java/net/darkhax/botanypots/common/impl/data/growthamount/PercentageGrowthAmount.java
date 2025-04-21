package net.darkhax.botanypots.common.impl.data.growthamount;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

public record PercentageGrowthAmount(float percentage) implements GrowthAmount {

    public static final ResourceLocation ID = BotanyPotsMod.id("percent");
    public static final Supplier<GrowthAmountType<?>> TYPE = GrowthAmountType.getLazy(ID);
    public static final MapCodec<PercentageGrowthAmount> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.floatRange(0f, 1f).fieldOf("amount").forGetter(PercentageGrowthAmount::percentage)).apply(instance, PercentageGrowthAmount::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, PercentageGrowthAmount> STREAM = StreamCodec.composite(ByteBufCodecs.FLOAT, PercentageGrowthAmount::percentage, PercentageGrowthAmount::new);

    @Override
    public int getAmount(@NotNull BotanyPotContext context, @NotNull Level level) {
        final int requiredTicks = context.getRequiredGrowthTicks();
        return Math.max(0, (int) Math.floor(requiredTicks * percentage));
    }

    @Override
    public GrowthAmountType<?> getType() {
        return TYPE.get();
    }
}
