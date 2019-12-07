package net.darkhax.botanypots.addons.crafttweaker.crop;

import net.darkhax.botanypots.BotanyPots;

public class ActionCropTickRate extends ActionCrop {
    
    private final int tickRate;
    
    public ActionCropTickRate(String id, int ticks) {
        
        super(id);
        
        this.tickRate = ticks;
    }
    
    @Override
    public void apply () {
        
        this.getCrop().setGrowthTicks(this.tickRate);
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script modified tick rate for crop {} to {}.", this.getId(), this.tickRate);
        return "[Botany Pots] Modifying tick rate for crop " + this.getId() + " to " + this.tickRate;
    }
    
}
