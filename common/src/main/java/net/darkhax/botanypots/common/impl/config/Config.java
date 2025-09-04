package net.darkhax.botanypots.common.impl.config;

import net.darkhax.pricklemc.common.api.annotations.Value;

public class Config {

    @Value(comment = "Options related to general gameplay and usage of botany pots.", writeDefault = false)
    public Gameplay gameplay = new Gameplay();

    @Value(comment = "Options related to various recipes in the game.", writeDefault = false)
    public Recipes recipes = new Recipes();

    @Value(comment = "Options related to how botany pots appear and are rendered.", writeDefault = false)
    public Visuals visuals = new Visuals();
}