package net.darkhax.botanypots.addons.crafttweaker.fertilizer;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.item.IIngredient;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.fertilizer.FertilizerReloadListener;
import net.minecraft.util.ResourceLocation;

public class ActionFertilizerCreate implements IRuntimeAction {
    
    private final FertilizerInfo info;
    
    public ActionFertilizerCreate(String id, IIngredient ingredient, int min, int max) {
        
        this.info = new FertilizerInfo(ResourceLocation.tryCreate(id), ingredient.asVanillaIngredient(), min, max);
    }
    
    @Override
    public void apply () {
        
        FertilizerReloadListener.registeredFertilizer.put(this.info.getId(), this.info);
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script created a new fertilizer with ID {}.", this.info.getId());
        return "[Botany Pots] Created new fertilizer with ID " + this.info.getId();
    }
    
}