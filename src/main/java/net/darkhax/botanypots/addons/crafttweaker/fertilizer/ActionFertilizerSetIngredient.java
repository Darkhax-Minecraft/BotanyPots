package net.darkhax.botanypots.addons.crafttweaker.fertilizer;

import com.blamejared.crafttweaker.api.item.IIngredient;

import net.darkhax.botanypots.BotanyPots;

public class ActionFertilizerSetIngredient extends ActionFertilizer {
    
    private final IIngredient ingredient;
    
    public ActionFertilizerSetIngredient(String id, IIngredient ingredient) {
        
        super(id);
        this.ingredient = ingredient;
    }
    
    @Override
    public void apply () {
        
        if (this.getFertilizer() != null) {
            
            this.getFertilizer().setIngredient(this.ingredient.asVanillaIngredient());
        }
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker Script changed ingredient for fertilizer {} to {}.", this.getId(), this.ingredient.getCommandString());
        return "[Botany Pots] Changed ingredient for fertilizer " + this.getId() + " to " + this.ingredient.getCommandString();
    }
    
}
