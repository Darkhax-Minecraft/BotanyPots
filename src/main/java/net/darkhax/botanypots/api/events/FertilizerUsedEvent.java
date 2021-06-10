package net.darkhax.botanypots.api.events;

import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * These events are fired on the Forge event bus when a player uses a fertilizer on a botany
 * pot.
 */
public class FertilizerUsedEvent extends BotanyPotEvent {
    
    /**
     * The player using the fertilizer.
     */
    private final PlayerEntity player;
    
    /**
     * The fertilizer being used.
     */
    private final FertilizerInfo fertilizer;
    
    /**
     * The fertilizer item stack.
     */
    private final ItemStack stack;
    
    /**
     * The amount of growth ticks being applied.
     */
    protected final int growthTicks;
    
    public FertilizerUsedEvent(TileEntityBotanyPot pot, PlayerEntity player, FertilizerInfo info, ItemStack stack, int growthTicks) {
        
        super(pot);
        this.player = player;
        this.fertilizer = info;
        this.stack = stack;
        this.growthTicks = growthTicks;
    }
    
    /**
     * Gets the player using the fertilizer.
     * 
     * @return The player using the fertilizer.
     */
    public PlayerEntity getPlayer () {
        
        return this.player;
    }
    
    /**
     * Gets the fertilizer being used.
     * 
     * @return The fertilizer being used.
     */
    public FertilizerInfo getFertilizer () {
        
        return this.fertilizer;
    }
    
    /**
     * Gets the ItemStack being used as a fertilizer.
     * 
     * @return The item being used as a fertilizer.
     */
    public ItemStack getFertilizerStack () {
        
        return this.stack;
    }
    
    /**
     * This event is fired on the Forge event bus just before the player uses a fertilizer on
     * the botany pot. Using this event you can prevent the fertilizer from being used or
     * changing the amount of growth being added. If the growth ticks are 0 or less the
     * fertilizer will not be used.
     */
    @Cancelable
    public static class Pre extends FertilizerUsedEvent {
        
        /**
         * The actual growth tick value to be used.
         */
        private int currentGrowthTicks;
        
        public Pre(TileEntityBotanyPot pot, PlayerEntity player, FertilizerInfo info, ItemStack stack, int growthTicks) {
            
            super(pot, player, info, stack, growthTicks);
            this.currentGrowthTicks = growthTicks;
        }
        
        /**
         * Gets the original and unmodified growth ticks for the fertilizer.
         * 
         * @return The original and unmodified growth ticks for the fertilizer.
         */
        public int getOriginalGrowthTicks () {
            
            return this.growthTicks;
        }
        
        /**
         * Gets the current growth ticks. The final result will be used as the actual growth
         * value to apply. If the value is less than one the fertilizer will not be used.
         * 
         * @return The current growth ticks.
         */
        public int getCurrentGrowthTicks () {
            
            return this.currentGrowthTicks;
        }
        
        /**
         * Sets the growth ticks for the fertilizer to apply.
         * 
         * @param growthTicks The amount of growth ticks to apply. If this is less than one the
         *        fertilizer will not be applied.
         */
        public void setGrowthTicks (int growthTicks) {
            
            this.currentGrowthTicks = growthTicks;
        }
    }
    
    /**
     * This event is fired on the Forge event bus after a fertilizer has been used on a crop.
     */
    public static class Post extends FertilizerUsedEvent {
        
        public Post(TileEntityBotanyPot pot, PlayerEntity player, FertilizerInfo info, ItemStack stack, int growthTicks) {
            
            super(pot, player, info, stack, growthTicks);
        }
        
        /**
         * The amount of growth ticks applied by the fertilizer.
         * 
         * @return The amount of growth ticks applied by the fertilizer.
         */
        public int getGrowthTicks () {
            
            return this.growthTicks;
        }
    }
}