package net.darkhax.botanypots.addons.crafttweaker.crop;

import java.util.Arrays;

import net.darkhax.botanypots.BotanyPots;

public class ActionCropAddCategory extends ActionCrop {
    
    private final String[] categories;
    
    public ActionCropAddCategory(String id, String[] categories) {
        
        super(id);
        this.categories = categories;
    }
    
    @Override
    public void apply () {
        
        for (final String cat : this.categories) {
            
            this.getCrop().getSoilCategories().add(cat);
        }
    }
    
    @Override
    public String describe () {
        
        final String catString = Arrays.toString(this.categories);
        BotanyPots.LOGGER.info("A CraftTweaker script has added categories {} to crop {}.", catString, this.getId());
        return "[Botany Pots] Added categories " + catString + " to crop " + this.getId();
    }
    
}
