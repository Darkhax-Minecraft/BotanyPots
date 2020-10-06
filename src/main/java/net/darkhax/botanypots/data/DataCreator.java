package net.darkhax.botanypots.data;

import net.darkhax.botanypots.BotanyPots;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = BotanyPots.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataCreator {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        if (event.includeServer()) {
            generator.addProvider(new Recipes(generator));
        }
    }

}
