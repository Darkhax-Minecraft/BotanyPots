package net.darkhax.botanypots.common.impl;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.impl.config.Config;
import net.darkhax.pricklemc.common.api.config.ConfigManager;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;

public class BotanyPotsMod {

    public static final String MOD_ID = "botanypots";
    public static final String MOD_NAME = "BotanyPots";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final ResourceLocation BUILTIN_COMPONENT_ID = id("builtin_component_override");
    public static final CachedSupplier<Config> CONFIG = CachedSupplier.cache(() -> ConfigManager.load(MOD_ID, new Config()));
    public static WeakReference<RegistryAccess> REGISTRY_ACCESS;

    public static ResourceLocation id(String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }

    public static void init() {
        CONFIG.get(); // Force init config file on main thread.
    }

    public static void updateRegistryAccess(RegistryAccess access) {
        REGISTRY_ACCESS = new WeakReference<>(access);
        BotanyPotsMod.LOG.info("Updating registry access.");
    }
}