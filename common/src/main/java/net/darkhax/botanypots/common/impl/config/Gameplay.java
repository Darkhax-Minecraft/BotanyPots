package net.darkhax.botanypots.common.impl.config;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.darkhax.bookshelf.common.api.function.ReloadableCache;
import net.darkhax.botanypots.common.api.command.generator.DataHelper;
import net.darkhax.pricklemc.common.api.annotations.Value;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;

public class Gameplay {

    @Value(comment = "A global modifier applied to the growth times of all crops. Increasing this value will make crops grow faster.")
    public float global_growth_modifier = 1f;

    @Value(comment = "Determines if the item in the harvest tool slot should take durability damage.")
    public boolean damage_harvest_tool = true;

    @Value(comment = "The growth modifier applied for each level of efficiency that the harvest tool has.")
    public float efficiency_growth_modifier = 0.05f;

    @Value(comment = "If the player has not placed a harvest tool in a pot this default will be used. For example, if you set this to shears with the silk touch enchantment players will not need to use shears or silk touch in pots in order to get certain item drops.")
    public JsonObject default_harvest_stack = DataHelper.GSON.fromJson("{}", JsonObject.class);

    public ReloadableCache<ItemStack> default_harvest_tool = ReloadableCache.of(level -> ItemStack.OPTIONAL_CODEC.decode(RegistryOps.create(JsonOps.INSTANCE, level.registryAccess()), this.default_harvest_stack).getOrThrow().getFirst());
}