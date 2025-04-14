package net.darkhax.botanypots.common.impl.config;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.pricklemc.common.api.annotations.Value;
import net.darkhax.pricklemc.common.api.config.ConfigManager;

import java.util.function.Supplier;

public class Config {

    public static final Supplier<Config> INSTANCE = CachedSupplier.cache(() -> ConfigManager.load(BotanyPotsMod.MOD_ID, new Config()));

    @Value(comment = "Options related to general gameplay and usage of botany pots.", writeDefault = false)
    public Gameplay gameplay = new Gameplay();

    @Value(comment = "Options related to various recipes in the game.", writeDefault = false)
    public Recipes recipes = new Recipes();

    @Value(comment = "Options related to how botany pots appear and are rendered.", writeDefault = false)
    public Visuals visuals = new Visuals();
}