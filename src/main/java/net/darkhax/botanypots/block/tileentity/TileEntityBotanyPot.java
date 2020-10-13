package net.darkhax.botanypots.block.tileentity;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.block.tileentity.TileEntityBasicTickable;
import net.darkhax.bookshelf.util.InventoryUtils;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.network.BreakEffectsMessage;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;

public class TileEntityBotanyPot extends TileEntityBasicTickable {
    
    /**
     * The current soil in the botany pot. Can be null.
     */
    @Nullable
    private SoilInfo soil;
    
    private ItemStack soilStack = ItemStack.EMPTY;
    
    /**
     * The current crop in the botany pot. Can be null.
     */
    @Nullable
    private CropInfo crop;
    
    private ItemStack cropStack = ItemStack.EMPTY;
    
    /**
     * The total growth ticks for the crop to mature. -1 means it's not growing.
     */
    private int totalGrowthTicks;
    
    /**
     * The total growth ticks for the crop. -1 means it's not growing.
     */
    private int currentGrowthTicks;
    
    /**
     * A cooldown for the auto harvest.
     */
    private int autoHarvestCooldown;
    
    /**
     * The current chunk pos. Not saved to nbt.
     */
    private ChunkPos chunkPos;
    
    public TileEntityBotanyPot() {
        
        super(BotanyPots.instance.getContent().tileBotanyPot);
    }
    
    /**
     * Checks if the soil can be set. This is always true if the new soil is null. Otherwise it
     * is only true if there isn't already soil.
     * 
     * @param newSoil The soil to set. Null will delete the existing soil.
     * @return Whether or not the soil can be set.
     */
    public boolean canSetSoil (@Nullable SoilInfo newSoil) {
        
        return newSoil == null || this.getSoil() == null;
    }
    
    /**
     * Sets the soil in the pot. If the new soil is null it will set the pot to having no soil.
     * This will also reset the growth timer. When called on the server the pot will be synced
     * to the client.
     * 
     * @param newSoil The new soil to set in the pot.
     */
    public void setSoil (@Nullable SoilInfo newSoil, ItemStack stack) {
        
        this.soil = newSoil;
        this.soilStack = stack;
        this.resetGrowthTime();
        
        if (!this.world.isRemote) {
            
            this.sync();
            this.world.getChunkProvider().getLightManager().checkBlock(this.pos);
        }
    }
    
    @Override
    public void sync () {
        
        if (this.world instanceof ServerWorld) {
            
            final IPacket<?> packet = this.getUpdatePacket();
            sendToTracking((ServerWorld) this.world, this.getChunkPos(), this.pos, packet, false);
        }
    }
    
    @Override
    public void onDataPacket (NetworkManager net, SUpdateTileEntityPacket packet) {
        
        super.onDataPacket(net, packet);
        this.world.getLightManager().checkBlock(this.pos);
    }
    
    /**
     * Checks if a crop can be set in the pot. You can always set a crop to null. Otherwise
     * there must be a soil, and there must not be an existing crop.
     * 
     * @param newCrop The new crop to set.
     * @return Whether or not the crop can be set.
     */
    public boolean canSetCrop (@Nullable CropInfo newCrop) {
        
        return newCrop == null || this.getSoil() != null && this.getCrop() == null;
    }
    
    /**
     * Sets the crop inside the pot. If set to null the crop will be set to nothing. This will
     * reset the growth time. If called on the server the tile will be synced to the client.
     * 
     * @param newCrop The new crop to set.
     */
    public void setCrop (@Nullable CropInfo newCrop, ItemStack stack) {
        
        this.crop = newCrop;
        this.cropStack = stack;
        this.resetGrowthTime();
        
        if (!this.world.isRemote) {
            
            this.sync();
        }
    }
    
    /**
     * Gets the soil in the pot. Null means no soil.
     * 
     * @return The soil in the pot.
     */
    @Nullable
    public SoilInfo getSoil () {
        
        return this.soil;
    }
    
    /**
     * Gets the crop in the pot. Null means no crop.
     * 
     * @return The crop in the pot.
     */
    @Nullable
    public CropInfo getCrop () {
        
        return this.crop;
    }
    
    /**
     * Gets the total required growth ticks For the crop to become mature enough to harvest..
     * 
     * @return The total required growth ticks. -1 means no growth.
     */
    public int getTotalGrowthTicks () {
        
        return this.totalGrowthTicks;
    }
    
    /**
     * Gets the current amount of ticks the crop has grown.
     * 
     * @return The current amount of ticks the crop has grown. -1 means no growth.
     */
    public int getCurrentGrowthTicks () {
        
        return this.currentGrowthTicks;
    }
    
    /**
     * Checks if the crop in the pot can be harvested.
     * 
     * @return Whether or not the crop can be harvested.
     */
    public boolean canHarvest () {
        
        return this.crop != null && this.getTotalGrowthTicks() > 0 && this.getCurrentGrowthTicks() >= this.getTotalGrowthTicks();
    }
    
    /**
     * Resets the current growth ticks and the total growth ticks for the crop in the pot.
     */
    public void resetGrowthTime () {
        
        // Recalculate total growth ticks to account for any data changes
        this.totalGrowthTicks = BotanyPotHelper.getRequiredGrowthTicks(this.getCrop(), this.getSoil());
        
        // Reset the growth time.
        this.currentGrowthTicks = 0;
        
        // To help deal with desyncs caused by reload, every reset will also reset the
        // cached
        // soila and crop references.
        if (this.soil != null) {
            
            this.soil = BotanyPotHelper.getSoil(this.soil.getId());
            
            // Check if the soil was removed. If so kill the crop, because crop needs a
            // soil.
            if (this.soil == null) {
                
                this.crop = null;
            }
        }
        
        if (this.crop != null) {
            
            this.crop = BotanyPotHelper.getCrop(this.crop.getId());
        }
        
        this.autoHarvestCooldown = 5;
        this.world.updateComparatorOutputLevel(this.pos, this.getBlockState().getBlock());
        
        if (!this.world.isRemote) {
            
            this.sync();
        }
    }
    
    /**
     * Adds growth to the crop. This is primarily used for things like bone meal. If this is
     * called on the server it will cause a client sync.
     * 
     * @param ticksToGrow The amount of ticks to add.
     */
    public void addGrowth (int ticksToGrow) {
        
        this.currentGrowthTicks += ticksToGrow;
        
        if (this.currentGrowthTicks > this.totalGrowthTicks) {
            
            this.currentGrowthTicks = this.totalGrowthTicks;
        }
        
        if (!this.world.isRemote) {
            
            this.sync();
        }
    }
    
    public float getGrowthPercent () {
        
        if (this.totalGrowthTicks == -1 || this.currentGrowthTicks == -1) {
            
            return 0f;
        }
        
        return (float) this.currentGrowthTicks / this.totalGrowthTicks;
    }
    
    @Override
    public void onTileTick () {
        
        if (this.world.isBlockPowered(this.pos)) {
            
            return;
        }
        
        if (this.hasSoilAndCrop()) {
            
            if (this.isDoneGrowing()) {
                
                this.world.updateComparatorOutputLevel(this.pos, this.getBlockState().getBlock());
                this.attemptAutoHarvest();
            }
            
            // It's not done growing
            else {
                
                this.currentGrowthTicks++;
            }
        }
        
        else if (this.totalGrowthTicks != -1 || this.currentGrowthTicks != 0) {
            
            // Reset tick counts
            this.resetGrowthTime();
        }
    }
    
    public boolean hasSoilAndCrop () {
        
        return this.soil != null && this.crop != null;
    }
    
    public boolean isDoneGrowing () {
        
        return this.hasSoilAndCrop() && this.totalGrowthTicks > 0 && this.currentGrowthTicks >= this.totalGrowthTicks;
    }
    
    private void attemptAutoHarvest () {
        
        final Block block = this.getBlockState().getBlock();
        
        if (block instanceof BlockBotanyPot && ((BlockBotanyPot) block).isHopper()) {
            
            if (this.autoHarvestCooldown > 0) {
                
                this.autoHarvestCooldown--;
                return;
            }
            
            final IItemHandler inventory = InventoryUtils.getInventory(this.world, this.pos.down(), Direction.UP);
            
            if (inventory != EmptyHandler.INSTANCE && !this.world.isRemote) {
                
                boolean didAutoHarvest = false;
                
                for (final ItemStack item : BotanyPotHelper.getHarvestStacks(this.world, this.getCrop())) {
                    
                    // Iterate every valid slot of the inventory
                    for (int slot = 0; slot < inventory.getSlots(); slot++) {
                        
                        // Check if the simulated insert stack can be accepted into the
                        // inventory.
                        if (inventory.isItemValid(slot, item) && inventory.insertItem(slot, item, true).getCount() != item.getCount()) {
                            
                            // Actually insert the stack.
                            
                            // Insert the items. We don't care about the remainder and
                            // it can be safely voided.
                            inventory.insertItem(slot, item, false);
                            
                            // Set auto harvest to true. This will cause a reset for
                            // the next growth cycle.
                            didAutoHarvest = true;
                            
                            // Exit the inventory for this loop. Will then move on to
                            // the next item and start over.
                            break;
                        }
                    }
                }
                
                if (didAutoHarvest) {
                    
                    this.onCropHarvest();
                    this.resetGrowthTime();
                }
            }
        }
    }
    
    public void onCropHarvest () {
        
        if (this.hasSoilAndCrop()) {
            
            final IChunk chunk = this.world.getChunk(this.pos);
            
            if (chunk instanceof Chunk) {
                
                // TODO remove the need to cast this.
                BotanyPots.NETWORK.sendToChunk((Chunk) chunk, new BreakEffectsMessage(this.pos, this.crop.getDisplayState()[0].getState()));
            }
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
        
        dataTag.put("CropStack", this.cropStack.serializeNBT());
        dataTag.put("SoilStack", this.soilStack.serializeNBT());
    }
    
    @Override
    public void deserialize (CompoundNBT dataTag) {
        
        this.soil = null;
        this.crop = null;
        
        if (dataTag.contains("CropStack")) {
            
            this.cropStack = ItemStack.read(dataTag.getCompound("CropStack"));
        }
        
        if (dataTag.contains("SoilStack")) {
            
            this.soilStack = ItemStack.read(dataTag.getCompound("SoilStack"));
        }
        
        if (dataTag.contains("Soil")) {
            
            final String rawSoilId = dataTag.getString("Soil");
            final ResourceLocation soilId = ResourceLocation.tryCreate(rawSoilId);
            
            if (soilId != null) {
                
                final SoilInfo foundSoil = BotanyPotHelper.getSoil(soilId);
                
                if (foundSoil != null) {
                    
                    this.soil = foundSoil;
                    
                    // Crops are only loaded if the soil exists.
                    if (dataTag.contains("Crop")) {
                        
                        final String rawCropId = dataTag.getString("Crop");
                        final ResourceLocation cropId = ResourceLocation.tryCreate(rawCropId);
                        
                        if (cropId != null) {
                            
                            final CropInfo cropInfo = BotanyPotHelper.getCrop(cropId);
                            
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
    
    public ItemStack getSoilStack () {
        
        return this.soilStack;
    }
    
    public ItemStack getCropStack () {
        
        return this.cropStack;
    }
    
    @Override
    public ChunkPos getChunkPos () {
        
        if (this.chunkPos == null) {
            
            this.chunkPos = new ChunkPos(this.pos);
        }
        
        return this.chunkPos;
    }
    
    public static void sendToTracking (ServerWorld world, ChunkPos chunkPos, BlockPos blockPos, IPacket<?> packet, boolean boundaryOnly) {
        
        world.getChunkProvider().chunkManager.getTrackingPlayers(chunkPos, boundaryOnly).forEach(p -> p.connection.sendPacket(packet));
    }
}