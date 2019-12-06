package net.darkhax.botanypots;

import net.darkhax.botanypots.api.BotanyPotHelper;
import net.darkhax.botanypots.api.crop.CropInfo;
import net.darkhax.botanypots.api.soil.SoilInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockBotanyPot extends Block {
    
    private static final VoxelShape SHAPE = Block.makeCuboidShape(2, 0, 2, 14, 8, 14);
    
    private static final Properties properties = Properties.create(Material.CLAY).hardnessAndResistance(1.25F, 4.2F);
    
    private final boolean hopper;
    
    public BlockBotanyPot() {
        
        this(false);
    }
    
    public BlockBotanyPot(boolean hopper) {
        
        super(properties);
        this.hopper = hopper;
    }
    
    public boolean isHopper () {
        
        return this.hopper;
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public boolean eventReceived (BlockState state, World worldIn, BlockPos pos, int id, int param) {
        
        super.eventReceived(state, worldIn, pos, id, param);
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, param);
    }
    
    @Override
    public boolean onBlockActivated (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        
        final TileEntity tile = world.getTileEntity(pos);
        
        if (tile instanceof TileEntityBotanyPot) {
            
            final TileEntityBotanyPot pot = (TileEntityBotanyPot) tile;
            
            // Attempt removal
            if (player.isSneaking()) {
                
                final CropInfo crop = pot.getCrop();
                
                // If a crop exists, remove it.
                if (crop != null) {
                    
                    final ItemStack seedStack = crop.getRandomSeed();
                    
                    if (!seedStack.isEmpty() && pot.canSetCrop(null)) {
                        
                        pot.setCrop(null);
                        dropItem(seedStack.copy(), world, pos);
                        return true;
                    }
                }
                
                // If no crop exits, try to remove the soil.
                else {
                    
                    final SoilInfo soil = pot.getSoil();
                    
                    if (soil != null) {
                        
                        final ItemStack soilStack = soil.getRandomSoilBlock();
                        
                        if (!soilStack.isEmpty() && pot.canSetSoil(null)) {
                            
                            pot.setSoil(null);
                            dropItem(soilStack.copy(), world, pos);
                            return true;
                        }
                    }
                }
            }
            
            // Attempt to insert or harvest
            else {
                
                final ItemStack heldItem = player.getHeldItem(hand);
                
                // Attempt to insert the item. If something is inserted true is returned and
                // the method ends. If the item can't be inserted the method will continue and
                // the crop will try to be harvested.
                if (!heldItem.isEmpty()) {
                    
                    // Attempt soil add first
                    if (pot.getSoil() == null) {
                        
                        final SoilInfo soilForStack = BotanyPotHelper.getSoilForItem(heldItem);
                        
                        if (soilForStack != null && pot.canSetSoil(soilForStack)) {
                            
                            pot.setSoil(soilForStack);
                            
                            if (!player.isCreative()) {
                                
                                heldItem.shrink(1);
                            }
                            
                            return true;
                        }
                    }
                    
                    // Attempt crop add second.
                    else if (pot.getCrop() == null) {
                        
                        final CropInfo cropForStack = BotanyPotHelper.getCropForItem(heldItem);
                        
                        if (cropForStack != null && BotanyPotHelper.isSoilValidForCrop(pot.getSoil(), cropForStack) && pot.canSetCrop(cropForStack)) {
                            
                            pot.setCrop(cropForStack);
                            
                            if (!player.isCreative()) {
                                
                                heldItem.shrink(1);
                            }
                            
                            return true;
                        }
                    }
                    
                    else {
                        
                        final int fertilizerGrowthTicks = BotanyPotHelper.getFertilizerTicks(heldItem, world.rand);
                        
                        if (fertilizerGrowthTicks > -1) {
                            
                            pot.addGrowth(fertilizerGrowthTicks);
                            
                            if (!world.isRemote) {
                                
                                world.playEvent(2005, tile.getPos(), 0);
                            }
                            
                            if (!player.isCreative()) {
                                
                                heldItem.shrink(1);
                            }
                            
                            return true;
                        }
                    }
                }
                
                // Check if the pot can be harvested
                if (pot.canHarvest()) {
                    
                    pot.resetGrowthTime();
                    
                    for (final ItemStack stack : BotanyPotHelper.getHarvestStacks(world, pot.getCrop())) {
                        
                        dropItem(stack, world, pos);
                    }
                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    @Override
    public boolean hasTileEntity (BlockState state) {
        
        return true;
    }
    
    @Override
    public TileEntity createTileEntity (BlockState state, IBlockReader world) {
        
        return new TileEntityBotanyPot();
    }
    
    @Override
    public BlockRenderLayer getRenderLayer () {
        
        return BlockRenderLayer.CUTOUT;
    }
    
    @Override
    public BlockRenderType getRenderType (BlockState state) {
        
        return BlockRenderType.MODEL;
    }
    
    @Override
    public VoxelShape getShape (BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        
        return SHAPE;
    }
    
    @Override
    public boolean isSolid (BlockState state) {
        
        return false;
    }
    
    public static void dropItem (ItemStack item, World world, BlockPos pos) {
        
        if (!world.isRemote) {
            
            final double offsetX = (double) (world.rand.nextFloat() * 0.7F) + (double) 0.15F;
            final double offsetY = (double) (world.rand.nextFloat() * 0.7F) + (double) 0.060000002F + 0.6D;
            final double offsetZ = (double) (world.rand.nextFloat() * 0.7F) + (double) 0.15F;
            final ItemEntity droppedItemEntity = new ItemEntity(world, pos.getX() + offsetX, pos.getY() + offsetY, pos.getZ() + offsetZ, item);
            droppedItemEntity.setDefaultPickupDelay();
            world.addEntity(droppedItemEntity);
        }
    }
}