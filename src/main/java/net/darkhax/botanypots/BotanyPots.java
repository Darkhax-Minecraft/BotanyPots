package net.darkhax.botanypots;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.bookshelf.item.ItemGroupBase;
import net.darkhax.bookshelf.network.NetworkHelper;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.registry.RegistryHelperClient;
import net.darkhax.botanypots.crop.CropReloadListener;
import net.darkhax.botanypots.fertilizer.FertilizerReloadListener;
import net.darkhax.botanypots.network.BotanyPotsClient;
import net.darkhax.botanypots.network.BotanyPotsServer;
import net.darkhax.botanypots.network.MessageSyncRegistries;
import net.darkhax.botanypots.soil.SoilReloadListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BotanyPots.MOD_ID)
public class BotanyPots {
    
    public static final String MOD_ID = "botanypots";
    public static final Logger LOGGER = LogManager.getLogger("Botany Pots");
    public static BotanyPots instance;
    
    private final Content content;
    private final RegistryHelper registry;
    private final ItemGroup itemGroup;
    private final NetworkHelper network;
    
    public BotanyPots() {
        
        instance = this;
        
        this.itemGroup = new ItemGroupBase(MOD_ID, () -> new ItemStack(BotanyPots.instance.content.getBasicBotanyPot()));
        this.registry = DistExecutor.runForDist( () -> () -> new RegistryHelperClient(MOD_ID, LOGGER, this.itemGroup), () -> () -> new RegistryHelper(MOD_ID, LOGGER, this.itemGroup));
        this.content = DistExecutor.runForDist( () -> () -> new ContentClient(this.registry), () -> () -> new Content(this.registry));
        this.network = new NetworkHelper(new ResourceLocation(MOD_ID, "main"), "1.0.X");
        
        this.network.registerEnqueuedMessage(MessageSyncRegistries.class, (p, b) -> BotanyPotsServer.encodeRegistriesMessage(p, b), (b) -> BotanyPotsClient.decodeRegistriesMessage(b), (m, c) -> BotanyPotsClient.processRegistriesMessage(m, c));
        MinecraftForge.EVENT_BUS.addListener(this::startServer);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerJoin);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::startServerLast);
        
        this.registry.initialize(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    private void startServer (FMLServerAboutToStartEvent event) {
        
        final IReloadableResourceManager manager = event.getServer().getResourceManager();
        manager.addReloadListener(new SoilReloadListener());
        manager.addReloadListener(new CropReloadListener());
        manager.addReloadListener(new FertilizerReloadListener());
    }
    
    private void startServerLast (FMLServerAboutToStartEvent event) {
        
        final IReloadableResourceManager manager = event.getServer().getResourceManager();
        
        // This reload listener handles printing out the final counts of the data, and will
        // also send a sync packet to all players to update the client side registry maps.
        manager.addReloadListener(new ReloadListenerFinal());
    }
    
    private void onPlayerJoin (PlayerLoggedInEvent event) {
        
        // Ensure that clients get the registry info when they join the server.
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            
            this.sync((ServerPlayerEntity) event.getPlayer());
        }
    }
    
    public Content getContent () {
        
        return this.content;
    }
    
    public RegistryHelper getRegistry () {
        
        return this.registry;
    }
    
    public void sync (@Nullable ServerPlayerEntity player) {
        
        if (player != null) {
            
            BotanyPots.LOGGER.info("Sending a Sync packet to {}.", player.getGameProfile().getName());
            this.network.sendToPlayer(player, new MessageSyncRegistries());
        }
        
        else {
            
            this.network.sendToAllPlayers(new MessageSyncRegistries());
        }
    }
}