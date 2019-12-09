package net.darkhax.botanypots;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.bookshelf.item.ItemGroupBase;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.registry.RegistryHelperClient;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BotanyPots.MOD_ID)
public class BotanyPots {
    
    public static final String MOD_ID = "botanypots";
    public static final Logger LOGGER = LogManager.getLogger("Botany Pots");
    public static BotanyPots instance;
    
    private final Content content;
    private final RegistryHelper registry;
    private final ItemGroup itemGroup;
    
    public BotanyPots() {
        
        instance = this;
        
        this.itemGroup = new ItemGroupBase(MOD_ID, () -> new ItemStack(BotanyPots.instance.content.getBasicBotanyPot()));
        this.registry = DistExecutor.runForDist( () -> () -> new RegistryHelperClient(MOD_ID, LOGGER, this.itemGroup), () -> () -> new RegistryHelper(MOD_ID, LOGGER, this.itemGroup));
        this.content = DistExecutor.runForDist( () -> () -> new ContentClient(this.registry), () -> () -> new Content(this.registry));
        
        this.registry.initialize(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    public Content getContent () {
        
        return this.content;
    }
    
    public RegistryHelper getRegistry () {
        
        return this.registry;
    }
}