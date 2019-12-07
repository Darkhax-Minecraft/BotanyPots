package net.darkhax.botanypots.addons.crafttweaker.soil;

import com.blamejared.crafttweaker.api.item.IIngredient;

import net.darkhax.botanypots.BotanyPots;

public class ActionSoilSetIngredient extends ActionSoil {
    
    private final IIngredient ingredient;
    
    public ActionSoilSetIngredient(String id, IIngredient ingredient) {
        
        super(id);
        this.ingredient = ingredient;
    }
    
    @Override
    public void apply () {
        
        if (this.getSoil() != null) {
            
            this.getSoil().setIngredient(this.ingredient.asVanillaIngredient());
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script changed soil ingredient for {} to {}.", this.getId(), this.ingredient.toString());
        return "[BotanyPots] Changing soil ingredient for " + this.getId() + " to " + this.ingredient.toString() + ".";
    }
}
