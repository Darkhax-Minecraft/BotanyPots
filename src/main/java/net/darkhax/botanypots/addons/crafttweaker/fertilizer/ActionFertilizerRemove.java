package net.darkhax.botanypots.addons.crafttweaker.fertilizer;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.fertilizer.FertilizerReloadListener;

public class ActionFertilizerRemove extends ActionFertilizer {
    
    public ActionFertilizerRemove(String id) {
        
        super(id);
    }
    
    @Override
    public void apply () {
        
        if (this.getFertilizer() != null) {
            
            FertilizerReloadListener.registeredFertilizer.remove(this.getFertilizer().getId());
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker Script removed fertilizer {}.", this.getId());
        return "[Botany Pots] Removed fertilizer " + this.getId();
    }
    
}
