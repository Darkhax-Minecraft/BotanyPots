package net.darkhax.botanypots.common.impl.block;

import com.mojang.serialization.MapCodec;
import net.darkhax.bookshelf.common.api.block.IBlockHooks;
import net.darkhax.botanypots.common.api.data.context.BlockEntityContext;
import net.darkhax.botanypots.common.api.data.recipes.fertilizer.Fertilizer;
import net.darkhax.botanypots.common.api.data.recipes.interaction.PotInteraction;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToIntFunction;

public class BotanyPotBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, IBlockHooks {

    public static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 8, 14);
    public static final ToIntFunction<BlockState> LIGHT_LEVEL = state -> state.getValue(BlockStateProperties.LEVEL);

    public final PotType type;

    public BotanyPotBlock(MapColor color, PotType type) {
        this(BlockBehaviour.Properties.of().mapColor(color).strength(1.25f, 4.2f).noOcclusion().lightLevel(LIGHT_LEVEL), type);
    }

    public BotanyPotBlock(Properties properties, PotType type) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any()
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(BlockStateProperties.LEVEL, 0)
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        );
        this.type = type;
    }

    public boolean isHopper() {
        return this.type == PotType.HOPPER;
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (this.type != PotType.WAXED && level.getBlockEntity(pos) instanceof BotanyPotBlockEntity pot) {
            final BlockEntityContext context = new BlockEntityContext(pot, player, hand);

            // Harvest basic pots
            if (this.type == PotType.BASIC && pot.canHarvest()) {
                pot.growthTime.reset();
                if (level instanceof ServerLevel serverLevel) {
                    pot.getOrInvalidateCrop().onHarvest(pot.getRecipeContext(), level, dropOutput -> Block.popResource(level, pos, dropOutput));
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(pot.getBlockState()));
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }

            // Fertilizer
            final RecipeHolder<Fertilizer> fertilizer = Fertilizer.getFertilizer(level, context, player.getItemInHand(hand));
            if (fertilizer != null && fertilizer.value().matches(context, level)) {
                fertilizer.value().apply(context, level);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }

            // Pot Interaction
            final RecipeHolder<PotInteraction> interaction = PotInteraction.getInteraction(level, context, player.getItemInHand(hand));
            if (interaction != null && interaction.value().matches(context, level)) {
                interaction.value().apply(context);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        // Menu
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        else {
            openMenu(state, level, pos, player);
            return ItemInteractionResult.CONSUME;
        }
    }

    public void openMenu(BlockState state, Level level, BlockPos pos, Player player) {
        MenuProvider menuprovider = this.getMenuProvider(state, level, pos);
        if (menuprovider != null) {
            player.openMenu(menuprovider);
        }
    }

    @Nullable
    protected MenuProvider getMenuProvider(@NotNull BlockState state, Level level, @NotNull BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof BotanyPotBlockEntity pot) {
            return pot;
        }
        return super.getMenuProvider(state, level, pos);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, BotanyPotBlockEntity.TYPE.get(), BotanyPotBlockEntity::tickPot);
    }

    @NotNull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        throw new RuntimeException("Codecs for this block have not been implemented yet. Sorry :(");
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockPos placedPos = context.getClickedPos();
        final FluidState fluidState = context.getLevel().getFluidState(placedPos);
        BlockState placedState = super.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
        if (context.getNearestLookingDirection().getAxis().isHorizontal()) {
            placedState = placedState.setValue(BlockStateProperties.HORIZONTAL_FACING, context.getNearestLookingDirection().getOpposite());
        }
        return placedState;
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        Containers.dropContentsOnDestroy(state, newState, level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @NotNull
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.getFluidState().isEmpty();
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, Level world, @NotNull BlockPos pos) {
        return world.getBlockEntity(pos) instanceof BotanyPotBlockEntity pot ? pot.comparatorLevel : 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.WATERLOGGED, BlockStateProperties.LEVEL, BlockStateProperties.HORIZONTAL_FACING);
    }

    @NotNull
    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new BotanyPotBlockEntity(pos, state);
    }
}