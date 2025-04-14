package net.darkhax.botanypots.fabric.impl;

import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.fabricmc.api.ModInitializer;

public class FabricMod implements ModInitializer {

    @Override
    public void onInitialize() {
        new BotanyPotsMod();
    }
}