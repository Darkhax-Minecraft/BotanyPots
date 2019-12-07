package net.darkhax.botanypots.addons.crafttweaker.soil;

import com.blamejared.crafttweaker.impl.blocks.MCBlockState;

import net.darkhax.botanypots.BotanyPots;

public class ActionSoilSetDisplay extends ActionSoil {
    
    private final MCBlockState state;
    
    public ActionSoilSetDisplay(String id, MCBlockState state) {
        
        super(id);
        this.state = state;
    }
    
    @Override
    public void apply () {
        
        if (this.getSoil() != null) {
            
            this.getSoil().setRenderState(this.state.getInternal());
        }
    }
    
    @Override
    public String describe () {
        
        System.out.println(this.state.getInternal().getClass());
        BotanyPots.LOGGER.info("A CraftTweaker script changed soil render for {} to {}.", this.getId(), this.state.getInternal());
        return "[BotanyPots] Changing soil render for " + this.getId() + " to " + this.state.getInternal().toString() + ".";
    }
}
