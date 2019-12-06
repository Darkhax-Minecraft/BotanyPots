package net.darkhax.botanypots.addons.crt;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.logger.ILogger;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.soil.SoilInfo;
import net.darkhax.botanypots.soil.SoilReloadListener;

public class ActionAddSoil implements IRuntimeAction {

    public final SoilInfo newInfo;

    public ActionAddSoil(SoilInfo newInfo) {
        
        this.newInfo = newInfo;
    }

    @Override
    public boolean validate(ILogger logger) {
        
        if (newInfo == null) {
            
            logger.error("Attempted to add a null soil info.");
            BotanyPots.LOGGER.error("A CraftTweaker script attempted to add a null soil.");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void apply() {
        
        SoilReloadListener.registeredSoil.put(this.newInfo.getId(), newInfo);
    }

    @Override
    public String describe() {

        final SoilInfo existingInfo = SoilReloadListener.registeredSoil.get(newInfo.getId());
        
        if (existingInfo != null)  {
            
            BotanyPots.LOGGER.warn("A CraftTweaker script has overwridden the soil {}.", existingInfo.getId());
            return "Overidding soil " + existingInfo.getId() + " with " + this.newInfo.toString();
        }
            
        return "Added new soil info for " + this.newInfo.toString();
    }
}
