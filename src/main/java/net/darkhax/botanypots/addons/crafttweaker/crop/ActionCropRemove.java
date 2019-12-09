package net.darkhax.botanypots.addons.crafttweaker.crop;

import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;

import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPots;

public class ActionCropRemove extends ActionCrop {
    
    public ActionCropRemove(String id) {
        
        super(id);
    }
    
    @Override
    public void apply () {
        
        BotanyPotHelper.getCropData(CTCraftingTableManager.recipeManager).remove(this.getCrop().getId());
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script has removed crop {}.", this.getId());
        return "[Botany Pots] Removing crop " + this.getId();
    }
    
}
