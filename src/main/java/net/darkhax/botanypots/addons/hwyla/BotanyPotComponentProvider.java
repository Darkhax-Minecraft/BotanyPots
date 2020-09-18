package net.darkhax.botanypots.addons.hwyla;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class BotanyPotComponentProvider implements IComponentProvider {
    
    private static final DecimalFormat format = new DecimalFormat("#");
    
    @Override
    public void appendBody (List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        
        final TileEntity tile = accessor.getTileEntity();
        
        if (tile instanceof TileEntityBotanyPot) {
            
            final TileEntityBotanyPot pot = (TileEntityBotanyPot) tile;
            
            if (config.get(BotanyPotsHwylaPlugin.KEY_SHOW_SOIL) && pot.getSoil() != null) {
                
                tooltip.add(new TranslationTextComponent("botanypots.tooltip.soil", pot.getSoil().getName()));
            }
            
            if (config.get(BotanyPotsHwylaPlugin.KEY_SHOW_CROP) && pot.getCrop() != null) {
                
                tooltip.add(new TranslationTextComponent("botanypots.tooltip.crop", pot.getCrop().getName()));
            }
            
            if (config.get(BotanyPotsHwylaPlugin.KEY_SHOW_PROGRESS) && pot.getCrop() != null && pot.getSoil() != null) {
                
                tooltip.add(new TranslationTextComponent("botanypots.tooltip.growth_progress", format.format(pot.getGrowthPercent() * 100f)));
            }
            
            if (config.get(BotanyPotsHwylaPlugin.KEY_SHOW_TIME) && pot.getCurrentGrowthTicks() > 0) {
                
                final int ticksRemaining = pot.getTotalGrowthTicks() - pot.getCurrentGrowthTicks();
                tooltip.add(new TranslationTextComponent("botanypots.tooltip.growth_time", ticksToElapsedTime(ticksRemaining)));
            }
            
            if (config.get(BotanyPotsHwylaPlugin.KEY_SHOW_DEBUG)) {
                
                if (pot.getCrop() != null) {
                    
                    tooltip.add(new TranslationTextComponent("botanypots.tooltip.crop.id", pot.getCrop().getId().toString()).mergeStyle(TextFormatting.LIGHT_PURPLE));
                    tooltip.add(new TranslationTextComponent("botanypots.tooltip.crop.ticks", pot.getCrop().getGrowthTicks()).mergeStyle(TextFormatting.LIGHT_PURPLE));
                    tooltip.add(new TranslationTextComponent("botanypots.tooltip.crop.categories", pot.getCrop().getSoilCategories().stream().collect(Collectors.joining(", "))).mergeStyle(TextFormatting.LIGHT_PURPLE));
                }
                
                if (pot.getSoil() != null) {
                    
                    tooltip.add(new TranslationTextComponent("botanypots.tooltip.soil.id", pot.getSoil().getId().toString()).mergeStyle(TextFormatting.AQUA));
                    tooltip.add(new TranslationTextComponent("botanypots.tooltip.soil.modifier", pot.getSoil().getGrowthModifier()).mergeStyle(TextFormatting.AQUA));
                    tooltip.add(new TranslationTextComponent("botanypots.tooltip.soil.categories", pot.getSoil().getCategories().stream().collect(Collectors.joining(", "))).mergeStyle(TextFormatting.AQUA));
                }
                
                tooltip.add(new TranslationTextComponent("botanypots.tooltip.pot.totalticks", pot.getTotalGrowthTicks()).mergeStyle(TextFormatting.GREEN));
                tooltip.add(new TranslationTextComponent("botanypots.tooltip.pot.growticks", pot.getCurrentGrowthTicks()).mergeStyle(TextFormatting.GREEN));
            }
        }
    }
    
    private static String ticksToElapsedTime (int ticks) {
        
        int i = ticks / 20;
        final int j = i / 60;
        i = i % 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }
}