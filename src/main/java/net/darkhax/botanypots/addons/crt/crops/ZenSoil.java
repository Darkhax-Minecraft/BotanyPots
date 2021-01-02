package net.darkhax.botanypots.addons.crt.crops;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import net.minecraft.block.*;
import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;

import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.ZenSoil")
public class ZenSoil {
    
    private final SoilInfo internal;
    
    public ZenSoil(String id, IIngredient ingredient, BlockState renderState, float growthModifier, String[] categories, int lightLevel) {
        
        this(new SoilInfo(ResourceLocation.tryCreate(id), ingredient.asVanillaIngredient(), renderState, growthModifier, new HashSet<>(Arrays.asList(categories)), Optional.of(lightLevel)));
    }
    
    public ZenSoil(String id, IIngredient ingredient, BlockState renderState, float growthModifier, String[] categories) {
        
        this(new SoilInfo(ResourceLocation.tryCreate(id), ingredient.asVanillaIngredient(), renderState, growthModifier, new HashSet<>(Arrays.asList(categories)), Optional.empty()));
    }
    
    public ZenSoil(SoilInfo info) {
        
        this.internal = info;
    }
    
    @ZenCodeType.Method
    public ZenSoil addCategory (String category) {
        
        this.internal.getCategories().add(category);
        return this;
    }
    
    @ZenCodeType.Method
    public ZenSoil removeCategory (String category) {
        
        this.internal.getCategories().remove(category);
        return this;
    }
    
    @ZenCodeType.Method
    public ZenSoil clearCategories () {
        
        this.internal.getCategories().clear();
        return this;
    }
    
    @ZenCodeType.Method
    public ZenSoil setInput (IIngredient ingredient) {
        
        this.internal.setIngredient(ingredient.asVanillaIngredient());
        return this;
    }
    
    @ZenCodeType.Method
    public ZenSoil setDisplay (BlockState state) {
        
        this.internal.setRenderState(state);
        return this;
    }
    
    @ZenCodeType.Method
    public ZenSoil setGrowthModifier (float modifier) {
        
        this.internal.setGrowthModifier(modifier);
        return this;
    }
    
    @ZenCodeType.Method
    public ZenSoil setLightLevel (int lightLevel) {
        
        this.internal.setLightLevel(lightLevel);
        return this;
    }
    
    public SoilInfo getInternal () {
        
        return this.internal;
    }
}