package net.darkhax.botanypots.addons.crafttweaker.soil;

import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;

import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPots;

public class ActionSoilRemove extends ActionSoil {
    
    public ActionSoilRemove(String id) {
        
        super(id);
    }
    
    @Override
    public void apply () {
        
        if (this.getSoil() != null) {
            
            BotanyPotHelper.getSoilData(CTCraftingTableManager.recipeManager).remove(this.getSoil().getId());
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script removed soil {}.", this.getId());
        return "[BotanyPots] Removing soil " + this.getId();
    }
}
