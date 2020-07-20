package net.darkhax.botanypots;

import net.darkhax.botanypots.network.BreakEffectsMessage;
import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
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
    
    private static final String NETWORK_PROTOCOL_VERSION = "1";
    
    private static final SimpleChannel NETWORK_CHANNEL =
        NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MOD_ID, "network"))
            .clientAcceptedVersions(NETWORK_PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(NETWORK_PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> NETWORK_PROTOCOL_VERSION)
            .simpleChannel();
    
    static {
        
        NETWORK_CHANNEL.messageBuilder(BreakEffectsMessage.class, 0, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(BreakEffectsMessage::write).decoder(BreakEffectsMessage::new)
            .consumer( (breakEffectsMessage, contextSupplier) -> {
                
                contextSupplier.get().enqueueWork(breakEffectsMessage::doBreakEffects);
                contextSupplier.get().setPacketHandled(true);
            }).add();
    }
    
    public static BotanyPots instance;
    
    private final Content content;
    private final RegistryHelper registry;
    private final ItemGroup itemGroup;
    
    public BotanyPots() {
        
        instance = this;
        ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT_CONFIG.getSpec());
        this.itemGroup = new ItemGroupBase(MOD_ID, () -> new ItemStack(BotanyPots.instance.content.basicBotanyPot));
        this.registry = new RegistryHelper(MOD_ID, LOGGER).withItemGroup(this.itemGroup);
        this.content = DistExecutor.unsafeRunForDist( () -> () -> new ContentClient(this.registry), () -> () -> new Content(this.registry));
        
        this.registry.initialize(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    public Content getContent () {
        
        return this.content;
    }
    
    public RegistryHelper getRegistry () {
        
        return this.registry;
    }
    
    public static void doBreakEffects(World world, BlockPos pos, BlockState state) {
        
        NETWORK_CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new BreakEffectsMessage(pos, state));
    }
}