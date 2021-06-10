package net.darkhax.botanypots.api.events;

import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;

/**
 * These events are fired on the Forge event bus when a botany pot ticks.
 */
public class BotanyPotTickEvent extends BotanyPotEvent {
    
    public BotanyPotTickEvent(TileEntityBotanyPot pot) {
        
        super(pot);
    }
    
    /**
     * This event is fired on the Forge event bus just before the botany pot begins it's tick.
     */
    public static class Pre extends BotanyPotTickEvent {
        
        public Pre(TileEntityBotanyPot pot) {
            
            super(pot);
        }
    }
    
    /**
     * This event is fired on the Forge event bus just after the botany pot finishes it's tick.
     */
    public static class Post extends BotanyPotTickEvent {
        
        public Post(TileEntityBotanyPot pot) {
            
            super(pot);
        }
    }
}