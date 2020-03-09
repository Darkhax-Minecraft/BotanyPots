package net.darkhax.botanypots.addons.crafttweaker.soil;

import net.darkhax.botanypots.BotanyPots;

public class ActionSoilSetGrowthModifier extends ActionSoil {
    
    private final float modifier;
    
    public ActionSoilSetGrowthModifier(String id, float modifier) {
        
        super(id);
        this.modifier = modifier;
    }
    
    @Override
    public void apply () {
        
        if (this.getSoil() != null) {
            
            this.getSoil().setGrowthModifier(this.modifier);
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script changed soil {} growth modifier from {} to {}.", this.getId(), this.getSoil().getGrowthModifier(), this.modifier);
        return "[BotanyPots] Changing soil growth modifier of " + this.getId() + " from " + this.getSoil().getGrowthModifier() + " to " + this.modifier + ".";
    }
}
