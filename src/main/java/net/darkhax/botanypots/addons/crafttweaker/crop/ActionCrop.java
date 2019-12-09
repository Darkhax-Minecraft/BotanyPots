package net.darkhax.botanypots.addons.crafttweaker.crop;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.logger.ILogger;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;

import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.crop.CropInfo;
import net.minecraft.util.ResourceLocation;

public abstract class ActionCrop implements IRuntimeAction {
    
    private final String id;
    private final CropInfo targetFertilizer;
    
    public ActionCrop(String id) {
        
        this.id = id;
        this.targetFertilizer = BotanyPotHelper.getCrop(CTCraftingTableManager.recipeManager, ResourceLocation.tryCreate(id));
    }
    
    public String getId () {
        
        return this.id;
    }
    
    public CropInfo getCrop () {
        
        return this.targetFertilizer;
    }
    
    @Override
    public boolean validate (ILogger logger) {
        
        if (this.targetFertilizer == null) {
            
            logger.error("[BotanyPots] No crop found for provided id " + this.id + ".");
        }
        
        return this.targetFertilizer != null;
    }
}