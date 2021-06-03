package net.darkhax.botanypots.addons.top;

import java.text.DecimalFormat;
import java.util.function.Function;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class TOPPlugin implements Function<ITheOneProbe, Void>, IProbeInfoProvider {
    
    private static final DecimalFormat format = new DecimalFormat("#");
    
    @Override
    public String getID () {
        
        return BotanyPots.MOD_ID + ":top_support";
    }
    
    @Override
    public Void apply (ITheOneProbe top) {
        
        top.registerProvider(this);
        return null;
    }
    
    @Override
    public void addProbeInfo (ProbeMode mode, IProbeInfo info, PlayerEntity player, World world, BlockState state, IProbeHitData hit) {
        
        final Block block = state.getBlock();
        
        if (block instanceof BlockBotanyPot) {
            
            final TileEntity tile = world.getTileEntity(hit.getPos());
            
            if (tile instanceof TileEntityBotanyPot) {
                
                this.addPotInfo((TileEntityBotanyPot) tile, ((BlockBotanyPot) block).isHopper(), info);
            }
        }
    }
    
    private void addPotInfo (TileEntityBotanyPot pot, boolean isHopper, IProbeInfo info) {
        
        if (pot.getSoil() != null) {
            
            // I would like to use Block#getTranslatedName but it is client only and this is a
            // server environment.
            info.text(new TranslationTextComponent("botanypots.tooltip.soil", new TranslationTextComponent(pot.getSoil().getRenderState().getState().getBlock().getTranslationKey())));
        }
        
        if (pot.getCrop() != null) {
            
            // I would like to use Block#getTranslatedName but it is client only and this is a
            // server environment.
            info.text(new TranslationTextComponent("botanypots.tooltip.crop", new TranslationTextComponent(pot.getCrop().getDisplayState()[0].getState().getBlock().getTranslationKey())));
        }
        
        if (pot.getCurrentGrowthTicks() > 0) {
            
            final int ticksRemaining = pot.getTotalGrowthTicks() - pot.getCurrentGrowthTicks();
            info.text(new TranslationTextComponent("botanypots.tooltip.growth_time", ticksToElapsedTime(ticksRemaining)));
        }
        
        if (pot.getCrop() != null && pot.getSoil() != null) {
            
            final ProgressStyle style = new ProgressStyle();
            style.filledColor(0xff32CD32);
            style.alternateFilledColor(0xff259925);
            style.prefix("Progress: "); // TODO This can't be translated?
            style.suffix("%");
            final int f = MathHelper.floor(pot.getGrowthPercent() * 100f);
            info.progress(f, 100, style);
        }
    }
    
    private static String ticksToElapsedTime (int ticks) {
        
        int i = ticks / 20;
        final int j = i / 60;
        i = i % 60;
        return i < 10 ? j + ":0" + i : j + ":" + i;
    }
}