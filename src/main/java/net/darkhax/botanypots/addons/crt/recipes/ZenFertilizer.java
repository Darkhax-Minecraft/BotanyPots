package net.darkhax.botanypots.addons.crt.recipes;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;

import net.darkhax.botanypots.addons.crt.CrTSidedExecutor;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.ZenFertilizer")
public class ZenFertilizer {
    
    private final FertilizerInfo internal;
    
    public ZenFertilizer(String id, IIngredient input, int min, int max) {
        
        this(new FertilizerInfo(ResourceLocation.tryCreate(id), input.asVanillaIngredient(), min, max));
    }
    
    public ZenFertilizer(FertilizerInfo info) {
        
        this.internal = info;
    }
    
    @ZenCodeType.Method
    public ZenFertilizer setInput (IIngredient input) {
        
        CrTSidedExecutor.runOnServer( () -> this.internal.setIngredient(input.asVanillaIngredient()));
        return this;
    }
    
    @ZenCodeType.Method
    public ZenFertilizer setGrowthAmount (int ticks) {
        
        CrTSidedExecutor.runOnServer( () -> this.setGrowthAmount(ticks, ticks));
        return this;
    }
    
    @ZenCodeType.Method
    public ZenFertilizer setGrowthAmount (int min, int max) {
        
        CrTSidedExecutor.runOnServer( () -> {
            this.internal.setMaxTicks(max);
            this.internal.setMinTicks(min);
        });
        
        return this;
    }
    
    @ZenCodeType.Method
    public ResourceLocation getId () {
        
        return this.internal.getId();
    }
    
    public FertilizerInfo getInternal () {
        
        return this.internal;
    }
}
