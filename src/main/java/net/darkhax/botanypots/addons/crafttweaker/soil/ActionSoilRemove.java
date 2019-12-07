package net.darkhax.botanypots.addons.crafttweaker.soil;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.soil.SoilReloadListener;

public class ActionSoilRemove extends ActionSoil {
    
    public ActionSoilRemove(String id) {
        
        super(id);
    }
    
    @Override
    public void apply () {
        
        if (this.getSoil() != null) {
            
            SoilReloadListener.registeredSoil.remove(this.getSoil().getId());
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script removed soil {}.", this.getId());
        return "[BotanyPots] Removing soil " + this.getId();
    }
}
