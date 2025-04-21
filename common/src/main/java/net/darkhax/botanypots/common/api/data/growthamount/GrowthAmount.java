package net.darkhax.botanypots.common.api.data.growthamount;

import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Calculates an amount of growth to apply to a crop based on predetermined inputs. For example, a fertilizer may use
 * this to calculate how much growth to add.
 */
public interface GrowthAmount {

    /**
     * Gets the amount of growth to add.
     *
     * @param context The context of the crop being grown.
     * @param level   The current game level.
     * @return The amount of ticks to grow.
     */
    int getAmount(@NotNull BotanyPotContext context, @NotNull Level level);

    /**
     * Gets the type of the growth amount. This is often used for serialization.
     *
     * @return The type of the growth amount.
     */
    GrowthAmountType<?> getType();
}