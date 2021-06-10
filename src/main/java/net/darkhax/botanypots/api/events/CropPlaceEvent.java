package net.darkhax.botanypots.api.events;

import javax.annotation.Nullable;

import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.crop.CropInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This class contains events that are fired on the Forge event bus when a player tries to
 * plant a crop in a Botany Pot.
 */
public class CropPlaceEvent extends BotanyPotEvent.Player {
    
    /**
     * The crop being planted.
     */
    protected final CropInfo crop;
    
    public CropPlaceEvent(TileEntityBotanyPot pot, @Nullable PlayerEntity player, CropInfo crop) {
        
        super(pot, player);
        this.crop = crop;
    }
    
    /**
     * This event is fired on the Forge event bus just before a player plants a crop in a
     * Botany Pot. This event can be used to change the crop being planted or prevent the crop
     * from being planted.
     */
    @Cancelable
    public static class Pre extends CropPlaceEvent {
        
        /**
         * The actual crop to place.
         */
        @Nullable
        private CropInfo cropToPlace;
        
        public Pre(TileEntityBotanyPot pot, PlayerEntity player, CropInfo crop) {
            
            super(pot, player, crop);
            this.cropToPlace = crop;
        }
        
        /**
         * Gets the current crop to be planted. If the final result is null no crop will be
         * planted.
         * 
         * @return The crop to be planted.
         */
        @Nullable
        public CropInfo getCurrentCrop () {
            
            return this.cropToPlace;
        }
        
        /**
         * Sets the crop to plant. Setting this to null will prevent the crop from being
         * planted.
         * 
         * @param crop The new crop to be planted.
         */
        public void setCrop (@Nullable CropInfo crop) {
            
            this.cropToPlace = crop;
        }
        
        /**
         * Gets the original and unmodified crop trying to be planted.
         * 
         * @return The original crop trying to be planted.
         */
        public CropInfo getOriginalCrop () {
            
            return this.crop;
        }
    }
    
    /**
     * This event is fired on the Forge event bus just after the player has planted a crop in
     * the botany pot.
     */
    public static class Post extends CropPlaceEvent {
        
        public Post(TileEntityBotanyPot pot, PlayerEntity player, CropInfo soil) {
            
            super(pot, player, soil);
        }
        
        /**
         * Gets the crop that was planted.
         * 
         * @return The crop that was planted.
         */
        public CropInfo getCrop () {
            
            return this.crop;
        }
    }
}