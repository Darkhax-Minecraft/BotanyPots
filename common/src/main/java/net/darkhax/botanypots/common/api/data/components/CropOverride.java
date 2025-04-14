package net.darkhax.botanypots.common.api.data.components;

import com.mojang.serialization.Codec;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.api.data.recipes.BotanyPotRecipe;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * This item component allows specific items to define a crop that differs from their default recipe-based behavior. For
 * example, a specific dirt item with this override could grow diamonds while all other instances are not valid seeds
 * and have no crop.
 *
 * @param crop The crop to use instead of the default recipe-based lookup.
 */
public record CropOverride(Crop crop) {

    public static final Codec<CropOverride> CODEC = BotanyPotRecipe.recipeCodec(Crop.TYPE).xmap(CropOverride::new, CropOverride::crop);
    public static final StreamCodec<RegistryFriendlyByteBuf, CropOverride> STREAM = BotanyPotRecipe.recipeStream(Crop.TYPE).map(CropOverride::new, CropOverride::crop);
    public static final ResourceLocation TYPE_ID = BotanyPotsMod.id("crop");
    public static final CachedSupplier<DataComponentType<CropOverride>> TYPE = CachedSupplier.of(BuiltInRegistries.DATA_COMPONENT_TYPE, TYPE_ID).cast();

    /**
     * Gets the override value for an item if one exists.
     *
     * @param stack The stack to query.
     * @return The current crop override as an optional. If an override is specified the optional will be present,
     * otherwise it will be empty.
     */
    public static Optional<CropOverride> get(ItemStack stack) {
        return stack.has(TYPE.get()) ? Optional.ofNullable(stack.get(TYPE.get())) : Optional.empty();
    }

    /**
     * Applies a crop override to the specified item.
     *
     * @param stack The item to apply the override to.
     * @param crop  The crop to attach as an override.
     */
    public static void set(ItemStack stack, Crop crop) {
        stack.set(TYPE.get(), new CropOverride(crop));
    }
}