package net.darkhax.botanypots.addons.crt.crops;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;

import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.crop.HarvestEntry;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.ZenCrop")
public class ZenCrop {
    
    private final CropInfo internal;
    
    public ZenCrop(String id, IIngredient seed, MCBlockState[] display, int ticks, String[] categories, int lightLevel) {
        
        this(new CropInfo(ResourceLocation.tryCreate(id), seed.asVanillaIngredient(), new HashSet<>(Arrays.asList(categories)), ticks, new ArrayList<>(), getBlockStates(display), Optional.of(lightLevel)));
    }
    
    public ZenCrop(String id, IIngredient seed, MCBlockState[] display, int ticks, String[] categories) {
        
        this(new CropInfo(ResourceLocation.tryCreate(id), seed.asVanillaIngredient(), new HashSet<>(Arrays.asList(categories)), ticks, new ArrayList<>(), getBlockStates(display), Optional.empty()));
    }
    
    public ZenCrop(CropInfo crop) {
        
        this.internal = crop;
    }
    
    @ZenCodeType.Method
    public ZenCrop addCategory (String category) {
        
        this.internal.getSoilCategories().add(category);
        return this;
    }
    
    @ZenCodeType.Method
    public ZenCrop removeCategory (String category) {
        
        this.internal.getSoilCategories().remove(category);
        return this;
    }
    
    @ZenCodeType.Method
    public ZenCrop clearCategories () {
        
        this.internal.getSoilCategories().clear();
        return this;
    }
    
    @ZenCodeType.Method
    public ZenCrop addDrop (IItemStack item, float chance) {
        
        return this.addDrop(item, chance, 1);
    }
    
    @ZenCodeType.Method
    public ZenCrop addDrop (IItemStack item, float chance, int rolls) {
        
        return this.addDrop(item, chance, rolls, rolls);
    }
    
    @ZenCodeType.Method
    public ZenCrop addDrop (IItemStack item, float chance, int min, int max) {
        
        this.internal.getResults().add(new HarvestEntry(chance, item.getInternal(), min, max));
        return this;
    }
    
    @ZenCodeType.Method
    public ZenCrop clearDrops () {
        
        this.internal.getResults().clear();
        return this;
    }
    
    @ZenCodeType.Method
    public ZenCrop removeDrop (IIngredient toRemove) {
        
        final Ingredient ingredient = toRemove.asVanillaIngredient();
        this.internal.getResults().removeIf(drop -> ingredient.test(drop.getItem()));
        return this;
    }
    
    @ZenCodeType.Method
    public ZenCrop setGrowthTicks (int ticks) {
        
        this.internal.setGrowthTicks(ticks);
        return this;
    }
    
    @ZenCodeType.Method
    public ZenCrop setSeed (IIngredient seed) {
        
        this.internal.setSeed(seed.asVanillaIngredient());
        return this;
    }
    
    @ZenCodeType.Method
    public ZenCrop setDisplay (MCBlockState state) {
        
        this.internal.setDisplayBlock(getBlockStates(state));
        return this;
    }
    
    @ZenCodeType.Method
    public ZenCrop setDisplay (MCBlockState[] states) {
        
        this.internal.setDisplayBlock(getBlockStates(states));
        return this;
    }
    
    @ZenCodeType.Method
    public ZenCrop setLightLevel (int lightLevel) {
        
        this.internal.setLightLevel(lightLevel);
        return this;
    }
    
    public CropInfo getInternal () {
        
        return this.internal;
    }
    
    public static List<BlockState> getBlockStates (Collection<MCBlockState> states) {
        
        return states.stream().map(MCBlockState::getInternal).collect(Collectors.toList());
    }
    
    public static List<MCBlockState> getMCBlockStates (Collection<BlockState> states) {
        
        return states.stream().map(MCBlockState::new).collect(Collectors.toList());
    }
    
    public static BlockState[] getBlockStates (MCBlockState... states) {
        
        return Arrays.stream(states).map(MCBlockState::getInternal).toArray(BlockState[]::new);
    }
    
    public static MCBlockState[] getMCBlockStates (BlockState... states) {
        
        return Arrays.stream(states).map(MCBlockState::new).toArray(MCBlockState[]::new);
    }
}