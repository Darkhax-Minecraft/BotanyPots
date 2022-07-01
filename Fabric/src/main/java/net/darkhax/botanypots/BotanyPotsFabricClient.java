package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.botanypots.block.inv.BotanyPotMenu;
import net.darkhax.botanypots.block.inv.BotanyPotScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class BotanyPotsFabricClient implements ClientModInitializer {

    private static final CachedSupplier<MenuType<?>> MENU_TYPE = CachedSupplier.cache(() -> Registry.MENU.get(new ResourceLocation(Constants.MOD_ID, "pot_menu")));

    @Override
    public void onInitializeClient() {

        MenuType<BotanyPotMenu> menu = (MenuType<BotanyPotMenu>) MENU_TYPE.get();
        MenuScreens.register(menu, BotanyPotScreen::new);
    }
}