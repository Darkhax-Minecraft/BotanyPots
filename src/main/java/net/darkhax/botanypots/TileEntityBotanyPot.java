package net.darkhax.botanypots;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.block.tileentity.TileEntityBasicTickable;
import net.darkhax.botanypots.api.BotanyPotHelper;
import net.darkhax.botanypots.api.crop.CropInfo;
import net.darkhax.botanypots.api.crop.CropReloadListener;
import net.darkhax.botanypots.api.soil.SoilInfo;
import net.darkhax.botanypots.api.soil.SoilReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class TileEntityBotanyPot extends TileEntityBasicTickable {
    
    @Nullable
    private SoilInfo soil;
    
    @Nullable
    private CropInfo crop;
    
    private int totalGrowthTicks;
    private int currentGrowthTicks;
    
    public TileEntityBotanyPot() {
        
        super(BotanyPots.tileBotanyPot);
    }
    
    public boolean setSoil (@Nullable SoilInfo newSoil) {
        
        if (newSoil == null) {
            
            this.totalGrowthTicks = -1;
            this.currentGrowthTicks = -1;
            this.soil = newSoil;
            return true;
        }
        
        if (this.soil == null && this.crop == null) {
            
            this.soil = newSoil;
            return true;
        }
        
        return false;
    }
    
    public boolean setCrop (@Nullable CropInfo newCrop) {
        
        if (newCrop == null) {
            
            this.totalGrowthTicks = -1;
            this.currentGrowthTicks = -1;
            this.crop = newCrop;
        }
        
        if (this.soil != null && this.crop == null) {
            
            this.crop = newCrop;
            
            if (newCrop != null) {
                
                this.totalGrowthTicks = BotanyPotHelper.getRequiredGrowthTicks(newCrop, this.soil);
            }
            
            return true;
        }
        
        return false;
    }
    
    @Nullable
    public SoilInfo getSoil () {
        
        return this.soil;
    }
    
    @Nullable
    public CropInfo getCrop () {
        
        return this.crop;
    }
    
    public int getTotalGrowthTicks () {
        
        return this.totalGrowthTicks;
    }
    
    public int getCurrentGrowthTicks () {
        
        return this.currentGrowthTicks;
    }
    
    public boolean canHarvest () {
        
        return this.getTotalGrowthTicks() > 0 && this.currentGrowthTicks > 0 && this.currentGrowthTicks >= this.getTotalGrowthTicks();
    }
    
    public void resetGrowthTime () {
        
        // Recalculate total growth ticks to account for any data changes
        this.totalGrowthTicks = BotanyPotHelper.getRequiredGrowthTicks(this.crop, this.soil);
        
        // Reset the growth time.
        this.currentGrowthTicks = 0;
    }
    
    public void addGrowth(int ticksToGrow) {
        
        this.currentGrowthTicks += ticksToGrow;
    }
    
    @Override
    public void onTileTick () {
        
        if (this.soil != null && this.crop != null) {
            
            // If it's done growing
            if (this.currentGrowthTicks >= this.totalGrowthTicks) {
                
                for (final ItemStack item : BotanyPotHelper.getHarvestStacks(this.world, this.getCrop())) {
                    
                    BlockBotanyPot.dropItem(item, this.world, this.pos);
                }
                
                this.resetGrowthTime();
            }
            
            // It's not done growing
            else {
                
                //TODO Growth rate is accelerated here
                this.currentGrowthTicks += 10;
            }
        }
        
        else {
            
            // Reset tick counts
            this.totalGrowthTicks = -1;
            this.currentGrowthTicks = -1;
        }
    }
    
    @Override
    public void serialize (CompoundNBT dataTag) {
        
        if (this.soil != null) {
            
            dataTag.putString("Soil", this.soil.getId().toString());
            
            // Crop is only saved if there is a soil
            if (this.crop != null) {
                
                dataTag.putString("Crop", this.crop.getId().toString());
                
                // Tick info is only saved if there is a crop and a soil.
                dataTag.putInt("GrowthTicks", this.currentGrowthTicks);
            }
        }
    }
    
    @Override
    public void deserialize (CompoundNBT dataTag) {
        
        if (dataTag.contains("Soil")) {
            
            final String rawSoilId = dataTag.getString("Soil");
            final ResourceLocation soilId = ResourceLocation.tryCreate(rawSoilId);
            
            if (soilId != null) {
                
                final SoilInfo foundSoil = SoilReloadListener.registeredSoil.get(soilId);
                
                if (foundSoil != null) {
                    
                    this.soil = foundSoil;
                    
                    // Crops are only loaded if the soil exists.
                    if (dataTag.contains("Crop")) {
                        
                        final String rawCropId = dataTag.getString("Crop");
                        final ResourceLocation cropId = ResourceLocation.tryCreate(rawCropId);
                        
                        if (cropId != null) {
                            
                            final CropInfo cropInfo = CropReloadListener.registeredCrops.get(cropId);
                            
                            if (cropInfo != null) {
                                
                                this.crop = cropInfo;
                                
                                // Growth ticks are only loaded if a crop and soil exist.
                                this.currentGrowthTicks = dataTag.getInt("GrowthTicks");
                                
                                // Reset total growth ticks on tile load to account for data
                                // changes.
                                this.totalGrowthTicks = this.crop.getGrowthTicksForSoil(this.soil);
                            }
                            
                            else {
                                
                                BotanyPots.LOGGER.error("Botany Pot at {} had a crop of type {} but that crop does not exist. The crop will be discarded.", this.pos, rawCropId);
                            }
                        }
                        
                        else {
                            
                            BotanyPots.LOGGER.error("Botany Pot at {} has an invalid crop Id of {}. The crop will be discarded.", this.pos, rawCropId);
                        }
                    }
                }
                
                else {
                    
                    BotanyPots.LOGGER.error("Botany Pot at {} had a soil of type {} which no longer exists. Soil and crop will be discarded.", this.pos, rawSoilId);
                }
            }
            
            else {
                
                BotanyPots.LOGGER.error("Botany Pot at {} has invalid soil type {}. Soil and crop will be discarded.", this.pos, rawSoilId);
            }
        }
    }
}