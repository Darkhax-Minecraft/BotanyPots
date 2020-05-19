package net.darkhax.botanypots;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.bookshelf.item.ItemGroupBase;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BotanyPots.MOD_ID)
public class BotanyPots {
    
    public static final String MOD_ID = "botanypots";
    public static final Logger LOGGER = LogManager.getLogger("Botany Pots");
    public static final ConfigClient CLIENT_CONFIG = new ConfigClient();
    
    public static BotanyPots instance;
    
    private final Content content;
    private final RegistryHelper registry;
    private final ItemGroup itemGroup;
    
    public BotanyPots() {
        
        instance = this;
        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT_CONFIG.getSpec());
        this.itemGroup = new ItemGroupBase(MOD_ID, () -> new ItemStack(BotanyPots.instance.content.getBasicBotanyPot()));
        this.registry = new RegistryHelper(MOD_ID, LOGGER, this.itemGroup);
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