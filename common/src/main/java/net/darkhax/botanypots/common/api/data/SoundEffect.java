package net.darkhax.botanypots.common.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public record SoundEffect(Holder<SoundEvent> sound, SoundSource source, float volume, float pitch) {

    public static final MapCodec<SoundEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SoundEvent.CODEC.fieldOf("id").forGetter(SoundEffect::sound),
            MapCodecs.enumerable(SoundSource.class).optionalFieldOf("category", SoundSource.MASTER).forGetter(SoundEffect::source),
            Codec.FLOAT.optionalFieldOf("volume", 1f).forGetter(SoundEffect::volume),
            Codec.FLOAT.optionalFieldOf("pitch", 1f).forGetter(SoundEffect::pitch)
    ).apply(instance, SoundEffect::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoundEffect> STREAM = StreamCodec.of(
            (buf, val) -> {
                SoundEvent.STREAM_CODEC.encode(buf, val.sound);
                buf.writeEnum(val.source);
                buf.writeFloat(val.volume);
                buf.writeFloat(val.pitch);
            },
            buf -> {
                final Holder<SoundEvent> sound = SoundEvent.STREAM_CODEC.decode(buf);
                final SoundSource source = buf.readEnum(SoundSource.class);
                final float volume = buf.readFloat();
                final float pitch = buf.readFloat();
                return new SoundEffect(sound, source, volume, pitch);
            }
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, Optional<SoundEffect>> OPTIONAL_STREAM = StreamCodec.of(
            (buf, val) -> {
                buf.writeBoolean(val.isPresent());
                val.ifPresent(soundEffect -> SoundEffect.STREAM.encode(buf, soundEffect));
            },
            buf -> {
                final boolean isPresent = buf.readBoolean();
                return isPresent ? Optional.of(SoundEffect.STREAM.decode(buf)) : Optional.empty();
            }
    );

    public void playSound(ServerLevel level, Player player, BlockPos pos) {
        this.playSound(level, player, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
    }

    public void playSound(ServerLevel level, Player player, float x, float y, float z) {
        level.playSound(player, x, y, z, this.sound, this.source, this.volume, this.pitch);
    }
}
