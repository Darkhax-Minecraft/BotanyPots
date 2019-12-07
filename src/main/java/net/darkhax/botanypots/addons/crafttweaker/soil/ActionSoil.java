package net.darkhax.botanypots.addons.crafttweaker.soil;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.logger.ILogger;

import net.darkhax.botanypots.soil.SoilInfo;
import net.darkhax.botanypots.soil.SoilReloadListener;
import net.minecraft.util.ResourceLocation;

public abstract class ActionSoil implements IRuntimeAction {
    
    private final String id;
    private final SoilInfo targetSoil;
    
    public ActionSoil(String id) {
        
        this.id = id;
        this.targetSoil = SoilReloadListener.registeredSoil.get(ResourceLocation.tryCreate(id));
    }
    
    public String getId () {
        
        return this.id;
    }
    
    public SoilInfo getSoil () {
        
        return this.targetSoil;
    }
    
    @Override
    public boolean validate (ILogger logger) {
        
        if (this.targetSoil == null) {
            
            logger.error("[BotanyPots] No soil found for provided id " + this.id + ".");
        }
        
        return this.targetSoil != null;
    }
}