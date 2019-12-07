package net.darkhax.botanypots.addons.crafttweaker.crop;

import com.blamejared.crafttweaker.api.item.IIngredient;

import net.darkhax.botanypots.BotanyPots;
import net.minecraft.item.crafting.Ingredient;

public class ActionCropRemoveDrop extends ActionCrop {
    
    private final IIngredient seed;
    
    public ActionCropRemoveDrop(String id, IIngredient toRemove) {
        
        super(id);
        
        this.seed = toRemove;
    }
    
    @Override
    public void apply () {
        
        final Ingredient ingredient = this.seed.asVanillaIngredient();
        this.getCrop().getResults().removeIf(drop -> ingredient.test(drop.getItem()));
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script removed all {} from {}'s drops", this.seed.getCommandString(), this.getId());
        return "[Botany Pots] Removed all " + this.seed.getCommandString() + " from " + this.getId() + "'s drops.";
    }
    
}
