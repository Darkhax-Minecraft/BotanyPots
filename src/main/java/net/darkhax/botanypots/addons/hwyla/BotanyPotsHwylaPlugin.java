package net.darkhax.botanypots.addons.hwyla;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.minecraft.util.ResourceLocation;

@WailaPlugin
public class BotanyPotsHwylaPlugin implements IWailaPlugin {
    
    public static final ResourceLocation KEY_SHOW_SOIL = new ResourceLocation(BotanyPots.MOD_ID, "show_soil");
    public static final ResourceLocation KEY_SHOW_CROP = new ResourceLocation(BotanyPots.MOD_ID, "show_crop");
    public static final ResourceLocation KEY_SHOW_PROGRESS = new ResourceLocation(BotanyPots.MOD_ID, "show_progress");
    public static final ResourceLocation KEY_SHOW_DEBUG = new ResourceLocation(BotanyPots.MOD_ID, "show_debug");
    
    @Override
    public void register (IRegistrar registry) {
        
        registry.addConfig(KEY_SHOW_SOIL, true);
        registry.addConfig(KEY_SHOW_CROP, true);
        registry.addConfig(KEY_SHOW_PROGRESS, true);
        registry.addConfig(KEY_SHOW_DEBUG, false);
        
        registry.registerComponentProvider(new BotanyPotComponentProvider(), TooltipPosition.BODY, BlockBotanyPot.class);
    }
}