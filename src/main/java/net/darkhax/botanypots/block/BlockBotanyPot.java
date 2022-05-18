package net.darkhax.botanypots.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.util.MathsUtils;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.api.events.BotanyPotHarvestedEvent;
import net.darkhax.botanypots.api.events.CropPlaceEvent;
import net.darkhax.botanypots.api.events.CropRemovedEvent;
import net.darkhax.botanypots.api.events.FertilizerUsedEvent;
import net.darkhax.botanypots.api.events.LookupEvent;
import net.darkhax.botanypots.api.events.SoilPlaceEvent;
import net.darkhax.botanypots.api.events.SoilRemoveEvent;
import net.darkhax.botanypots.api.events.SoilValidForCropEvent;
import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

public class BlockBotanyPot extends Block implements IGrowable, IWaterLoggable {
    
    private static final ITextComponent TOOLTIP_NORMAL = new TranslationTextComponent("botanypots.tooltip.pot.normal").mergeStyle(TextFormatting.GRAY);
    
    private static final ITextComponent TOOLTIP_HOPPER = new TranslationTextComponent("botanypots.tooltip.pot.hopper").mergeStyle(TextFormatting.GRAY);
    
    private static final VoxelShape SHAPE = Block.makeCuboidShape(2, 0, 2, 14, 8, 14);
    
    private static final Properties properties = Properties.create(Material.CLAY).hardnessAndResistance(1.25F, 4.2F).notSolid();

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private final boolean hopper;
    
    public static List<Block> botanyPots = NonNullList.create();
    
    public BlockBotanyPot() {
        
        this(false);
    }
    
    public BlockBotanyPot(boolean hopper) {
        
        super(properties);
        this.hopper = hopper;
        botanyPots.add(this);
        this.setDefaultState(this.getStateContainer().getBaseState().with(WATERLOGGED, false));
    }
    
    public boolean isHopper () {
        
        return this.hopper;
    }
    
    @Override
    public ActionResultType onBlockActivated (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        
        if (world.isRemote) {
            
            // Forces all the logic to run on the server. Returning fail or pass on the
            // client will cause the click packet not to be sent to the server.
            return ActionResultType.SUCCESS;
        }
        
        final TileEntity tile = world.getTileEntity(pos);
        
        if (tile instanceof TileEntityBotanyPot) {
            
            final TileEntityBotanyPot pot = (TileEntityBotanyPot) tile;
            
            // Attempt removal
            if (player.isSneaking()) {
                
                final CropInfo crop = pot.getCrop();
                
                // If a crop exists, remove it.
                if (crop != null) {
                    
                    if (MinecraftForge.EVENT_BUS.post(new CropRemovedEvent.Pre(pot, player, crop))) {
                        
                        // Return out of the method here. Prevents the soil from being removed
                        // instead.
                        return ActionResultType.FAIL;
                    }
                    
                    else if (pot.canSetCrop(null)) {
                        
                        final ItemStack seedStack = pot.getCropStack();
                        
                        if (!seedStack.isEmpty()) {
                            
                            dropItem(seedStack.copy(), world, pos);
                        }
                        
                        pot.setCrop(null, ItemStack.EMPTY);
                        
                        MinecraftForge.EVENT_BUS.post(new CropRemovedEvent.Post(pot, player, crop));
                        return ActionResultType.SUCCESS;
                    }
                }
                
                // If no crop exits, try to remove the soil.
                else {
                    
                    final SoilInfo soil = pot.getSoil();
                    
                    if (soil != null && !MinecraftForge.EVENT_BUS.post(new SoilRemoveEvent.Pre(pot, soil, player))) {
                        
                        final ItemStack soilStack = pot.getSoilStack();
                        
                        if (!soilStack.isEmpty() && pot.canSetSoil(null)) {
                            
                            pot.setSoil(null, ItemStack.EMPTY);
                            dropItem(soilStack.copy(), world, pos);
                            MinecraftForge.EVENT_BUS.post(new SoilRemoveEvent.Post(pot, soil, player));
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
                        
                        SoilInfo soilForStack = BotanyPotHelper.getSoilForItem(heldItem);
                        final LookupEvent.Soil lookupEvent = new LookupEvent.Soil(pot, player, soilForStack, heldItem);
                        
                        if (!MinecraftForge.EVENT_BUS.post(lookupEvent) && lookupEvent.getCurrentLookup() != null) {
                            
                            soilForStack = lookupEvent.getCurrentLookup();
                            
                            final SoilPlaceEvent.Pre preEvent = new SoilPlaceEvent.Pre(pot, soilForStack, player);
                            
                            if (!MinecraftForge.EVENT_BUS.post(preEvent) && preEvent.getCurrentSoil() != null) {
                                
                                soilForStack = preEvent.getCurrentSoil();
                                
                                if (soilForStack != null && pot.canSetSoil(soilForStack)) {
                                    
                                    final ItemStack inStack = heldItem.copy();
                                    inStack.setCount(1);
                                    
                                    pot.setSoil(soilForStack, inStack);
                                    
                                    if (!player.isCreative()) {
                                        
                                        heldItem.shrink(1);
                                    }
                                    
                                    MinecraftForge.EVENT_BUS.post(new SoilPlaceEvent.Post(pot, soilForStack, player));
                                    return ActionResultType.SUCCESS;
                                }
                            }
                        }
                    }
                    
                    // Attempt crop add second.
                    else if (pot.getCrop() == null) {
                        
                        CropInfo cropForStack = BotanyPotHelper.getCropForItem(heldItem);
                        final LookupEvent<CropInfo> lookupEvent = new LookupEvent.Crop(pot, player, cropForStack, heldItem);
                        
                        if (!MinecraftForge.EVENT_BUS.post(lookupEvent) && lookupEvent.getCurrentLookup() != null) {
                            
                            cropForStack = lookupEvent.getCurrentLookup();
                            final CropPlaceEvent.Pre preEvent = new CropPlaceEvent.Pre(pot, player, cropForStack);
                            
                            if (!MinecraftForge.EVENT_BUS.post(preEvent) && preEvent.getCurrentCrop() != null) {
                                
                                cropForStack = preEvent.getCurrentCrop();
                                
                                boolean isSoilValid = BotanyPotHelper.isSoilValidForCrop(pot.getSoil(), cropForStack);
                                final SoilValidForCropEvent validSoilEvent = new SoilValidForCropEvent(pot, player, pot.getSoil(), cropForStack, isSoilValid);
                                isSoilValid = !MinecraftForge.EVENT_BUS.post(validSoilEvent) && validSoilEvent.isSoilValid();
                                
                                if (isSoilValid && pot.canSetCrop(cropForStack)) {
                                    
                                    final ItemStack inStack = heldItem.copy();
                                    inStack.setCount(1);
                                    
                                    pot.setCrop(cropForStack, inStack);
                                    
                                    if (!player.isCreative()) {
                                        
                                        heldItem.shrink(1);
                                    }
                                    
                                    MinecraftForge.EVENT_BUS.post(new CropPlaceEvent.Post(pot, player, cropForStack));
                                    return ActionResultType.SUCCESS;
                                }
                            }
                        }
                    }
                    
                    // Attempt fertilizer.
                    else if (!pot.canHarvest()) {
                        
                        FertilizerInfo fertilizerForStack = BotanyPotHelper.getFertilizerForItem(heldItem);
                        final LookupEvent.Fertilizer lookupEvent = new LookupEvent.Fertilizer(pot, player, fertilizerForStack, heldItem);
                        
                        if (!MinecraftForge.EVENT_BUS.post(lookupEvent) && lookupEvent.getCurrentLookup() != null) {
                            
                            fertilizerForStack = lookupEvent.getCurrentLookup();
                            
                            if (fertilizerForStack != null) {
                                
                                int ticksToGrow = fertilizerForStack.getTicksToGrow(world.rand, pot.getSoil(), pot.getCrop());
                                
                                final FertilizerUsedEvent.Pre preEvent = new FertilizerUsedEvent.Pre(pot, player, fertilizerForStack, heldItem, ticksToGrow);
                                
                                if (!MinecraftForge.EVENT_BUS.post(preEvent) && preEvent.getCurrentGrowthTicks() > 0) {
                                    
                                    ticksToGrow = preEvent.getCurrentGrowthTicks();
                                    
                                    pot.addGrowth(ticksToGrow);
                                    
                                    if (!world.isRemote) {
                                        
                                        world.playEvent(2005, tile.getPos(), 0);
                                    }
                                    
                                    if (!player.isCreative()) {
                                        
                                        heldItem.shrink(1);
                                    }
                                    
                                    MinecraftForge.EVENT_BUS.post(new FertilizerUsedEvent.Post(pot, player, fertilizerForStack, heldItem, ticksToGrow));
                                    return ActionResultType.SUCCESS;
                                }
                            }
                        }
                    }
                }
                
                // Check if the pot can be harvested
                if (!this.isHopper() && pot.canHarvest() && !MinecraftForge.EVENT_BUS.post(new BotanyPotHarvestedEvent.Pre(pot, player))) {
                    
                    final BotanyPotHarvestedEvent.LootGenerated event = new BotanyPotHarvestedEvent.LootGenerated(pot, player, BotanyPotHelper.generateDrop(world.rand, pot.getCrop()));
                    
                    if (!MinecraftForge.EVENT_BUS.post(event) && !event.getDrops().isEmpty()) {
                        
                        for (final ItemStack stack : event.getDrops()) {
                            
                            dropItem(stack, world, pos);
                        }
                    }
                    
                    pot.onCropHarvest();
                    pot.resetGrowthTime();
                    MinecraftForge.EVENT_BUS.post(new BotanyPotHarvestedEvent.Post(pot, player));
                    
                    return ActionResultType.SUCCESS;
                }
            }
        }
        
        return ActionResultType.FAIL;
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

                if (pot.getSoilStack() != ItemStack.EMPTY) dropItem(pot.getSoilStack(), worldIn, pos);
                if (pot.getCropStack() != ItemStack.EMPTY) dropItem(pot.getCropStack(), worldIn, pos);
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
        
        // We have custom logic for bone meal and other fertilizer. See the fertilizer
        // data pack type.
        return false;
    }
    
    @Override
    public void grow (ServerWorld world, Random random, BlockPos pos, BlockState myState) {
        
        final TileEntity tile = world.getTileEntity(pos);
        
        if (tile instanceof TileEntityBotanyPot) {
            
            // Grow crop by 3 to 15 seconds.
            ((TileEntityBotanyPot) tile).addGrowth(MathsUtils.nextIntInclusive(random, 3, 15) * 20);
        }
    }
    
    @Override
    public boolean hasComparatorInputOverride (BlockState state) {
        
        return true;
    }
    
    @Override
    public int getComparatorInputOverride (BlockState blockState, World world, BlockPos pos) {
        
        if (world.isBlockLoaded(pos)) {
            
            final TileEntity tile = world.getTileEntity(pos);
            
            if (tile instanceof TileEntityBotanyPot) {
                
                return ((TileEntityBotanyPot) tile).isDoneGrowing() ? 15 : super.getComparatorInputOverride(blockState, world, pos);
            }
        }
        
        return super.getComparatorInputOverride(blockState, world, pos);
    }
    
    @Override
    public int getLightValue (BlockState state, IBlockReader world, BlockPos pos) {
        
        int light = super.getLightValue(state, world, pos);
        
        final TileEntity tile = world.getTileEntity(pos);
        
        if (tile instanceof TileEntityBotanyPot) {
            
            final TileEntityBotanyPot pot = (TileEntityBotanyPot) tile;
            
            if (pot.getSoil() != null) {
                
                final int soilLight = pot.getSoil().getLightLevel(world, pos);
                
                if (soilLight > light) {
                    
                    light = soilLight;
                }
            }
            
            if (pot.getCrop() != null) {
                
                final int cropLight = pot.getCrop().getLightLevel(world, pos);
                
                if (cropLight > light) {
                    
                    light = cropLight;
                }
            }
        }
        
        return light;
    }
    
    @Override
    public int getOpacity (BlockState state, IBlockReader worldIn, BlockPos pos) {
        
        return 0;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        return this.getDefaultState().with(WATERLOGGED, fluidstate.getFluid() == Fluids.WATER);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }

        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
}