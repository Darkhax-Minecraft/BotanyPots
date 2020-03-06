package net.darkhax.botanypots;

import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.botanypots.block.tileentity.TileEntityRendererBotanyPot;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ContentClient extends Content {
    
    public ContentClient(RegistryHelper registry) {
        
        super(registry);
        
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }
    
    private void onClientSetup (FMLClientSetupEvent event) {
        
        for (final Block block : this.getBotanyPotBlocks()) {
            
            RenderTypeLookup.setRenderLayer(block, RenderType.getCutout());
        }
        
        ClientRegistry.bindTileEntityRenderer(this.getPotTileType(), TileEntityRendererBotanyPot::new);
    }
}