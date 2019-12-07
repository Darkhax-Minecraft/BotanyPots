package net.darkhax.botanypots.addons.crafttweaker.soil;

import java.util.Arrays;

import net.darkhax.botanypots.BotanyPots;

public class ActionSoilAddCategory extends ActionSoil {
    
    private final String[] categories;
    
    public ActionSoilAddCategory(String id, String... categories) {
        
        super(id);
        
        this.categories = categories;
    }
    
    @Override
    public void apply () {
        
        if (this.getSoil() != null) {
            
            for (final String category : this.categories) {
                
                this.getSoil().getCategories().add(category);
            }
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script added categories {} to soil {}.", Arrays.toString(this.categories), this.getId());
        return "[BotanyPots] Adding categories " + Arrays.toString(this.categories) + " to soil " + this.getId() + ".";
    }
}
