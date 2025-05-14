package net.darkhax.botanypots.common.mixin;

import com.google.gson.JsonElement;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.ref.WeakReference;
import java.util.Map;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {

    @Shadow
    @Final
    private HolderLookup.Provider registries;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("HEAD"))
    private void onLoad(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
        if (this.registries instanceof AccessorConfigurableRegistryLookup registryAccess) {
            BotanyPotsMod.REGISTRY_ACCESS = new WeakReference<>(registryAccess.botanypots$getRegistry());
            BotanyPotsMod.LOG.info("Updating registry access.");
        }
    }
}