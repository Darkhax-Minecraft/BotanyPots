package net.darkhax.botanypots.common.api.data.display.math;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;

/**
 * Represents the ARGB channels of a color.
 */
public final class TintColor {

    /**
     * A pure white tint color. This is used as a default and effectively leaves the original colors unmodified.
     */
    public static final TintColor WHITE = new TintColor(255, 255, 255, 255);

    /**
     * A codec that reads a TintColor from different keys of a map.
     */
    public static Codec<TintColor> CODEC_ARGB = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 255).optionalFieldOf("alpha", 255).forGetter(TintColor::alpha),
            Codec.intRange(0, 255).optionalFieldOf("red", 255).forGetter(TintColor::red),
            Codec.intRange(0, 255).optionalFieldOf("green", 255).forGetter(TintColor::green),
            Codec.intRange(0, 255).optionalFieldOf("blue", 255).forGetter(TintColor::blue)
    ).apply(instance, TintColor::new));

    /**
     * A codec that reads a TintColor from a hexadecimal encoded string. The casing of the string does not matter and a
     * leading hashtag will be removed if present.
     */
    public static Codec<TintColor> CODEC_STRING = Codec.STRING.xmap(TintColor::fromHex, TintColor::asHex);

    /**
     * A codec that can read a TintColor from a component map or hexadecimal string.
     */
    public static Codec<TintColor> CODEC = MapCodecs.xor(CODEC_ARGB, CODEC_STRING);

    /**
     * Stream codec for networking.
     */
    public static StreamCodec<ByteBuf, TintColor> STREAM = ByteBufCodecs.INT.map(TintColor::unpack, TintColor::pack);

    private final int[] components;
    private final float[] percentages;

    /**
     * @param alpha The value of the alpha channel. An alpha of 0 is fully invisible while 255 is fully opaque.
     * @param red   The value of the red channel.
     * @param green The value of the green channel.
     * @param blue  The value of the blue channel.
     */
    public TintColor(int alpha, int red, int green, int blue) {
        this.components = new int[]{alpha, red, green, blue};
        this.percentages = new float[]{alpha / 255f, red / 255f, green / 255f, blue / 255f};
    }

    /**
     * Represents the color as a hex string. The trailing hashtag is not included.
     *
     * @return A string that contains a hexadecimal representation of the color.
     */
    public String asHex() {
        return Integer.toHexString(this.pack());
    }

    /**
     * Gets the color components as an array of percentages. This can be useful for floating point rendering.
     *
     * @return An array of color components as percentages.
     */
    public float[] asPercents() {
        return this.percentages;
    }

    /**
     * Gets the color components as an array.
     *
     * @return The color components as an array.
     */
    public int[] asArray() {
        return this.components;
    }

    /**
     * Packs the color into a single integer.
     *
     * @return The integer representation of the color.
     */
    public int pack() {
        return FastColor.ARGB32.color(this.alpha(), this.red(), this.green(), this.blue());
    }

    /**
     * Gets the alpha value of the color. 0 is fully transparent and 255 is fully opaque.
     *
     * @return The value of the alpha channel.
     */
    public int alpha() {
        return this.components[0];
    }

    /**
     * Gets the red value.
     *
     * @return The value of the red channel.
     */
    public int red() {
        return this.components[1];
    }

    /**
     * ? Gets the green value.
     *
     * @return The value of the green channel.
     */
    public int green() {
        return this.components[2];
    }

    /**
     * Gets the blue value.
     *
     * @return The value of the blue channel.
     */
    public int blue() {
        return this.components[3];
    }


    /**
     * Unpacks a color from a single integer using bit shifting.
     *
     * @param color The color to unpack.
     * @return A tint based on the bits of the integer.
     */
    public static TintColor unpack(int color) {
        return new TintColor((color >> 24 & 0xFF), color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF);
    }

    /**
     * Decodes a color from a hex string. The casing does not matter and the hashtag is completely optional.
     *
     * @param hexCode The hex string to decode.
     * @return The color that was decoded.
     */
    public static TintColor fromHex(String hexCode) {
        final String original = hexCode;
        if (hexCode.startsWith("#")) {
            hexCode = hexCode.substring(1);
        }
        if (hexCode.length() == 6) {
            final int red = Integer.valueOf(hexCode.substring(0, 2), 16);
            final int green = Integer.valueOf(hexCode.substring(2, 4), 16);
            final int blue = Integer.valueOf(hexCode.substring(4, 6), 16);
            return new TintColor(255, red, green, blue);
        }
        else if (hexCode.length() == 8) {
            final int alpha = Integer.valueOf(hexCode.substring(0, 2), 16);
            final int red = Integer.valueOf(hexCode.substring(2, 4), 16);
            final int green = Integer.valueOf(hexCode.substring(4, 6), 16);
            final int blue = Integer.valueOf(hexCode.substring(6, 8), 16);
            return new TintColor(alpha, red, green, blue);
        }
        throw new IllegalArgumentException("Hexcode " + original + " is not valid. Must be in RRGGBB or AARRGGBB format.");
    }
}