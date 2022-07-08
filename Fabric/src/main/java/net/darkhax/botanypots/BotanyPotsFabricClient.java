package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.block.BotanyPotRenderer;
import net.darkhax.botanypots.block.inv.BotanyPotMenu;
import net.darkhax.botanypots.block.inv.BotanyPotScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class BotanyPotsFabricClient implements ClientModInitializer {

    private static final CachedSupplier<MenuType<?>> MENU_TYPE = CachedSupplier.cache(() -> Registry.MENU.get(new ResourceLocation(Constants.MOD_ID, "pot_menu")));
    private static final CachedSupplier<BlockEntityType<BlockEntityBotanyPot>> POT_TYPE = CachedSupplier.cache(() -> (BlockEntityType<BlockEntityBotanyPot>) Services.REGISTRIES.blockEntities().get(new ResourceLocation(Constants.MOD_ID, "botany_pot")));

    @Override
    public void onInitializeClient() {

        MenuType<BotanyPotMenu> menu = (MenuType<BotanyPotMenu>) MENU_TYPE.get();
        MenuScreens.register(menu, BotanyPotScreen::new);
        BlockEntityRendererRegistry.register(POT_TYPE.get(), BotanyPotRenderer::new);
    }
}