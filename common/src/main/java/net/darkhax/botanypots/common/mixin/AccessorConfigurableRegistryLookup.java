package net.darkhax.botanypots.common.mixin;

import net.minecraft.core.RegistryAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "net.minecraft.server.ReloadableServerResources$ConfigurableRegistryLookup")
public interface AccessorConfigurableRegistryLookup {

    @Accessor("registryAccess")
    RegistryAccess botanypots$getRegistry();
}
