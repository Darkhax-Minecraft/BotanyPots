package net.darkhax.botanypots.addons.crafttweaker.crop;

import com.blamejared.crafttweaker.impl.blocks.MCBlockState;

import net.darkhax.botanypots.BotanyPots;

public class ActionCropDisplay extends ActionCrop {
    
    private final MCBlockState state;
    
    public ActionCropDisplay(String id, MCBlockState state) {
        
        super(id);
        this.state = state;
    }
    
    @Override
    public void apply () {
        
        this.getCrop().setDisplayBlock(this.state.getInternal());
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script has changed display of crop {} to {}.", this.getId(), this.state.getCommandString());
        return "[Botany Pots] Changed display of crop " + this.getId() + " to " + this.state.getCommandString();
    }
    
}
