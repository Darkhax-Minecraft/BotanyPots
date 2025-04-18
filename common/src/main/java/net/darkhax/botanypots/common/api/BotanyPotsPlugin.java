package net.darkhax.botanypots.common.api;

import com.mojang.serialization.MapCodec;
import net.darkhax.bookshelf.common.api.PhysicalSide;
import net.darkhax.bookshelf.common.api.annotation.OnlyFor;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.service.Services;
import net.darkhax.botanypots.common.api.command.generator.crop.CropGenerator;
import net.darkhax.botanypots.common.api.command.generator.soil.SoilGenerator;
import net.darkhax.botanypots.common.api.data.display.render.DisplayRenderer;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.BiConsumer;

/**
 * Botany pots plugins can be used to hook into various botany pot systems and improve integration with the mod.
 * <p>
 * Plugins are registered using a service loader. This makes them viable on several mod loaders using a single entry
 * point. In order to define your plugin define the fully qualified class name in the
 * META-INF/services/net.darkhax.botanypots.common.api.BotanyPotsPlugin. Please refer to the built-in plugin which is
 * also defined using this system if you need more info.
 */
public interface BotanyPotsPlugin {

    /**
     * Provides a list of registered botany pot plugins. Resolving the plugin list will cause all plugins to be
     * initialized.
     */
    CachedSupplier<List<BotanyPotsPlugin>> PLUGINS = CachedSupplier.cache(() -> {
        final List<BotanyPotsPlugin> plugins = Services.loadMany(BotanyPotsPlugin.class);
        BotanyPotsMod.LOG.info("Loaded {} botany pots plugins!", plugins.size());
        return plugins;
    });

    /**
     * Registers soil generators with Botany Pots. These are data generators that are accessed using the in-game
     * command. These do NOT automatically create and register soils with the mod. Use datapack files for that.
     *
     * @param register A consumer that will register any accepted soil generator.
     */
    default void registerSoilGenerators(BiConsumer<ResourceLocation, SoilGenerator> register) {
    }

    /**
     * Registers crop generators with Botany Pots. These are data generators that are accessed using the in-game
     * command. These do NOT automatically create and register soils with the mod. Use datapack files for that.
     *
     * @param register A consumer that will register any accepted crop generator.
     */
    default void registerCropGenerators(BiConsumer<ResourceLocation, CropGenerator> register) {
    }

    /**
     * Register new display types with Botany Pots. These are registered using
     * {@link net.darkhax.botanypots.common.api.data.display.types.DisplayType#register(ResourceLocation, MapCodec,
     * StreamCodec)}.
     */
    default void registerDisplayTypes() {
    }

    /**
     * Binds a renderer to a display type. This method is only invoked on the client, and it is safe to use client side
     * only code within this method. These are bound using
     * {@link net.darkhax.botanypots.common.api.data.display.render.DisplayRenderer#bind(DisplayType,
     * DisplayRenderer)}.
     */
    @OnlyFor(PhysicalSide.CLIENT)
    default void bindDisplayRenderers() {
    }
}