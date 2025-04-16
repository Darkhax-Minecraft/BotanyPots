package net.darkhax.botanypots.common.api.command.generator.soil;

import com.google.gson.JsonObject;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface SoilGenerator {

    boolean canGenerateSoil(Level level, ItemStack stack);

    JsonObject generateData(Level level, ItemStack stack);
}