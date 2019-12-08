package net.darkhax.botanypots.network;

import java.util.Map;

import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.crop.CropReloadListener;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.fertilizer.FertilizerReloadListener;
import net.darkhax.botanypots.soil.SoilInfo;
import net.darkhax.botanypots.soil.SoilReloadListener;
import net.minecraft.util.ResourceLocation;

public class MessageSyncRegistries {
    
    private final Map<ResourceLocation, SoilInfo> soils;
    private final Map<ResourceLocation, CropInfo> crops;
    private final Map<ResourceLocation, FertilizerInfo> fertilzers;
    
    public MessageSyncRegistries() {
        
        this(SoilReloadListener.registeredSoil, CropReloadListener.registeredCrops, FertilizerReloadListener.registeredFertilizer);
    }
    
    public MessageSyncRegistries(Map<ResourceLocation, SoilInfo> soils, Map<ResourceLocation, CropInfo> crops, Map<ResourceLocation, FertilizerInfo> fertilzers) {
        
        this.soils = soils;
        this.crops = crops;
        this.fertilzers = fertilzers;
    }
    
    public Map<ResourceLocation, SoilInfo> getSoils () {
        
        return this.soils;
    }
    
    public Map<ResourceLocation, CropInfo> getCrops () {
        
        return this.crops;
    }
    
    public Map<ResourceLocation, FertilizerInfo> getFertilzers () {
        
        return this.fertilzers;
    }
}