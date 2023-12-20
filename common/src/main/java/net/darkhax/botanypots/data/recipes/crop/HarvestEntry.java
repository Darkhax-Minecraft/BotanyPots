package net.darkhax.botanypots.data.recipes.crop;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.darkhax.bookshelf.api.data.bytebuf.ByteBufHelper;
import net.darkhax.bookshelf.api.data.codecs.BookshelfCodecs;
import net.darkhax.bookshelf.api.data.codecs.CodecHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class HarvestEntry {

    public static CodecHelper<HarvestEntry> CODEC = new CodecHelper<>(RecordCodecBuilder.create(instance -> instance.group(
            BookshelfCodecs.FLOAT.get("chance", HarvestEntry::getChance, 1f),
            BookshelfCodecs.ITEM_STACK_FLEXIBLE.get("output", HarvestEntry::getItem),
            BookshelfCodecs.INT.get("minRolls", HarvestEntry::getMinRolls, 1),
            BookshelfCodecs.INT.get("maxRolls", HarvestEntry::getMaxRolls, 1)
    ).apply(instance, HarvestEntry::new)));
    public static ByteBufHelper<HarvestEntry> BUFFER = new ByteBufHelper<>(HarvestEntry::read, HarvestEntry::write);

    private final float chance;
    private final ItemStack item;
    private final int minRolls;
    private final int maxRolls;

    public HarvestEntry(float chance, ItemStack item, int minRolls, int maxRolls) {

        this.chance = chance;
        this.item = item;
        this.minRolls = minRolls;
        this.maxRolls = maxRolls;

        if (minRolls < 0 || maxRolls < 0) {

            throw new IllegalArgumentException("Rolls must not be negative!");
        }

        if (minRolls > maxRolls) {

            throw new IllegalArgumentException("Min rolls must not be greater than max rolls!");
        }
    }

    /**
     * Gets the chance for the entry to happen.
     *
     * @return The chance for the entry to happen.
     */
    public float getChance() {

        return this.chance;
    }

    /**
     * Gets the item to give from this entry.
     *
     * @return The item to give.
     */
    public ItemStack getItem() {

        return this.item;
    }

    /**
     * Gets the minimum amount of items to give.
     *
     * @return The minimum amount of items to give.
     */
    public int getMinRolls() {

        return this.minRolls;
    }

    /**
     * Gets the maximum amount of items to give.
     *
     * @return The maximum amount of items to give.
     */
    public int getMaxRolls() {

        return this.maxRolls;
    }

    public static HarvestEntry read(FriendlyByteBuf buffer) {

        final float chance = BookshelfByteBufs.FLOAT.read(buffer);
        final ItemStack output = BookshelfByteBufs.ITEM_STACK.read(buffer);
        final int min = BookshelfByteBufs.INT.read(buffer);
        final int max = BookshelfByteBufs.INT.read(buffer);
        return new HarvestEntry(chance, output, min, max);
    }

    public static void write(FriendlyByteBuf buffer, HarvestEntry entry) {

        BookshelfByteBufs.FLOAT.write(buffer, entry.chance);
        BookshelfByteBufs.ITEM_STACK.write(buffer, entry.item);
        BookshelfByteBufs.INT.write(buffer, entry.minRolls);
        BookshelfByteBufs.INT.write(buffer, entry.maxRolls);
    }
}