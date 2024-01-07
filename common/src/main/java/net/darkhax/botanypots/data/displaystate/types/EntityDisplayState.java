package net.darkhax.botanypots.data.displaystate.types;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.darkhax.bookshelf.api.data.bytebuf.ByteBufHelper;
import net.darkhax.bookshelf.api.data.codecs.BookshelfCodecs;
import net.darkhax.bookshelf.api.data.codecs.CodecHelper;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.data.displaystate.DisplayTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class EntityDisplayState extends DisplayState {

    public static final CodecHelper<EntityDisplayState> CODEC = new CodecHelper<>(RecordCodecBuilder.create(instance -> instance.group(
            BookshelfCodecs.COMPOUND_TAG.get("entity", EntityDisplayState::getEntityData),
            BookshelfCodecs.BOOLEAN.get("should_tick", EntityDisplayState::shouldTickEntity, true),
            BookshelfCodecs.INT.get("spin_speed", EntityDisplayState::getSpinSpeed, 0),
            BookshelfCodecs.VECTOR_3F.getOptional("scale", EntityDisplayState::getScale, Optional.of(new Vector3f(0.5f, 0.5f, 0.5f))),
            BookshelfCodecs.VECTOR_3F.getOptional("offset", EntityDisplayState::getOffset)
    ).apply(instance, EntityDisplayState::new)));
    public static final ByteBufHelper<EntityDisplayState> BUFFER = new ByteBufHelper<>(EntityDisplayState::readFromBuffer, EntityDisplayState::writeToBuffer);

    private final CompoundTag entityData;
    private final boolean tickEntity;
    private final int spinSpeed;
    private final Optional<Vector3f> scale;
    private final Optional<Vector3f> offset;

    @Nullable
    private Entity displayEntity;

    public EntityDisplayState(CompoundTag displayEntity, boolean shouldTick, int spinSpeed, Optional<Vector3f> scale, Optional<Vector3f> offset) {

        this.entityData = displayEntity;
        this.tickEntity = shouldTick;
        this.spinSpeed = spinSpeed;
        this.scale = scale;
        this.offset = offset;
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

    public Optional<Vector3f> getScale() {
        return scale;
    }

    public Optional<Vector3f> getOffset() {
        return this.offset;
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(Level level, RandomSource rng, BlockPos pos) {

        if (this.displayEntity == null && this.entityData != null && this.entityData.contains("id", Tag.TAG_STRING)) {

            try {

                this.displayEntity = EntityType.loadEntityRecursive(this.entityData, level, Function.identity());
            }

            catch (Exception e) {

                Constants.LOG.error("Failed to create entity renderer from data {}.", this.entityData, e);
            }
        }

        return this.displayEntity;
    }

    @Override
    public DisplayTypes.DisplayType<?> getType() {
        return DisplayTypes.ENTITY;
    }

    private static EntityDisplayState readFromBuffer(FriendlyByteBuf buffer) {

        final CompoundTag entityTag = BookshelfByteBufs.COMPOUND_TAG.read(buffer);
        final boolean tickEntity = BookshelfByteBufs.BOOLEAN.read(buffer);
        final int spinSpeed = BookshelfByteBufs.INT.read(buffer);
        final Optional<Vector3f> scale = BookshelfByteBufs.VECTOR_3F.readOptional(buffer);
        final Optional<Vector3f> offset = BookshelfByteBufs.VECTOR_3F.readOptional(buffer);
        return new EntityDisplayState(entityTag, tickEntity, spinSpeed, scale, offset);
    }

    private static void writeToBuffer(FriendlyByteBuf buffer, EntityDisplayState toWrite) {

        BookshelfByteBufs.COMPOUND_TAG.write(buffer, toWrite.entityData);
        BookshelfByteBufs.BOOLEAN.write(buffer, toWrite.tickEntity);
        BookshelfByteBufs.INT.write(buffer, toWrite.spinSpeed);
        BookshelfByteBufs.VECTOR_3F.writeOptional(buffer, toWrite.scale);
        BookshelfByteBufs.VECTOR_3F.writeOptional(buffer, toWrite.offset);
    }
}