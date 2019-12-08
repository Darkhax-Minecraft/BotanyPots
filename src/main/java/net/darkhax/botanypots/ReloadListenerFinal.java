package net.darkhax.botanypots;

import net.darkhax.botanypots.crop.CropReloadListener;
import net.darkhax.botanypots.fertilizer.FertilizerReloadListener;
import net.darkhax.botanypots.soil.SoilReloadListener;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;

public class ReloadListenerFinal extends ReloadListener<Void> {
    
    @Override
    protected Void prepare (IResourceManager resourceManagerIn, IProfiler profilerIn) {
        
        return null;
    }
    
    @Override
    protected void apply (Void splashList, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        
        BotanyPots.LOGGER.info("Loaded {} soil types.", SoilReloadListener.registeredSoil.size());
        BotanyPots.LOGGER.info("Loaded {} crop types.", CropReloadListener.registeredCrops.size());
        BotanyPots.LOGGER.info("Loaded {} fertilizer types.", FertilizerReloadListener.registeredFertilizer.size());
        BotanyPots.LOGGER.info("Syncing registry data with all active players.");
        BotanyPots.instance.sync(null);
    }
}