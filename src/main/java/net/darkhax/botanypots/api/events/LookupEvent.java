package net.darkhax.botanypots.api.events;

import javax.annotation.Nullable;

import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * A LookupEvent is fired on the Forge event bus when a player uses an ItemStack on the Botany
 * Pot and a lookup is required. For example clicking the pot with a dirt item will query the
 * soil registry for a matching soil. See inner classes for more info.
 * 
 * @param <T> The type of data being looked up.
 */
public class LookupEvent<T> extends BotanyPotEvent.Player {
    
    /**
     * The ItemStack being looked up.
     */
    private final ItemStack stack;
    
    /**
     * The original value retrieved from the registry. This is null when there was no original
     * result found.
     */
    @Nullable
    private final T originalLookup;
    
    /**
     * The current lookup value. The final result will be used as the actual lookup value. A
     * null value is treated as no lookup found and will prevent the item from being used.
     */
    @Nullable
    private T currentLookup;
    
    public LookupEvent(TileEntityBotanyPot pot, PlayerEntity player, @Nullable T lookup, ItemStack stack) {
        
        super(pot, player);
        this.stack = stack;
        this.originalLookup = lookup;
        this.currentLookup = lookup;
    }
    
    /**
     * Gets the ItemStack being looked up by the player.
     * 
     * @return The ItemStack being looked up.
     */
    public ItemStack getItemStack () {
        
        return this.stack;
    }
    
    /**
     * Gets the original lookup value for {@link #stack}. A null value indicates that there was
     * no value initially found.
     * 
     * @return The original lookup value.
     */
    @Nullable
    public T getOriginalLookup () {
        
        return this.originalLookup;
    }
    
    /**
     * Gets the current lookup value. A null value indicates that no result was found and that
     * the item can not be used.
     * 
     * @return The current lookup value.
     */
    @Nullable
    public T getCurrentLookup () {
        
        return this.currentLookup;
    }
    
    /**
     * Sets the current lookup value.
     * 
     * @param lookup The new lookup value.
     */
    public void setLookup (@Nullable T lookup) {
        
        this.currentLookup = lookup;
    }
    
    /**
     * This event is fired on the Forge event bus when a player attempts to place a potential
     * soil ItemStack in the botany pot.
     */
    @Cancelable
    public static class Soil extends LookupEvent<SoilInfo> {
        
        public Soil(TileEntityBotanyPot pot, PlayerEntity player, @Nullable SoilInfo lookup, ItemStack stack) {
            
            super(pot, player, lookup, stack);
        }
    }
    
    /**
     * This event is fired on the Forge event bus when a player attempts to plant a potential
     * seed ItemStack in the botany pot.
     */
    @Cancelable
    public static class Crop extends LookupEvent<CropInfo> {
        
        public Crop(TileEntityBotanyPot pot, PlayerEntity player, @Nullable CropInfo lookup, ItemStack stack) {
            
            super(pot, player, lookup, stack);
        }
    }
    
    /**
     * This event is fired on the Forge event bus when a player attempts to use a potential
     * fertilizer ItemStack on the botany pot.
     */
    @Cancelable
    public static class Fertilizer extends LookupEvent<FertilizerInfo> {
        
        public Fertilizer(TileEntityBotanyPot pot, PlayerEntity player, @Nullable FertilizerInfo lookup, ItemStack stack) {
            
            super(pot, player, lookup, stack);
        }
    }
}