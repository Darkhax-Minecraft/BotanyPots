package net.darkhax.botanypots.addons.crafttweaker.crop;

import net.darkhax.botanypots.BotanyPots;

public class ActionCropGrowthModifier extends ActionCrop {
    
    private final float modifier;
    
    public ActionCropGrowthModifier(String id, float modifier) {
        
        super(id);
        this.modifier = modifier;
    }
    
    @Override
    public void apply () {
        
        this.getCrop().setGrowthMultiplier(this.modifier);
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script modified growth modifier for crop {} to {}.", this.getId(), this.modifier);
        return "[Botany Pots] Modifying growth modifier for crop " + this.getId() + " to " + this.modifier;
    }
}