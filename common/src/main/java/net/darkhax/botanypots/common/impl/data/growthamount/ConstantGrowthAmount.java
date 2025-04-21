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

public record ConstantGrowthAmount(int amount) implements GrowthAmount {

    public static final ResourceLocation ID = BotanyPotsMod.id("constant");
    public static final Supplier<GrowthAmountType<?>> TYPE = GrowthAmountType.getLazy(ID);
    public static final MapCodec<ConstantGrowthAmount> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.intRange(0, Integer.MAX_VALUE).fieldOf("amount").forGetter(ConstantGrowthAmount::amount)).apply(instance, ConstantGrowthAmount::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ConstantGrowthAmount> STREAM = StreamCodec.composite(ByteBufCodecs.INT, ConstantGrowthAmount::amount, ConstantGrowthAmount::new);

    @Override
    public int getAmount(@NotNull BotanyPotContext context, @NotNull Level level) {
        return amount;
    }

    @Override
    public GrowthAmountType<?> getType() {
        return TYPE.get();
    }
}
