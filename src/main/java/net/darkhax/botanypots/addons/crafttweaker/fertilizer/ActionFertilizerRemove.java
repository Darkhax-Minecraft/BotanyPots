package net.darkhax.botanypots.addons.crafttweaker.fertilizer;

import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;

import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPots;

public class ActionFertilizerRemove extends ActionFertilizer {
    
    public ActionFertilizerRemove(String id) {
        
        super(id);
    }
    
    @Override
    public void apply () {
        
        if (this.getFertilizer() != null) {
            
            BotanyPotHelper.getFertilizerData(CTCraftingTableManager.recipeManager).remove(this.getFertilizer().getId());
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker Script removed fertilizer {}.", this.getId());
        return "[Botany Pots] Removed fertilizer " + this.getId();
    }
    
}
