package net.darkhax.botanypots.api.soil;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.darkhax.botanypots.BotanyPots;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class SoilReloadListener extends JsonReloadListener {
    
    private static final Gson GSON_INSTANCE = new GsonBuilder().create();
    
    public static Map<ResourceLocation, SoilInfo> registeredSoil = new HashMap<>();
    
    public SoilReloadListener() {
        
        super(GSON_INSTANCE, "botanypots_soils");
    }
    
    @Override
    protected void apply (Map<ResourceLocation, JsonObject> splashList, IResourceManager manager, IProfiler profiler) {
        
        profiler.startSection("Processing soil JsonObjects");
        
        registeredSoil.clear();
        
        for (final Entry<ResourceLocation, JsonObject> entry : splashList.entrySet()) {
            
            if (registeredSoil.containsKey(entry.getKey())) {
                
                BotanyPots.LOGGER.warn("Duplicate JSON for {}. It will be overriden.", entry.getKey());
            }
            
            try {
                
                final SoilInfo soilInfo = SoilInfo.deserialize(entry.getKey(), entry.getValue());
                registeredSoil.put(entry.getKey(), soilInfo);
            }
            
            catch (final Exception exception) {
                
                BotanyPots.LOGGER.error("Could not parse soil info JSON for {}.", entry.getKey());
                BotanyPots.LOGGER.catching(exception);
            }
        }
        
        profiler.endSection();
        
        BotanyPots.LOGGER.info("Loaded {} soil types from datapack jsons.", registeredSoil.size());
    }
}