package net.darkhax.botanypots.common.impl.data.display.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.function.ReloadableCache;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import org.joml.Vector3f;

import java.util.Optional;

public class EntityDisplayState implements Display {

    public static final ResourceLocation TYPE_ID = BotanyPotsMod.id("entity");
    public static final CachedSupplier<DisplayType<EntityDisplayState>> TYPE = CachedSupplier.cache(() -> DisplayType.get(TYPE_ID));

    public static final MapCodec<EntityDisplayState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CompoundTag.CODEC.fieldOf("entity").forGetter(EntityDisplayState::getEntityData),
            Codec.BOOL.optionalFieldOf("should_tick", true).forGetter(EntityDisplayState::shouldTickEntity),
            Codec.INT.optionalFieldOf("spin_speed", 0).forGetter(EntityDisplayState::getSpinSpeed),
            ExtraCodecs.VECTOR3F.optionalFieldOf("scale", new Vector3f(0.5f, 0.5f, 0.5f)).forGetter(EntityDisplayState::getScale),
            ExtraCodecs.VECTOR3F.optionalFieldOf("offset").forGetter(EntityDisplayState::getOffset)
    ).apply(instance, EntityDisplayState::new));

    public static final StreamCodec<FriendlyByteBuf, EntityDisplayState> STREAM = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, EntityDisplayState::getEntityData,
            ByteBufCodecs.BOOL, EntityDisplayState::shouldTickEntity,
            ByteBufCodecs.INT, EntityDisplayState::getSpinSpeed,
            ByteBufCodecs.VECTOR3F, EntityDisplayState::getScale,
            DataHelper.optionalStream(ByteBufCodecs.VECTOR3F), EntityDisplayState::getOffset,
            EntityDisplayState::new
    );

    private final CompoundTag entityData;
    private final boolean tickEntity;
    private final int spinSpeed;
    private final Vector3f scale;
    private final Optional<Vector3f> offset;
    private final ReloadableCache<Entity> displayEntity;

    public EntityDisplayState(CompoundTag displayEntity, boolean shouldTick, int spinSpeed, Vector3f scale, Optional<Vector3f> offset) {
        this.entityData = displayEntity;
        this.tickEntity = shouldTick;
        this.spinSpeed = spinSpeed;
        this.scale = scale;
        this.offset = offset;
        this.displayEntity = ReloadableCache.entity(displayEntity);
    }

    public CompoundTag getEntityData() {
        return entityData;
    }

    public boolean shouldTickEntity() {
        return this.tickEntity;
    }

    public int getSpinSpeed() {
        return this.spinSpeed;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Optional<Vector3f> getOffset() {
        return this.offset;
    }

    public ReloadableCache<Entity> getDisplayEntity() {
        return this.displayEntity;
    }

    @Override
    public DisplayType<?> getType() {
        return TYPE.get();
    }
}