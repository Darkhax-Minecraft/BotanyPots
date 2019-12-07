package net.darkhax.botanypots.addons.crafttweaker.crop;

import com.blamejared.crafttweaker.api.item.IItemStack;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.HarvestEntry;

public class ActionCropAddDrop extends ActionCrop {
    
    private final IItemStack item;
    private final float chance;
    private final int min;
    private final int max;
    
    public ActionCropAddDrop(String id, IItemStack item, float chance, int min, int max) {
        
        super(id);
        
        this.item = item;
        this.chance = chance;
        this.min = min;
        this.max = max;
    }
    
    @Override
    public void apply () {
        
        this.getCrop().getResults().add(new HarvestEntry(this.chance, this.item.getInternal(), this.min, this.max));
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script has added drop {} to crop {}.", this.item.getCommandString(), this.getId());
        return "[Botany Pots] Added drop" + this.item.getCommandString() + " to crop " + this.getId();
    }
    
}
