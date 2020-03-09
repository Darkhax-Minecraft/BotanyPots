package net.darkhax.botanypots;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.bookshelf.item.ItemGroupBase;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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
        this.registry = new RegistryHelper(MOD_ID, LOGGER, this.itemGroup);
        this.content = DistExecutor.runForDist( () -> () -> new ContentClient(this.registry), () -> () -> new Content(this.registry));
        
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(this::seedTooltip));
        
        this.registry.initialize(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    private void seedTooltip (ItemTooltipEvent event) {
        
        final PlayerEntity player = event.getPlayer();
        
        if (event.getFlags().isAdvanced() && player != null && BotanyPotHelper.getCropForItem(player.world, event.getItemStack()) != null) {
            
            event.getToolTip().add(new TranslationTextComponent("botanypots.tooltip.advanced.seed_item").applyTextStyle(TextFormatting.GREEN));
        }
    }
    
    public Content getContent () {
        
        return this.content;
    }
    
    public RegistryHelper getRegistry () {
        
        return this.registry;
    }
}