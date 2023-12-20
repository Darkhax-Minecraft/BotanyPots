package net.darkhax.botanypots.data.displaystate.types;

import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.data.displaystate.DisplayTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class DisplayState {

    public abstract DisplayTypes.DisplayType<?> getType();


    public static Optional<Map<String, String>> encodeProperties(BlockState state) {

        final Map<String, String> propertyMap = new HashMap<>();

        for (Map.Entry<Property<?>, Comparable<?>> entry : state.getValues().entrySet()) {

            propertyMap.put(entry.getKey().getName(), ((Property) entry.getKey()).getName(entry.getValue()));
        }

        return Optional.ofNullable(propertyMap.isEmpty() ? null : propertyMap);
    }

    public static BlockState decodeBlockState(Block block, Optional<Map<String, String>> properties) {

        BlockState state = block.defaultBlockState();

        if (properties.isPresent()) {

            final StateDefinition<Block, BlockState> definition = block.getStateDefinition();

            for (Map.Entry<String, String> entry : properties.get().entrySet()) {

                final Property<? extends Comparable<?>> property = definition.getProperty(entry.getKey());

                if (property != null) {

                    final Optional<?> value = property.getValue(entry.getValue());

                    if (value.isPresent()) {

                        try {

                            state = state.setValue((Property) property, (Comparable) value.get());
                        }

                        catch (final Exception e) {

                            Constants.LOG.error("Failed to update state for block {} with valid value {}={}. The mod that adds this block may have a serious issue.", BuiltInRegistries.BLOCK.getKey(block), entry.getKey(), entry.getValue());
                            return state;
                        }
                    }

                    else {

                        Constants.LOG.error("\"{}\" is not a valid value for property \"{}\" on block \"{}\". Available values: {}", entry.getValue(), property.getName(), BuiltInRegistries.BLOCK.getKey(block), property.getAllValues().map(propVal -> ((Property) property).getName(propVal.value())).collect(Collectors.joining()));
                        return state;
                    }
                }

                else {

                    Constants.LOG.error("The property \"{}\" is not valid for block \"{}\". Available properties: {}", entry.getKey(), BuiltInRegistries.BLOCK.getKey(block), definition.getProperties().stream().map(Property::getName).collect(Collectors.joining()));
                    return state;
                }
            }
        }

        return state;
    }
}