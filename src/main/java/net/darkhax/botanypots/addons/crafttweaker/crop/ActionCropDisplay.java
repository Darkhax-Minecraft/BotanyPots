package net.darkhax.botanypots.addons.crafttweaker.crop;

import com.blamejared.crafttweaker.impl.blocks.MCBlockState;

import net.darkhax.botanypots.BotanyPots;
import net.minecraft.block.BlockState;

public class ActionCropDisplay extends ActionCrop {
    
    private final MCBlockState[] states;
    
    public ActionCropDisplay(String id, MCBlockState... states) {
        
        super(id);
        this.states = states;
    }
    
    @Override
    public void apply () {
        
        final BlockState[] asStates = new BlockState[this.states.length];
        
        for (int i = 0; i < asStates.length; i++) {
            
            asStates[i] = this.states[i].getInternal();
        }
        
        this.getCrop().setDisplayBlock(asStates);
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script has changed display of crop {} to {}.", this.getId(), this.states[0].getCommandString());
        return "[Botany Pots] Changed display of crop " + this.getId() + " to " + this.states[0].getCommandString();
    }
    
}
