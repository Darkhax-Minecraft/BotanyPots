package net.darkhax.botanypots.network;

import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.network.PacketBuffer;

public class BotanyPotsServer {
    
    public static void encodeRegistriesMessage (MessageSyncRegistries packet, PacketBuffer buffer) {
        
        buffer.writeInt(packet.getSoils().size());
        
        for (final SoilInfo soilInfo : packet.getSoils().values()) {
            
            SoilInfo.serialize(buffer, soilInfo);
        }
        
        buffer.writeInt(packet.getCrops().size());
        
        for (final CropInfo cropInfo : packet.getCrops().values()) {
            
            CropInfo.serialize(buffer, cropInfo);
        }
        
        buffer.writeInt(packet.getFertilzers().size());
        
        for (final FertilizerInfo fertilizerInfo : packet.getFertilzers().values()) {
            
            FertilizerInfo.serialize(buffer, fertilizerInfo);
        }
    }
}