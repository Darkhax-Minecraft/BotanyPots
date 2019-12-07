package net.darkhax.botanypots.addons.crafttweaker.soil;

import java.util.Arrays;

import net.darkhax.botanypots.BotanyPots;

public class ActionSoilRemoveCategory extends ActionSoil {
    
    private final String[] categories;
    
    public ActionSoilRemoveCategory(String id, String... categories) {
        
        super(id);
        
        this.categories = categories;
    }
    
    @Override
    public void apply () {
        
        if (this.getSoil() != null) {
            
            for (final String category : this.categories) {
                
                this.getSoil().getCategories().remove(category);
            }
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script removed categories {} to soil {}.", Arrays.toString(this.categories), this.getId());
        return "[BotanyPots] Removing categories " + Arrays.toString(this.categories) + " to soil " + this.getId() + ".";
    }
}
