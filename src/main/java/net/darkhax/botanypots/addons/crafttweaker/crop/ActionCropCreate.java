package net.darkhax.botanypots.addons.crafttweaker.crop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.logger.ILogger;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;

import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.crop.CropInfo;
import net.minecraft.util.ResourceLocation;

public class ActionCropCreate implements IRuntimeAction {
    
    private final String rawId;
    private final CropInfo newCrop;
    
    public ActionCropCreate(String id, IIngredient seed, MCBlockState display, int ticks, float multiplier, String[] categories) {
        
        this.rawId = id;
        this.newCrop = new CropInfo(ResourceLocation.tryCreate(id), seed.asVanillaIngredient(), new HashSet<>(Arrays.asList(categories)), ticks, multiplier, new ArrayList<>(), display.getInternal());
    }
    
    @Override
    public void apply () {
        
        BotanyPotHelper.getCropData(CTCraftingTableManager.recipeManager).put(this.newCrop.getId(), this.newCrop);
    }
    
    @Override
    public boolean validate (ILogger logger) {
        
        final boolean isValid = this.newCrop != null && this.newCrop.getId() != null;
        
        if (!isValid) {
            
            logger.error("Tried to register a crop with a null ID. This means your ID format was wrong. ID: " + this.rawId);
        }
        
        return isValid;
    }
    
    @Override
    public String describe () {
        
        return "[Botany Pots] Added new crop " + this.newCrop.getId();
    }
    
}
