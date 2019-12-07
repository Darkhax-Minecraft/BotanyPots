package net.darkhax.botanypots.addons.crafttweaker.fertilizer;

import net.darkhax.botanypots.BotanyPots;

public class ActionFertilizerSetTicks extends ActionFertilizer {
    
    private final int min;
    private final int max;
    
    public ActionFertilizerSetTicks(String id, int min, int max) {
        
        super(id);
        this.min = min;
        this.max = max;
    }
    
    @Override
    public void apply () {
        
        if (this.getFertilizer() != null) {
            
            this.getFertilizer().setMinTicks(this.min);
            this.getFertilizer().setMaxTicks(this.max);
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker Script changed fertilizer {} ticks to {} and {}.", this.getId(), this.min, this.max);
        return "[Botany Pots] Changed fertilizer " + this.getId() + " ticks to " + this.min + " and " + this.max;
    }
    
}
