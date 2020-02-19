package net.darkhax.botanypots;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.bookshelf.item.ItemGroupBase;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.bookshelf.registry.RegistryHelperClient;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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
    
    private RecipeManager recipeManager;
    
    public BotanyPots() {
        
        instance = this;
        
        this.itemGroup = new ItemGroupBase(MOD_ID, () -> new ItemStack(BotanyPots.instance.content.getBasicBotanyPot()));
        this.registry = DistExecutor.runForDist( () -> () -> new RegistryHelperClient(MOD_ID, LOGGER, this.itemGroup), () -> () -> new RegistryHelper(MOD_ID, LOGGER, this.itemGroup));
        this.content = DistExecutor.runForDist( () -> () -> new ContentClient(this.registry), () -> () -> new Content(this.registry));
        
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::startServer);
        
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(this::onRecipesUpdated));
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(this::seedTooltip));
        
        this.registry.initialize(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    private void seedTooltip (ItemTooltipEvent event) {
        
        if (event.getFlags().isAdvanced() && event.getEntityPlayer() != null && BotanyPotHelper.getCropForItem(event.getEntityPlayer().world, event.getItemStack()) != null) {
            
            event.getToolTip().add(new TranslationTextComponent("botanypots.tooltip.advanced.seed_item").applyTextStyle(TextFormatting.GREEN));
        }
    }
    
    private void startServer (FMLServerAboutToStartEvent event) {
        
        this.recipeManager = event.getServer().getRecipeManager();
    }
    
    private void onRecipesUpdated (RecipesUpdatedEvent event) {
        
        this.recipeManager = event.getRecipeManager();
    }
    
    public Content getContent () {
        
        return this.content;
    }
    
    public RegistryHelper getRegistry () {
        
        return this.registry;
    }
    
    public RecipeManager getActiveRecipeManager () {
        
        return this.recipeManager;
    }
}