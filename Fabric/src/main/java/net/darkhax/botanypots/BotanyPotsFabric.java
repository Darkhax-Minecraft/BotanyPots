package net.darkhax.botanypots;

import net.fabricmc.api.ModInitializer;

public class BotanyPotsFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        new BotanyPotsCommon();
    }
}