package net.darkhax.botanypots.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.crop.CropReloadListener;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.fertilizer.FertilizerReloadListener;
import net.darkhax.botanypots.soil.SoilInfo;
import net.darkhax.botanypots.soil.SoilReloadListener;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class BotanyPotsClient {
    
    public static MessageSyncRegistries decodeRegistriesMessage (PacketBuffer buffer) {
        
        final Map<ResourceLocation, SoilInfo> soils = new HashMap<>();
        final int soilCount = buffer.readInt();
        
        for (int i = 0; i < soilCount; i++) {
            
            final SoilInfo soil = SoilInfo.deserialize(buffer);
            soils.put(soil.getId(), soil);
        }
        
        final Map<ResourceLocation, CropInfo> crops = new HashMap<>();
        final int cropCount = buffer.readInt();
        
        for (int i = 0; i < cropCount; i++) {
            
            final CropInfo crop = CropInfo.deserialize(buffer);
            crops.put(crop.getId(), crop);
        }
        
        final Map<ResourceLocation, FertilizerInfo> fertilizers = new HashMap<>();
        final int fertilizerCount = buffer.readInt();
        
        for (int i = 0; i < fertilizerCount; i++) {
            
            final FertilizerInfo fertilizer = FertilizerInfo.deserialize(buffer);
            fertilizers.put(fertilizer.getId(), fertilizer);
        }
        
        return new MessageSyncRegistries(soils, crops, fertilizers);
    }
    
    public static void processRegistriesMessage (MessageSyncRegistries message, Supplier<Context> context) {
        
        SoilReloadListener.registeredSoil = message.getSoils();
        BotanyPots.LOGGER.info("Received {} soil entries from the server.", SoilReloadListener.registeredSoil.size());
        
        CropReloadListener.registeredCrops = message.getCrops();
        BotanyPots.LOGGER.info("Received {} crop entries from the server.", CropReloadListener.registeredCrops.size());
        
        FertilizerReloadListener.registeredFertilizer = message.getFertilzers();
        BotanyPots.LOGGER.info("Received {} fertilizers from the server.", FertilizerReloadListener.registeredFertilizer.size());
    }
}
