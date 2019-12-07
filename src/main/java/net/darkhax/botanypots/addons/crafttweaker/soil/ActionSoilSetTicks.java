package net.darkhax.botanypots.addons.crafttweaker.soil;

import net.darkhax.botanypots.BotanyPots;

public class ActionSoilSetTicks extends ActionSoil {
    
    private final int ticks;
    
    public ActionSoilSetTicks(String id, int ticks) {
        
        super(id);
        this.ticks = ticks;
    }
    
    @Override
    public void apply () {
        
        if (this.getSoil() != null) {
            
            this.getSoil().setTickRate(this.ticks);
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script changed soil {} tick rate from {} to {}.", this.getId(), this.getSoil().getTickRate(), this.ticks);
        return "[BotanyPots] Changing soil tick rate of " + this.getId() + " from " + this.getSoil().getTickRate() + " to " + this.ticks + ".";
    }
}
