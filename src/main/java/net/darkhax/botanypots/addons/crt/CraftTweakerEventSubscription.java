package net.darkhax.botanypots.addons.crt;

import static net.darkhax.botanypots.BotanyPots.MOD_ID;

import java.util.HashSet;
import java.util.Set;

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
        
        final Set<BotanyDump> dumps = new HashSet<>();
        
        dumps.add(new BotanyDump("botanyCrops", "crops", BotanyPotHelper::getCropData));
        dumps.add(new BotanyDump("botanyFertilizers", "fertilizers", BotanyPotHelper::getFertilizerData));
        dumps.add(new BotanyDump("botanySoils", "soils", BotanyPotHelper::getSoilData));
        
        for (final BotanyDump dump : dumps) {
            dump.registerTo(event);
        }
    }
}
