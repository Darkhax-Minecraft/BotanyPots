package net.darkhax.botanypots.api.events;

import javax.annotation.Nullable;

import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This class contains events that are fired when a player attempts to remove soil from the
 * pot.
 */
public class SoilRemoveEvent extends BotanyPotEvent.Player {
    
    /**
     * The soil being removed.
     */
    private final SoilInfo soil;
    
    public SoilRemoveEvent(TileEntityBotanyPot pot, SoilInfo soil, @Nullable PlayerEntity player) {
        
        super(pot, player);
        this.soil = soil;
    }
    
    /**
     * Gets the soil being removed.
     * 
     * @return The soil being removed.
     */
    public SoilInfo getSoil () {
        
        return this.soil;
    }
    
    /**
     * This event is fired on the Forge event bus just before a player removes a soil from the
     * pot. Canceling this event will prevent the soil from being removed.
     */
    @Cancelable
    public static class Pre extends SoilRemoveEvent {
        
        public Pre(TileEntityBotanyPot pot, SoilInfo soil, PlayerEntity player) {
            
            super(pot, soil, player);
        }
    }
    
    /**
     * This event is fired on the Forge event bus after a player removes soil from the pot.
     */
    public static class Post extends SoilRemoveEvent {
        
        public Post(TileEntityBotanyPot pot, SoilInfo soil, PlayerEntity player) {
            
            super(pot, soil, player);
        }
    }
}