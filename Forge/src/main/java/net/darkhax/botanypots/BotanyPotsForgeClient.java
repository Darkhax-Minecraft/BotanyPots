package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.botanypots.block.inv.BotanyPotMenu;
import net.darkhax.botanypots.block.inv.BotanyPotScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BotanyPotsForgeClient {

    private static final CachedSupplier<MenuType<?>> MENU_TYPE = CachedSupplier.cache(() -> Registry.MENU.get(new ResourceLocation(Constants.MOD_ID, "pot_menu")));

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {

            MenuType<BotanyPotMenu> menu = (MenuType<BotanyPotMenu>) MENU_TYPE.get();
            MenuScreens.register(menu, BotanyPotScreen::new);
        });
    }
}
