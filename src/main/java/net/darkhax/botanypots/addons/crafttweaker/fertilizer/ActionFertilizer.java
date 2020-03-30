package net.darkhax.botanypots.addons.crafttweaker.fertilizer;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.logger.ILogger;

import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.minecraft.util.ResourceLocation;

public abstract class ActionFertilizer implements IRuntimeAction {
    
    private final String id;
    private final FertilizerInfo targetFertilizer;
    
    public ActionFertilizer(String id) {
        
        this.id = id;
        this.targetFertilizer = BotanyPotHelper.getFertilizer(ResourceLocation.tryCreate(id));
    }
    
    public String getId () {
        
        return this.id;
    }
    
    public FertilizerInfo getFertilizer () {
        
        return this.targetFertilizer;
    }
    
    @Override
    public boolean validate (ILogger logger) {
        
        if (this.targetFertilizer == null) {
            
            logger.error("[BotanyPots] No fertilizer found for provided id " + this.id + ".");
        }
        
        return this.targetFertilizer != null;
    }
}