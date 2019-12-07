package net.darkhax.botanypots.addons.crafttweaker.soil;

import net.darkhax.botanypots.BotanyPots;

public class ActionSoilClearCategories extends ActionSoil {
    
    public ActionSoilClearCategories(String id) {
        
        super(id);
    }
    
    @Override
    public void apply () {
        
        if (this.getSoil() != null) {
            
            this.getSoil().getCategories().clear();
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script cleared soil categories for soil {}.", this.getId());
        return "[BotanyPots] Clearing soil categories for " + this.getId() + ".";
    }
}
