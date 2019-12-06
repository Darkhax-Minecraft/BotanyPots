package net.darkhax.botanypots;

import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.registry.RegistryHelperClient;

public class ContentClient extends Content {
    
    public ContentClient(RegistryHelper registry) {
        
        super(registry);
        
        if (registry instanceof RegistryHelperClient) {
            
            final RegistryHelperClient clientRegistry = (RegistryHelperClient) registry;
            
            clientRegistry.setSpecialRenderer(TileEntityBotanyPot.class, new TileEntityRendererBotanyPot());
        }
    }
}