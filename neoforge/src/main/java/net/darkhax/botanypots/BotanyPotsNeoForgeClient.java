package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.function.CachedSupplier;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.block.BotanyPotRenderer;
import net.darkhax.botanypots.block.inv.BotanyPotMenu;
import net.darkhax.botanypots.block.inv.BotanyPotScreen;
import net.darkhax.botanypots.data.displaystate.render.DisplayStateRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BotanyPotsNeoForgeClient {

    private static final CachedSupplier<MenuType<?>> MENU_TYPE = CachedSupplier.cache(() -> BuiltInRegistries.MENU.get(new ResourceLocation(Constants.MOD_ID, "pot_menu")));
    protected static final CachedSupplier<BlockEntityType<BlockEntityBotanyPot>> POT_TYPE = CachedSupplier.cache(() -> (BlockEntityType<BlockEntityBotanyPot>) BuiltInRegistries.BLOCK_ENTITY_TYPE.get(new ResourceLocation(Constants.MOD_ID, "botany_pot")));

    @SubscribeEvent
    public static void clientInit(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {

            MenuType<BotanyPotMenu> menu = (MenuType<BotanyPotMenu>) MENU_TYPE.get();
            MenuScreens.register(menu, BotanyPotScreen::new);
            DisplayStateRenderer.init();
        });
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerBlockEntityRenderer(POT_TYPE.get(), BotanyPotRenderer::new);
    }
}
