package net.darkhax.botanypots.addons.crafttweaker.crop;

import com.blamejared.crafttweaker.api.item.IIngredient;

import net.darkhax.botanypots.BotanyPots;

public class ActionCropSeed extends ActionCrop {
    
    private final IIngredient seed;
    
    public ActionCropSeed(String id, IIngredient seed) {
        
        super(id);
        
        this.seed = seed;
    }
    
    @Override
    public void apply () {
        
        this.getCrop().setSeed(this.seed.asVanillaIngredient());
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script modified seed for crop {} to {}.", this.getId(), this.seed.getCommandString());
        return "[Botany Pots] Modifying seed for crop " + this.getId() + " to " + this.seed.getCommandString();
    }
    
}
