package net.darkhax.botanypots.common.api.data.components;

import com.mojang.serialization.Codec;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.api.data.recipes.BotanyPotRecipe;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

/**
 * This item component allows specific items to define a soil that differs from their default recipe-based behavior. For
 * example, a specific iron block could grow crops at 10x speed while normal iron blocks do not offer any speed
 * modifier.
 *
 * @param soil The soil to use instead of the default recipe-based lookup.
 */
public record SoilOverride(Soil soil) {

    public static final Codec<SoilOverride> CODEC = BotanyPotRecipe.recipeCodec(Soil.TYPE).xmap(SoilOverride::new, SoilOverride::soil);
    public static final StreamCodec<RegistryFriendlyByteBuf, SoilOverride> STREAM = BotanyPotRecipe.recipeStream(Soil.TYPE).map(SoilOverride::new, SoilOverride::soil);
    public static final ResourceLocation TYPE_ID = BotanyPotsMod.id("soil");
    public static final CachedSupplier<DataComponentType<SoilOverride>> TYPE = CachedSupplier.of(BuiltInRegistries.DATA_COMPONENT_TYPE, TYPE_ID).cast();

    /**
     * Gets the override value for an item if one exists.
     *
     * @param stack The item to query.
     * @return The current soil override as an optional. If an override is specified the optional will be present,
     * otherwise it is empty.
     */
    public static Optional<SoilOverride> get(ItemStack stack) {
        return stack.has(TYPE.get()) ? Optional.ofNullable(stack.get(TYPE.get())) : Optional.empty();
    }

    /**
     * Applies a soil override to the specified item.
     *
     * @param stack The item to apply the override to.
     * @param soil  The soil to attach as an override.
     */
    public static void set(ItemStack stack, Soil soil) {
        stack.set(TYPE.get(), new SoilOverride(soil));
    }
}