package net.darkhax.botanypots.common.impl.data.recipe.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.codecs.map.MapCodecs;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.bookshelf.common.mixin.access.block.AccessorCropBlock;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.darkhax.botanypots.common.impl.Helpers;
import net.darkhax.botanypots.common.impl.data.display.types.AgingDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.BasicOptions;
import net.darkhax.botanypots.common.impl.data.display.types.SimpleDisplayState;
import net.darkhax.botanypots.common.impl.data.itemdrops.BlockStateDrops;
import net.darkhax.botanypots.common.mixin.AccessorIntegerProperty;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockDerivedCrop extends BasicCrop {

    public static final MapCodec<BlockDerivedCrop> CODEC = Properties.CODEC.xmap(BlockDerivedCrop::new, BlockDerivedCrop::getProperties);
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockDerivedCrop> STREAM = Properties.STREAM.map(BlockDerivedCrop::new, BlockDerivedCrop::getProperties);
    public static final RecipeSerializer<BlockDerivedCrop> SERIALIZER = DataHelper.recipeSerializer(CODEC, STREAM);

    private final Properties properties;

    public BlockDerivedCrop(Properties properties) {
        super(properties.toBasic());
        this.properties = properties;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public record Properties(Block block, Optional<Ingredient> seed, Ingredient soil, int growTime, Optional<List<Display>> display, int lightLevel, Optional<List<ItemDropProvider>> drops, Optional<BasicOptions> renderOptions, Optional<ResourceLocation> functionId, Optional<BlockPredicate> potPredicate, float baseYield, float yieldScale) {
        public static final MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(Properties::block),
                Ingredient.CODEC.optionalFieldOf("input").forGetter(Properties::seed),
                Ingredient.CODEC.optionalFieldOf("soil", BasicCrop.DIRT).forGetter(Properties::soil),
                Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("grow_time", 1200).forGetter(Properties::growTime),
                DisplayType.LIST_CODEC.optionalFieldOf("display").forGetter(Properties::display),
                Codec.intRange(0, 15).optionalFieldOf("light_level", 0).forGetter(Properties::lightLevel),
                MapCodecs.flexibleList(ItemDropProviderType.DROP_PROVIDER_CODEC).optionalFieldOf("drops").forGetter(Properties::drops),
                BasicOptions.CODEC.optionalFieldOf("render_options").forGetter(Properties::renderOptions),
                ResourceLocation.CODEC.optionalFieldOf("function").forGetter(Properties::functionId),
                BlockPredicate.CODEC.optionalFieldOf("pot_predicate").forGetter(Properties::potPredicate),
                Codec.floatRange(0f, Float.MAX_VALUE).optionalFieldOf("yield", 1f).forGetter(Properties::baseYield),
                Codec.floatRange(0f, Float.MAX_VALUE).optionalFieldOf("yield_scale", 1f).forGetter(Properties::yieldScale)
        ).apply(instance, Properties::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Properties> STREAM = new StreamCodec<>() {
            @NotNull
            @Override
            public Properties decode(@NotNull RegistryFriendlyByteBuf buf) {
                final Block block = Helpers.BLOCK_STREAM.decode(buf);
                final BasicCrop.Properties props = BasicCrop.Properties.STREAM.decode(buf);
                return Properties.fromBasic(block, props);
            }

            @Override
            public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull Properties properties) {
                Helpers.BLOCK_STREAM.encode(buf, properties.block);
                BasicCrop.Properties.STREAM.encode(buf, properties.toBasic());
            }
        };

        public BasicCrop.Properties toBasic() {
            final Ingredient seed = this.seed.orElseGet(() -> getSeed(this.block));
            final BasicOptions rOptions = this.renderOptions.orElse(BasicOptions.ofDefault());
            final List<Display> display = this.display.orElseGet(() -> {
                final List<Display> states = new ArrayList<>();
                if (this.block.defaultBlockState().hasProperty(BlockStateProperties.HALF)) {
                    states.add(new SimpleDisplayState(this.block.defaultBlockState().setValue(BlockStateProperties.HALF, Half.BOTTOM), rOptions));
                    states.add(new SimpleDisplayState(this.block.defaultBlockState().setValue(BlockStateProperties.HALF, Half.TOP), rOptions));
                }
                else if (this.block.defaultBlockState().hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                    states.add(new SimpleDisplayState(this.block.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER), rOptions));
                    states.add(new SimpleDisplayState(this.block.defaultBlockState().setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER), rOptions));
                }
                else {
                    states.add(new AgingDisplayState(this.block, rOptions));
                }
                return states;
            });
            final List<ItemDropProvider> drops = this.drops.orElseGet(() -> List.of(new BlockStateDrops(getHarvestState(this.block))));
            return new BasicCrop.Properties(seed, this.soil, this.growTime, display, this.lightLevel, drops, this.functionId, this.potPredicate, this.baseYield, this.yieldScale);
        }

        private static Properties fromBasic(Block block, BasicCrop.Properties basicProps) {
            return new Properties(block, Optional.of(basicProps.input()), basicProps.soil(), basicProps.growTime(), Optional.of(basicProps.display()), basicProps.lightLevel(), Optional.of(basicProps.drops()), Optional.empty(), basicProps.functionId(), basicProps.potPredicate(), basicProps.baseYield(), basicProps.yieldScale());
        }

        private static BlockState getHarvestState(Block block) {
            final StateDefinition<Block, BlockState> stateDef = block.getStateDefinition();
            BlockState state = getAgedState(block);
            if (stateDef.getProperty("berries") instanceof BooleanProperty boolProp) {
                state = state.setValue(boolProp, true);
            }
            if (block instanceof MultifaceBlock) {
                state = state.setValue(MultifaceBlock.getFaceProperty(Direction.DOWN), true);
            }
            return state;
        }

        private static BlockState getAgedState(Block block) {
            if (block instanceof CropBlock cropBlock) {
                return cropBlock.getStateForAge(cropBlock.getMaxAge());
            }
            final Property<?> ageProp = block.getStateDefinition().getProperty("age");
            if (ageProp instanceof IntegerProperty intProp && intProp instanceof AccessorIntegerProperty accessor) {
                return block.defaultBlockState().setValue(intProp, accessor.botanypots$getMax());
            }
            return block.defaultBlockState();
        }

        private static Ingredient getSeed(Block block) {
            if (block instanceof AccessorCropBlock crop) {
                final ItemLike seedItem = crop.bookshelf$getSeed();
                if (seedItem != null && seedItem != Items.AIR) {
                    return Ingredient.of(seedItem);
                }
            }
            final Item placer = block.asItem();
            if (placer != Items.AIR) {
                return Ingredient.of(placer);
            }
            throw new IllegalArgumentException("Can not derive seed from block " + block + " id=" + BuiltInRegistries.BLOCK.getKey(block));
        }
    }
}