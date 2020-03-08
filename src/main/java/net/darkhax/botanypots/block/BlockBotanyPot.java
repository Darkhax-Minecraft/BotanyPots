package net.darkhax.botanypots.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBotanyPot extends Block implements IGrowable {
    
    private static final ITextComponent TOOLTIP_NORMAL = new TranslationTextComponent("botanypots.tooltip.pot.normal").applyTextStyle(TextFormatting.GRAY);
    
    private static final ITextComponent TOOLTIP_HOPPER = new TranslationTextComponent("botanypots.tooltip.pot.hopper").applyTextStyle(TextFormatting.GRAY);
    
    private static final VoxelShape SHAPE = Block.makeCuboidShape(2, 0, 2, 14, 8, 14);
    
    private static final Properties properties = Properties.create(Material.CLAY).hardnessAndResistance(1.25F, 4.2F).notSolid();
    
    private final boolean hopper;
    
    public BlockBotanyPot() {
        
        this(false);
    }
    
    public BlockBotanyPot(boolean hopper) {
        
        super(properties);
        this.hopper = hopper;
        this.setDefaultState(this.stateContainer.getBaseState().with(BlockStateProperties.POWERED, false));
    }
    
    public boolean isHopper () {
        
        return this.hopper;
    }
    
    @Override
    public ActionResultType onBlockActivated (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        
        final TileEntity tile = world.getTileEntity(pos);
        
        if (tile instanceof TileEntityBotanyPot) {
            
            final TileEntityBotanyPot pot = (TileEntityBotanyPot) tile;
            
            // Attempt removal
            if (player.isShiftKeyDown()) {
                
                final CropInfo crop = pot.getCrop();
                
                // If a crop exists, remove it.
                if (crop != null) {
                    
                    final ItemStack seedStack = crop.getRandomSeed();
                    
                    if (!seedStack.isEmpty() && pot.canSetCrop(null)) {
                        
                        pot.setCrop(null);
                        dropItem(seedStack.copy(), world, pos);
                        return ActionResultType.SUCCESS;
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
                            return ActionResultType.SUCCESS;
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
                        
                        final SoilInfo soilForStack = BotanyPotHelper.getSoilForItem(world, heldItem);
                        
                        if (soilForStack != null && pot.canSetSoil(soilForStack)) {
                            
                            pot.setSoil(soilForStack);
                            
                            if (!player.isCreative()) {
                                
                                heldItem.shrink(1);
                            }
                            
                            return ActionResultType.SUCCESS;
                        }
                    }
                    
                    // Attempt crop add second.
                    else if (pot.getCrop() == null) {
                        
                        final CropInfo cropForStack = BotanyPotHelper.getCropForItem(world, heldItem);
                        
                        if (cropForStack != null && BotanyPotHelper.isSoilValidForCrop(pot.getSoil(), cropForStack) && pot.canSetCrop(cropForStack)) {
                            
                            pot.setCrop(cropForStack);
                            
                            if (!player.isCreative()) {
                                
                                heldItem.shrink(1);
                            }
                            
                            return ActionResultType.SUCCESS;
                        }
                    }
                    
                    // Attempt fertilizer.
                    else if (!pot.canHarvest()) {
                        
                        final int fertilizerGrowthTicks = BotanyPotHelper.getFertilizerTicks(heldItem, world);
                        
                        if (fertilizerGrowthTicks > -1) {
                            
                            pot.addGrowth(fertilizerGrowthTicks);
                            
                            if (!world.isRemote) {
                                
                                world.playEvent(2005, tile.getPos(), 0);
                            }
                            
                            if (!player.isCreative()) {
                                
                                heldItem.shrink(1);
                            }
                            
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
                
                // Check if the pot can be harvested
                if (!this.isHopper() && pot.canHarvest()) {
                    
                    pot.onCropHarvest();
                    pot.resetGrowthTime();
                    
                    for (final ItemStack stack : BotanyPotHelper.getHarvestStacks(world, pot.getCrop())) {
                        
                        dropItem(stack, world, pos);
                    }
                    
                    return ActionResultType.SUCCESS;
                }
            }
        }
        
        return super.onBlockActivated(state, world, pos, player, hand, hit);
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
    public VoxelShape getShape (BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        
        return SHAPE;
    }
    
    @Override
    public void onReplaced (BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        
        if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
            
            final TileEntity tileEntity = worldIn.getTileEntity(pos);
            
            if (tileEntity instanceof TileEntityBotanyPot) {
                
                final TileEntityBotanyPot pot = (TileEntityBotanyPot) tileEntity;
                
                if (pot.getSoil() != null) {
                    
                    dropItem(pot.getSoil().getRandomSoilBlock(), worldIn, pos);
                }
                
                if (pot.getCrop() != null) {
                    
                    dropItem(pot.getCrop().getRandomSeed(), worldIn, pos);
                }
            }
        }
        
        super.onReplaced(state, worldIn, pos, newState, isMoving);
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
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        
        tooltip.add(this.isHopper() ? TOOLTIP_HOPPER : TOOLTIP_NORMAL);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick (BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        
        if (this.isHopper()) {
            
            // No sparkles for hopper pots
            return;
        }
        
        final TileEntity tile = worldIn.getTileEntity(pos);
        
        if (tile instanceof TileEntityBotanyPot && MathsUtils.tryPercentage(0.1)) {
            
            final TileEntityBotanyPot pot = (TileEntityBotanyPot) tile;
            
            if (pot.hasSoilAndCrop() && pot.isDoneGrowing()) {
                
                BoneMealItem.spawnBonemealParticles(worldIn, pos, 0);
            }
        }
    }
    
    @Override
    public boolean canGrow (IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
        
        final TileEntity tile = worldIn.getTileEntity(pos);
        
        if (tile instanceof TileEntityBotanyPot) {
            
            final TileEntityBotanyPot pot = (TileEntityBotanyPot) tile;
            return pot.hasSoilAndCrop() && !pot.isDoneGrowing();
        }
        
        return false;
    }
    
    @Override
    public boolean canUseBonemeal (World worldIn, Random rand, BlockPos pos, BlockState state) {
        
        // We have custom logic for bone meal and other fertilizer. See the fertilizer data
        // pack type.
        return false;
    }
    
    @Override
    public void grow (ServerWorld world, Random random, BlockPos pos, BlockState myState) {
        
        final TileEntity tile = world.getTileEntity(pos);
        
        if (tile instanceof TileEntityBotanyPot) {
            
            ((TileEntityBotanyPot) tile).onTileTick();
        }
    }
    
    @Override
    public boolean hasComparatorInputOverride (BlockState state) {
        
        return true;
    }
    
    @Override
    public int getComparatorInputOverride (BlockState blockState, World world, BlockPos pos) {
        
        return blockState.get(BlockStateProperties.POWERED) ? 15 : 0;
    }
    
    @Override
    protected void fillStateContainer (StateContainer.Builder<Block, BlockState> builder) {
        
        builder.add(BlockStateProperties.POWERED);
    }
}