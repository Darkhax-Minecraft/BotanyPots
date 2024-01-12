package net.darkhax.botanypots.block;

import com.mojang.serialization.MapCodec;
import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.block.IBindRenderLayer;
import net.darkhax.bookshelf.api.block.InventoryBlock;
import net.darkhax.bookshelf.api.data.bytebuf.BookshelfByteBufs;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.data.recipes.fertilizer.Fertilizer;
import net.darkhax.botanypots.data.recipes.potinteraction.PotInteraction;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockBotanyPot extends InventoryBlock implements SimpleWaterloggedBlock, IBindRenderLayer {

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 8, 14);
    private static final Properties DEFAULT_PROPERTIES = BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_WHITE).strength(1.25F, 4.2F).noOcclusion().lightLevel(state -> state.getValue(BlockStateProperties.LEVEL));

    private final boolean hasInventory;

    public BlockBotanyPot(boolean hasInventory) {

        this(DEFAULT_PROPERTIES, hasInventory);
    }

    public BlockBotanyPot(BlockBehaviour.Properties properties, boolean hasInventory) {

        super(properties);

        BlockState defaultState = this.getStateDefinition().any();
        defaultState = defaultState.setValue(BlockStateProperties.WATERLOGGED, false);
        defaultState = defaultState.setValue(BlockStateProperties.LEVEL, 0);
        defaultState = defaultState.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
        this.registerDefaultState(defaultState);

        this.hasInventory = hasInventory;
    }

    public boolean hasInventory() {

        return this.hasInventory;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        throw new RuntimeException("Codecs for this block have not been implemented yet. Sorry :(");
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {

        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {

        return SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        final BlockPos placedPos = context.getClickedPos();
        final FluidState fluidState = context.getLevel().getFluidState(placedPos);

        BlockState placedState = super.getStateForPlacement(context).setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);

        if (context.getNearestLookingDirection().getAxis().isHorizontal()) {
            placedState = placedState.setValue(BlockStateProperties.HORIZONTAL_FACING, context.getNearestLookingDirection().getOpposite());
        }

        return placedState;
    }

    @Override
    public FluidState getFluidState(BlockState state) {

        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {

        return state.getFluidState().isEmpty();
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {

        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {

        return world.getBlockEntity(pos) instanceof BlockEntityBotanyPot pot ? pot.getComparatorLevel() : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(BlockStateProperties.WATERLOGGED, BlockStateProperties.LEVEL, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {

        return new BlockEntityBotanyPot(BlockEntityBotanyPot.POT_TYPE.get(), pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (world.getBlockEntity(pos) instanceof BlockEntityBotanyPot potEntity) {

            final ItemStack heldStack = player.getItemInHand(hand);

            // Apply fertilizers, only if a valid crop is growing.
            if (potEntity.areGrowthConditionsMet() && potEntity.getGrowthTime() > 0 && !potEntity.doneGrowing) {

                final RecipeHolder<Fertilizer> fertilizerRecipe = BotanyPotHelper.findFertilizer(state, world, pos, player, hand, heldStack, potEntity);

                if (fertilizerRecipe != null) {

                    fertilizerRecipe.value().apply(state, world, pos, player, hand, heldStack, potEntity);
                    return InteractionResult.CONSUME;
                }
            }

            // Attempt right click interaction recipes.
            final RecipeHolder<PotInteraction> interactionRecipe = BotanyPotHelper.findPotInteraction(state, world, pos, player, hand, heldStack, potEntity);

            if (interactionRecipe != null) {

                interactionRecipe.value().apply(state, world, pos, player, hand, heldStack, potEntity);
                return InteractionResult.CONSUME;
            }

            // Attempt harvesting the pot.
            else if (!player.isCrouching() && !potEntity.isHopper() && potEntity.doneGrowing && potEntity.getCrop() != null) {

                if (!world.isClientSide) {

                    for (ItemStack drop : BotanyPotHelper.generateDrop(potEntity.rng, world, pos, potEntity, potEntity.getCrop())) {

                        popResource(world, pos, drop);
                    }

                    potEntity.resetGrowth();
                }

                return InteractionResult.CONSUME;
            }

            // Open the pot GUI
            else if (player instanceof ServerPlayer serverPlayer) {

                Services.INVENTORY_HELPER.openMenu(serverPlayer, potEntity, buf -> BookshelfByteBufs.BLOCK_POS.write(buf, pos));
                return InteractionResult.CONSUME;
            }

            return InteractionResult.SUCCESS;
        }

        return super.use(state, world, pos, player, hand, hitResult);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level worldLevel, BlockState state, BlockEntityType<T> blockEntityType) {

        return createTickerHelper(blockEntityType, BlockEntityBotanyPot.POT_TYPE.get(), BlockEntityBotanyPot::tickPot);
    }

    @Override
    public RenderType getRenderLayerToBind() {

        return RenderType.cutout();
    }
}