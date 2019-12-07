package net.darkhax.botanypots.addons.crafttweaker.crop;

import java.util.Arrays;

import net.darkhax.botanypots.BotanyPots;

public class ActionCropRemoveCategory extends ActionCrop {
    
    private final String[] categories;
    
    public ActionCropRemoveCategory(String id, String[] categories) {
        
        super(id);
        this.categories = categories;
    }
    
    @Override
    public void apply () {
        
        for (final String cat : this.categories) {
            
            this.getCrop().getSoilCategories().remove(cat);
        }
    }
    
    @Override
    public String describe () {
        
        final String catString = Arrays.toString(this.categories);
        BotanyPots.LOGGER.info("A CraftTweaker script has removed categories {} to crop {}.", catString, this.getId());
        return "[Botany Pots] Removed categories " + catString + " to crop " + this.getId();
    }
    
}
