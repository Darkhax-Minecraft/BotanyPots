package net.darkhax.botanypots;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.bookshelf.item.ItemGroupBase;
import net.darkhax.bookshelf.network.NetworkHelper;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.util.ModUtils;
import net.darkhax.botanypots.addons.crt.CraftTweakerEventSubscription;
import net.darkhax.botanypots.addons.top.TOPPlugin;
import net.darkhax.botanypots.network.BreakEffectsMessage;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BotanyPots.MOD_ID)
public class BotanyPots {
    
    public static final String MOD_ID = "botanypots";
    public static final Logger LOGGER = LogManager.getLogger("Botany Pots");
    public static final ConfigClient CLIENT_CONFIG = new ConfigClient();
    public static final NetworkHelper NETWORK = new NetworkHelper("botanypots:main", "3.0.x");
    
    public static BotanyPots instance;
    
    private final Content content;
    private final RegistryHelper registry;
    private final ItemGroup itemGroup;
    
    public BotanyPots() {
        
        instance = this;
        
        NETWORK.registerEnqueuedMessage(BreakEffectsMessage.class, BreakEffectsMessage::encode, BreakEffectsMessage::decode, BreakEffectsMessage::handle);
        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT_CONFIG.getSpec());
        this.itemGroup = new ItemGroupBase(MOD_ID, () -> new ItemStack(BotanyPots.instance.content.basicBotanyPot));
        this.registry = new RegistryHelper(MOD_ID, LOGGER).withItemGroup(this.itemGroup);
        this.content = DistExecutor.unsafeRunForDist( () -> () -> new ContentClient(this.registry), () -> () -> new Content(this.registry));
        
        this.registry.initialize(FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        
        this.registerModSupport();
    }
    
    private void registerModSupport () {
        
        if (ModList.get().isLoaded("crafttweaker")) {
            MinecraftForge.EVENT_BUS.register(CraftTweakerEventSubscription.class);
        }
    }
    
    private void setup (FMLCommonSetupEvent event) {
        
        if (ModUtils.isInModList("theoneprobe")) {
            
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPPlugin::new);
        }
    }
    
    public Content getContent () {
        
        return this.content;
    }
    
    public RegistryHelper getRegistry () {
        
        return this.registry;
    }
}