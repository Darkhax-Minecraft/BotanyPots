package net.darkhax.botanypots.common.api.command.generator.soil;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

/**
 * Soil generators are used to find potential soil candidates and generate data pack files for them. Generators are not
 * meant to be perfect, they are only meant to save as much time as possible when writing data pack files.
 */
public interface SoilGenerator {

    /**
     * Determines if the generator can generate a soil for a specific item. If a generator returns true the item will be
     * considered claimed by the generator and other generators will not be given the chance to generate a soil for the
     * item.
     *
     * @param level The current world level. Used to provide context like registry access and the recipe manager.
     * @param stack The item to test.
     * @return If the generator can generate a soil for the provided item.
     */
    boolean canGenerateSoil(ServerLevel level, ItemStack stack);

    /**
     * Generates the JSON data for the soil.
     *
     * @param level The current world level. Used to provide context like registry access and the recipe manager.
     * @param stack The item to test.
     * @return The JSON representation of the soil. The produced data must be a valid JSON representation that can be
     * parsed by a registered recipe serializer.
     */
    JsonObject generateData(ServerLevel level, ItemStack stack);
}