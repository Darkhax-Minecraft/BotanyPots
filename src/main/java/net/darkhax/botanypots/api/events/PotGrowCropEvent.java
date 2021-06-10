package net.darkhax.botanypots.api.events;

import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * These events are fired on the Forge event bus when a crop grows.
 */
public class PotGrowCropEvent extends BotanyPotEvent {
    
    /**
     * The amount of ticks the crop is growing.
     */
    protected final int amount;
    
    public PotGrowCropEvent(TileEntityBotanyPot pot, int amount) {
        
        super(pot);
        this.amount = amount;
    }
    
    /**
     * This event is fired on the Forge event bus just before the crop grows. Cancelling this
     * event will prevent the crop from growing.
     */
    @Cancelable
    public static class Pre extends PotGrowCropEvent {
        
        /**
         * The amount of growth ticks to apply.
         */
        private int newAmount;
        
        public Pre(TileEntityBotanyPot pot, int amount) {
            
            super(pot, amount);
            this.newAmount = amount;
        }
        
        /**
         * Gets the original growth tick amount.
         * 
         * @return The original growth tick amount.
         */
        public int getOriginalAmount () {
            
            return this.amount;
        }
        
        /**
         * Gets the current amount of growth ticks.
         * 
         * @return The current amount of growth ticks.
         */
        public int getCurrentAmount () {
            
            return this.newAmount;
        }
        
        /**
         * Sets the new amount of ticks to apply. If a negative value is specified the crop
         * will ungrow.
         * 
         * @param amount The amount of growth ticks to apply.
         */
        public void setAmount (int amount) {
            
            this.newAmount = amount;
        }
    }
    
    /**
     * This event is fired on the Forge event bus after a crop has grown in the botany pot.
     */
    public static class Post extends PotGrowCropEvent {
        
        public Post(TileEntityBotanyPot pot, int amount) {
            
            super(pot, amount);
        }
        
        /**
         * The amount of ticks the crop grew by.
         * 
         * @return The amount of ticks the crop grew by.
         */
        public int getGrowthAmount () {
            
            return this.amount;
        }
    }
}