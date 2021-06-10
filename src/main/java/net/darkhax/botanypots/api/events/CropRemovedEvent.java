package net.darkhax.botanypots.api.events;

import javax.annotation.Nullable;

import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.crop.CropInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This class contains events that are fired on the Forge event bus when a player tries to
 * remove a crop from the Botany Pot.
 */
public class CropRemovedEvent extends BotanyPotEvent.Player {
    
    /**
     * The crop being removed.
     */
    protected final CropInfo crop;
    
    public CropRemovedEvent(TileEntityBotanyPot pot, @Nullable PlayerEntity player, CropInfo crop) {
        
        super(pot, player);
        this.crop = crop;
    }
    
    /**
     * Gets the crop being removed.
     * 
     * @return The crop being removed.
     */
    public CropInfo getCrop () {
        
        return this.crop;
    }
    
    /**
     * This event is fired on the Forge event bus just before a player removes a crop from a
     * Botany Pot. Canceling this event will prevent the crop from being removed.
     */
    @Cancelable
    public static class Pre extends CropRemovedEvent {
        
        public Pre(TileEntityBotanyPot pot, PlayerEntity player, CropInfo crop) {
            
            super(pot, player, crop);
        }
    }
    
    /**
     * This event is fired on the Forge event bus just after a player removes a crop from a
     * Botany Pot.
     */
    public static class Post extends CropRemovedEvent {
        
        public Post(TileEntityBotanyPot pot, PlayerEntity player, CropInfo soil) {
            
            super(pot, player, soil);
        }
    }
}