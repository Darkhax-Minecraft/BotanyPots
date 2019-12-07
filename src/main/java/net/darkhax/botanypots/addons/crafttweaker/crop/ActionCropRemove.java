package net.darkhax.botanypots.addons.crafttweaker.crop;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.CropReloadListener;

public class ActionCropRemove extends ActionCrop {
    
    public ActionCropRemove(String id) {
        
        super(id);
    }
    
    @Override
    public void apply () {
        
        CropReloadListener.registeredCrops.remove(this.getCrop().getId());
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script has removed crop {}.", this.getId());
        return "[Botany Pots] Removing crop " + this.getId();
    }
    
}
