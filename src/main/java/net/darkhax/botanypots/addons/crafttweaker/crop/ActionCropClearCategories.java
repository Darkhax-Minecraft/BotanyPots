package net.darkhax.botanypots.addons.crafttweaker.crop;

import net.darkhax.botanypots.BotanyPots;

public class ActionCropClearCategories extends ActionCrop {
    
    public ActionCropClearCategories(String id) {
        
        super(id);
    }
    
    @Override
    public void apply () {
        
        this.getCrop().getSoilCategories().clear();
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script has cleared categories for crop {}.", this.getId());
        return "[Botany Pots] Clearing categories for crop " + this.getId();
    }
    
}
