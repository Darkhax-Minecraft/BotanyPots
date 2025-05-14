package net.darkhax.botanypots.common.mixin;

import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IntegerProperty.class)
public interface AccessorIntegerProperty {

    @Accessor("max")
    int botanypots$getMax();
}