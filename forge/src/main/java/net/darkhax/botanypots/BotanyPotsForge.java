package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.botanypots.addons.top.TOPPlugin;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
public class BotanyPotsForge {

    public BotanyPotsForge() {

        new BotanyPotsCommon();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadModCompat);
    }

    private void loadModCompat(FMLCommonSetupEvent event) {

        if (Services.PLATFORM.isModLoaded("theoneprobe")) {

            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPPlugin::new);
        }
    }
}