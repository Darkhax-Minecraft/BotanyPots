package net.darkhax.botanypots.addons.crt;

import com.blamejared.crafttweaker.impl.commands.CTCommandCollectionEvent;

import net.darkhax.botanypots.BotanyPotHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CraftTweakerEventSubscription {
    
    @SubscribeEvent
    public static void addDumpCommands (CTCommandCollectionEvent event) {
        
        new BotanyDump("botanyCrops", "crops", BotanyPotHelper::getCropData).registerTo(event);
        new BotanyDump("botanyFertilizers", "fertilizers", BotanyPotHelper::getFertilizerData).registerTo(event);
        new BotanyDump("botanySoils", "soils", BotanyPotHelper::getSoilData).registerTo(event);
    }
}