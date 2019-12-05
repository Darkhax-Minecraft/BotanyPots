package net.darkhax.botanypots.api.crop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.darkhax.bookshelf.lib.TableBuilder;
import net.darkhax.bookshelf.util.ModUtils;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.api.fertilizer.FertilizerInfo;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class CropReloadListener extends JsonReloadListener {
    
    private static final Gson GSON_INSTANCE = new GsonBuilder().create();
    
    public static Map<ResourceLocation, CropInfo> registeredCrops = new HashMap<>();
    
    public CropReloadListener() {
        
        super(GSON_INSTANCE, "botanypots_crops");
    }
    
    @Override
    protected void apply (Map<ResourceLocation, JsonObject> splashList, IResourceManager manager, IProfiler profiler) {
        
        profiler.startSection("Processing crop JsonObjects");
        
        registeredCrops.clear();
        
        for (final Entry<ResourceLocation, JsonObject> entry : splashList.entrySet()) {
            
            if (registeredCrops.containsKey(entry.getKey())) {
                
                BotanyPots.LOGGER.warn("Duplicate JSON for {}. It will be overriden.", entry.getKey());
            }
            
            try {
                
                final JsonObject json = entry.getValue();
                
                // Allow crops to specify a required mod to load the file.
                if (json.has("requiredMod")) {
                    
                    final String requiredMod = JSONUtils.getString(json, "requiredMod");
                    
                    if (!ModUtils.isInModList(requiredMod)) {
                        
                        continue;
                    }
                }
                
                final CropInfo cropInfo = CropInfo.deserialize(entry.getKey(), json);
                registeredCrops.put(entry.getKey(), cropInfo);
            }
            
            catch (final Exception exception) {
                
                BotanyPots.LOGGER.error("Could not parse crop info JSON for {}.", entry.getKey());
                BotanyPots.LOGGER.catching(exception);
            }
        }
        
        profiler.endSection();
        
        BotanyPots.LOGGER.info("Loaded {} crop types from datapack jsons.", registeredCrops.size());
    }
}