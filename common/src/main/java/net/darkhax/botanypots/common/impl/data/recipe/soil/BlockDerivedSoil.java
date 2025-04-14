package net.darkhax.botanypots.common.impl.data.recipe.soil;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.util.DataHelper;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.impl.Helpers;
import net.darkhax.botanypots.common.impl.data.display.types.BasicOptions;
import net.darkhax.botanypots.common.impl.data.display.types.SimpleDisplayState;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.Set;

public class BlockDerivedSoil extends BasicSoil {

    public static final MapCodec<BlockDerivedSoil> CODEC = BlockProperties.CODEC.xmap(BlockDerivedSoil::new, BlockDerivedSoil::getBlockProperties);
    public static final StreamCodec<RegistryFriendlyByteBuf, BlockDerivedSoil> STREAM = BlockProperties.STREAM.map(BlockDerivedSoil::new, BlockDerivedSoil::getBlockProperties);
    public static final RecipeSerializer<BlockDerivedSoil> SERIALIZER = DataHelper.recipeSerializer(CODEC, STREAM);

    private final BlockProperties properties;

    public BlockDerivedSoil(BlockProperties properties) {
        super(properties.toBasic());
        this.properties = properties;
    }

    public BlockProperties getBlockProperties() {
        return this.properties;
    }

    public record BlockProperties(Block block, Optional<Ingredient> input, Optional<Display> display, float growthModifier, int lightLevel, Optional<BasicOptions> renderOptions) {
        public static final MapCodec<BlockProperties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter(BlockProperties::block),
                Ingredient.CODEC.optionalFieldOf("input").forGetter(BlockProperties::input),
                DisplayType.DISPLAY_STATE_CODEC.optionalFieldOf("display").forGetter(BlockProperties::display),
                Codec.FLOAT.optionalFieldOf("growth_modifier", 0f).forGetter(BlockProperties::growthModifier),
                Codec.INT.optionalFieldOf("light_level", 0).forGetter(BlockProperties::lightLevel),
                BasicOptions.CODEC.optionalFieldOf("render_options").forGetter(BlockProperties::renderOptions)
        ).apply(instance, BlockProperties::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, BlockProperties> STREAM = StreamCodec.of(
                (buf, props) -> {
                    Helpers.BLOCK_STREAM.encode(buf, props.block);
                    BasicSoil.Properties.STREAM.encode(buf, props.toBasic());
                },
                (buf) -> {
                    final Block block = Helpers.BLOCK_STREAM.decode(buf);
                    final BasicSoil.Properties properties = BasicSoil.Properties.STREAM.decode(buf);
                    return fromBasic(block, properties);
                }
        );

        public BasicSoil.Properties toBasic() {
            final Ingredient input = this.input.orElseGet(() -> getSoil(this.block));
            final BasicOptions options = this.renderOptions.orElseGet(() -> BasicOptions.ofDefault(Set.of(Direction.UP)));
            final Display display = this.display.orElseGet(() -> new SimpleDisplayState(this.block.defaultBlockState(), options));
            return new BasicSoil.Properties(input, display, this.growthModifier, this.lightLevel);
        }

        public static BlockProperties fromBasic(Block block, BasicSoil.Properties properties) {
            return new BlockProperties(block, Optional.of(properties.input()), Optional.of(properties.display()), properties.growthModifier(), properties.lightLevel(), Optional.empty());
        }

        private static Ingredient getSoil(Block block) {
            final Item blockItem = block.asItem();
            if (blockItem != Items.AIR) {
                return Ingredient.of(blockItem);
            }
            throw new IllegalStateException("Can not derive soil from block " + block + " id=" + BuiltInRegistries.BLOCK.getKey(block));
        }
    }
}
