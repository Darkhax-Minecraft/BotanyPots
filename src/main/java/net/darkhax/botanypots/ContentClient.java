package net.darkhax.botanypots;

import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.darkhax.botanypots.block.tileentity.RendererBotanyPot;
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
        
        for (final Block block : BlockBotanyPot.botanyPots) {
            
            RenderTypeLookup.setRenderLayer(block, RenderType.getCutout());
        }
        
        ClientRegistry.bindTileEntityRenderer(this.tileBotanyPot, RendererBotanyPot::new);
    }
}