package net.darkhax.botanypots.common.impl.data.display.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.Helpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;

public class AgingDisplayState extends PhasedDisplayState {

    public static final ResourceLocation TYPE_ID = BotanyPotsMod.id("aging");
    public static final CachedSupplier<DisplayType<AgingDisplayState>> TYPE = CachedSupplier.cache(() -> DisplayType.get(TYPE_ID));
    public static final MapCodec<AgingDisplayState> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Helpers.BLOCK_CODEC.fieldOf("block").forGetter(AgingDisplayState::getBlock), BasicOptions.CODEC.optionalFieldOf("options", BasicOptions.ofDefault()).forGetter(AgingDisplayState::getRenderOptions)).apply(instance, AgingDisplayState::new));
    public static final StreamCodec<FriendlyByteBuf, AgingDisplayState> STREAM = StreamCodec.composite(Helpers.BLOCK_STREAM, AgingDisplayState::getBlock, BasicOptions.STREAM, AgingDisplayState::getRenderOptions, AgingDisplayState::new);

    private final Block block;
    private final BasicOptions renderOptions;
    private final List<Display> phases;

    public AgingDisplayState(Block block, BasicOptions renderOptions) {
        this.block = block;
        this.renderOptions = renderOptions;
        this.phases = calculatePhases(block);
    }

    private List<Display> calculatePhases(Block block) {
        final List<Display> phases = new ArrayList<>();
        if (block instanceof CropBlock crop) {
            for (int cropStage = 0; cropStage < crop.getMaxAge(); cropStage++) {
                try {
                    final BlockState agedState = crop.getStateForAge(cropStage);
                    phases.add(new SimpleDisplayState(agedState, this.renderOptions));
                }
                catch (Exception e) {
                    BotanyPotsMod.LOG.error("Invalid crop age found! id={} block={} age={}", BuiltInRegistries.BLOCK.getKey(block), block, cropStage);
                    BotanyPotsMod.LOG.error("Error: ", e);
                }
            }
        }
        else if (block.defaultBlockState().hasProperty(BlockStateProperties.FLOWER_AMOUNT)) {
            final Property<?> flowerCount = block.getStateDefinition().getProperty("flower_amount");
            if (flowerCount instanceof IntegerProperty property) {
                for (int amount : property.getPossibleValues()) {
                    phases.add(new SimpleDisplayState(block.defaultBlockState().setValue(property, amount), this.renderOptions));
                }
            }
        }
        else {
            final Property<?> ageProperty = block.getStateDefinition().getProperty("age");
            if (ageProperty instanceof IntegerProperty intProperty) {
                for (int age : intProperty.getPossibleValues()) {
                    phases.add(new SimpleDisplayState(block.defaultBlockState().setValue(intProperty, age), this.renderOptions));
                }
            }
            else {
                phases.add(new SimpleDisplayState(block.defaultBlockState(), this.renderOptions));
            }
        }
        return phases;
    }

    public Block getBlock() {
        return this.block;
    }

    public BasicOptions getRenderOptions() {
        return this.renderOptions;
    }

    @Override
    public List<Display> getDisplayPhases() {
        return this.phases;
    }

    @Override
    public DisplayType<?> getType() {
        return TYPE.get();
    }
}