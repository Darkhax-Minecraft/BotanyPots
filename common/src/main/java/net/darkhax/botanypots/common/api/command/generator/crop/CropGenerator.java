package net.darkhax.botanypots.common.api.command.generator.crop;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

/**
 * Crop generators are used to find potential crops and generate data pack files for them. Generators are not meant to
 * be perfect, they are only meant to save as much time as possible when writing data pack files.
 */
public interface CropGenerator {

    /**
     * Determines if te generator can generate a crop for a specific item. If a generator returns true the item will be
     * considered claimed by the generator and other generators will not be given a chance to generate a crop for the
     * item.
     *
     * @param level The current world level. Used to provide context like registry access and the recipe manager.
     * @param stack The item to test.
     * @return If the generator can generate a crop for the provided item.
     */
    boolean canGenerateCrop(ServerLevel level, ItemStack stack);

    /**
     * Generates the JSON data for the crop.
     *
     * @param level The current world level. Used to provide context like registry access and the recipe manager.
     * @param stack The item to test.
     * @return The JSON representation of the crop. The produced data must be a valid JSON representation that can be
     * parsed by a registered recipe serializer.
     */
    JsonObject generateData(ServerLevel level, ItemStack stack);
}
