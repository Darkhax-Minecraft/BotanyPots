package net.darkhax.botanypots.common.impl.data.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkhax.bookshelf.common.api.data.conditions.ConditionType;
import net.darkhax.bookshelf.common.api.data.conditions.ILoadCondition;
import net.darkhax.bookshelf.common.api.data.conditions.LoadConditions;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.config.Config;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record ConfigLoadCondition(String property) implements ILoadCondition {

    public static final ResourceLocation TYPE_ID = BotanyPotsMod.id("config");
    public static final Supplier<ConditionType> TYPE = CachedSupplier.cache(() -> LoadConditions.getType(TYPE_ID));
    public static final MapCodec<ConfigLoadCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.STRING.fieldOf("property").forGetter(ConfigLoadCondition::property)).apply(instance, ConfigLoadCondition::new));

    private static final Supplier<Map<String, Supplier<Boolean>>> PROPERTIES = CachedSupplier.cache(() -> {
        final Config cfg = Config.INSTANCE.get();
        final Map<String, Supplier<Boolean>> properties = new HashMap<>();
        properties.put("can_craft_basic_pots", () -> cfg.recipes.craft_basic_pots);
        properties.put("can_craft_hopper_pots", () -> cfg.recipes.craft_hopper_pots);
        properties.put("can_wax_pots", () -> cfg.recipes.craft_wax_pots);
        return properties;
    });

    @Override
    public boolean allowLoading() {
        return PROPERTIES.get().get(this.property).get();
    }

    @Override
    public ConditionType getType() {
        return TYPE.get();
    }
}