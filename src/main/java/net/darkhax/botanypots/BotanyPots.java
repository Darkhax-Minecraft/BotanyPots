package net.darkhax.botanypots;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.registry.RegistryHelperClient;
import net.darkhax.botanypots.api.crop.CropReloadListener;
import net.darkhax.botanypots.api.fertilizer.FertilizerReloadListener;
import net.darkhax.botanypots.api.soil.SoilReloadListener;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BotanyPots.MOD_ID)
public class BotanyPots {
    
    public static final String MOD_ID = "botanypots";
    public static final Logger LOGGER = LogManager.getLogger("Botany Pots");
    public static final ItemGroup ITEM_GROUP = ItemGroup.MISC;
    private final RegistryHelper registry;
    
    public static TileEntityType<TileEntityBotanyPot> tileBotanyPot;
    public static List<Block> botanyPots = new ArrayList<>();
    
    public BotanyPots() {
        
        this.registry = DistExecutor.runForDist( () -> () -> new RegistryHelperClient(MOD_ID, LOGGER, ITEM_GROUP), () -> () -> new RegistryHelper(MOD_ID, LOGGER, ITEM_GROUP));
        MinecraftForge.EVENT_BUS.addListener(this::startServer);
        
        // Normal Botany Pots
        botanyPots.add(this.registry.registerBlock(new BlockBotanyPot(), "botany_pot"));
        
        for (final DyeColor dyeColor : DyeColor.values()) {
            
            botanyPots.add(this.registry.registerBlock(new BlockBotanyPot(), dyeColor.getName() + "_botany_pot"));
        }
        
        // Hopper Botany Pots
        botanyPots.add(this.registry.registerBlock(new BlockBotanyPot(true), "hopper_botany_pot"));
        
        for (final DyeColor dyeColor : DyeColor.values()) {
            
            botanyPots.add(this.registry.registerBlock(new BlockBotanyPot(true), "hopper_" + dyeColor.getName() + "_botany_pot"));
        }
        
        tileBotanyPot = this.registry.registerTileEntity(TileEntityBotanyPot::new, "botany_pot", botanyPots.toArray(new Block[0]));
        
        if (this.registry instanceof RegistryHelperClient) {
            
            ((RegistryHelperClient) this.registry).setSpecialRenderer(TileEntityBotanyPot.class, new TileEntityRendererBotanyPot());
        }
        
        this.registry.initialize(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    private void startServer (FMLServerAboutToStartEvent event) {
        
        final IReloadableResourceManager manager = event.getServer().getResourceManager();
        manager.addReloadListener(new SoilReloadListener());
        manager.addReloadListener(new CropReloadListener());
        manager.addReloadListener(new FertilizerReloadListener());
    }
}