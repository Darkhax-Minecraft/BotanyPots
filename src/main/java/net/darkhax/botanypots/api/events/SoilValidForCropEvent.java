package net.darkhax.botanypots.api.events;

import javax.annotation.Nullable;

import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired on the Forge event bus when a player attempts to plant a crop on a soil.
 * This event specifically handles the compatibility check between the soil and the crop.
 */
@Cancelable
public class SoilValidForCropEvent extends BotanyPotEvent {
    
    /**
     * The player planting the crop.
     */
    @Nullable
    private final PlayerEntity player;
    
    /**
     * The soil that the crop is being planted in.
     */
    private final SoilInfo soil;
    
    /**
     * The crop that is being planted.
     */
    private final CropInfo crop;
    
    /**
     * The original validity state of the soil.
     */
    private final boolean originallyValid;
    
    /**
     * The actual validity state of the soil.
     */
    private boolean isValid;
    
    public SoilValidForCropEvent(TileEntityBotanyPot pot, PlayerEntity player, SoilInfo soil, CropInfo crop, boolean originallyValid) {
        
        super(pot);
        this.player = player;
        this.soil = soil;
        this.crop = crop;
        this.originallyValid = originallyValid;
        this.isValid = originallyValid;
    }
    
    /**
     * Gets the player that is planting the crop in the soil.
     * 
     * @return The player that is planting the crop in the soil.
     */
    @Nullable
    public PlayerEntity getPlayer () {
        
        return this.player;
    }
    
    /**
     * Gets the soil that the crop is being planted in.
     * 
     * @return The soil that the crop is being planted in.
     */
    public SoilInfo getSoil () {
        
        return this.soil;
    }
    
    /**
     * Gets the crop being planted.
     * 
     * @return The crop being planted.
     */
    public CropInfo getCrop () {
        
        return this.crop;
    }
    
    /**
     * Checks if the soil was originally valid for the crop.
     * 
     * @return Whether or not the soil was originally valid for the crop.
     */
    public boolean isSoilOriginallyValid () {
        
        return this.originallyValid;
    }
    
    /**
     * Checks if the soil is currently valid for the crop.
     * 
     * @return Whether or not the soil is currently valid for the crop.
     */
    public boolean isSoilValid () {
        
        return this.isValid;
    }
    
    /**
     * Sets the current validity of the soil for the crop.
     * 
     * @param valid The current validity of the soil for the crop.
     */
    public void setSoilValidity (boolean valid) {
        
        this.isValid = valid;
    }
    
    @Override
    public void setCanceled (boolean cancel) {
        
        super.setCanceled(cancel);
        
        if (cancel) {
            
            this.setSoilValidity(false);
        }
    }
}
