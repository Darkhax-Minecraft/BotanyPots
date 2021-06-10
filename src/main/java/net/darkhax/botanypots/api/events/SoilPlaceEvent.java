package net.darkhax.botanypots.api.events;

import javax.annotation.Nullable;

import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This class contains events that are fired when a player attempts to place soil in a pot.
 */
public class SoilPlaceEvent extends BotanyPotEvent.Player {
    
    /**
     * The soil being placed.
     */
    protected final SoilInfo soil;
    
    public SoilPlaceEvent(TileEntityBotanyPot pot, SoilInfo soil, @Nullable PlayerEntity player) {
        
        super(pot, player);
        this.soil = soil;
    }
    
    /**
     * This event is fired on the Forge event bus just before a soil has been placed into the
     * pot. This event can be used to change the soil placed in the pot or prevent the soil
     * from being placed.
     */
    @Cancelable
    public static class Pre extends SoilPlaceEvent {
        
        /**
         * The soil to actually add to the pot. This will replace the original soil.
         */
        @Nullable
        private SoilInfo soilToAdd;
        
        public Pre(TileEntityBotanyPot pot, SoilInfo soil, PlayerEntity player) {
            
            super(pot, soil, player);
            this.soilToAdd = soil;
        }
        
        /**
         * Gets the current soil being placed in the botany pot. If the final result is null no
         * soil should be placed.
         * 
         * @return The soil to place in the pot.
         */
        @Nullable
        public SoilInfo getCurrentSoil () {
            
            return this.soilToAdd;
        }
        
        /**
         * Sets the soil to place in the botany pot. This will override the initial soil.
         * Setting this to null will result in no soil being placed.
         * 
         * @param soil The new soil to place. A null soil will prevent the soil from being
         *        placed.
         */
        public void setSoil (@Nullable SoilInfo soil) {
            
            this.soilToAdd = soil;
        }
        
        /**
         * Gets the original soil being placed in the pot. The soil actually being placed can
         * be accessed using {@link #getCurrentSoil()}.
         * 
         * @return The original soil being placed in the pot.
         */
        public SoilInfo getOriginalSoil () {
            
            return this.soil;
        }
    }
    
    /**
     * This event is fired on the Forge event bus just after a soil has been placed in a pot.
     * This event can not be canceled.
     */
    public static class Post extends SoilPlaceEvent {
        
        public Post(TileEntityBotanyPot pot, SoilInfo soil, PlayerEntity player) {
            
            super(pot, soil, player);
        }
        
        /**
         * Gets the soil that was placed.
         * 
         * @return The soil that was placed.
         */
        public SoilInfo getSoil () {
            
            return this.soil;
        }
    }
}