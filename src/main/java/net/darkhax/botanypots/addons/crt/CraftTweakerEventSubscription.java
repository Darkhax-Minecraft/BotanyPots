package net.darkhax.botanypots.addons.crt;

import static net.darkhax.botanypots.BotanyPots.MOD_ID;

import com.blamejared.crafttweaker.impl.commands.CTCommandCollectionEvent;
import com.blamejared.crafttweaker.impl.commands.script_examples.ExampleCollectionEvent;

import net.darkhax.botanypots.BotanyPotHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CraftTweakerEventSubscription {
    
    @SubscribeEvent
    public static void addExampleScriptFiles (ExampleCollectionEvent event) {
        
        event.addResource(new ResourceLocation(MOD_ID, "botanypots/crops"));
        event.addResource(new ResourceLocation(MOD_ID, "botanypots/fertilizers"));
        event.addResource(new ResourceLocation(MOD_ID, "botanypots/soils"));
    }
    
    @SubscribeEvent
    public static void addDumpCommands (CTCommandCollectionEvent event) {
        
        new BotanyDump("botanyCrops", "crops", BotanyPotHelper::getCropData).registerTo(event);
        new BotanyDump("botanyFertilizers", "fertilizers", BotanyPotHelper::getFertilizerData).registerTo(event);
        new BotanyDump("botanySoils", "soils", BotanyPotHelper::getSoilData).registerTo(event);
    }
}