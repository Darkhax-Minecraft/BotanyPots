package net.darkhax.botanypots.addons.crt;

import org.openzen.zencode.java.ZenCodeType;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.BracketResolver;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;

import net.darkhax.bookshelf.Bookshelf;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.soil.SoilInfo;
import net.darkhax.botanypots.soil.SoilReloadListener;
import net.minecraft.util.ResourceLocation;

@ZenRegister
@ZenCodeType.Name("mods.botanypots.BotanyPots")
public class BotanyPotsCraftTweaker {
    
    @ZenCodeType.Method
    public static int getFertilizerTicks(IItemStack item) {
        
        return BotanyPotHelper.getFertilizerTicks(item.getInternal(), Bookshelf.RANDOM);
    }
    
    @ZenCodeType.Method
    public static boolean isSoilValidForCrop(SoilInfo soil, CropInfo crop) {
        
        return BotanyPotHelper.isSoilValidForCrop(soil, crop);
    }
    
    @ZenCodeType.Method
    public static CropInfo getCropForItem(IItemStack item) {
        
        return BotanyPotHelper.getCropForItem(item.getInternal());
    }
    
    @ZenCodeType.Method
    public static SoilInfo getSoilForItem(IItemStack item) {
        
        return BotanyPotHelper.getSoilForItem(item.getInternal());
    }
    
    @ZenCodeType.Method
    public static int getRequiredGrowthTicks(CropInfo crop, SoilInfo soil) {
        
        return BotanyPotHelper.getRequiredGrowthTicks(crop, soil);
    }
    
    @BracketResolver("botanypots_soil")
    public static CrTSoilInfo getSoil(String tokens) {
        
        final ResourceLocation id = ResourceLocation.tryCreate(tokens);
        
        if (id != null) {
            
            final SoilInfo soil = SoilReloadListener.registeredSoil.get(id);
            return soil != null ? new CrTSoilInfo(soil) : new CrTSoilInfo(id.toString());
        }
        
        else {
            
            throw new IllegalArgumentException("Token " + tokens + " is not a valid id!");
        }
    }
    
    @ZenCodeType.Method
    public static void addSoil(CrTSoilInfo soil) {
        
        
        CraftTweakerAPI.apply(new ActionAddSoil(soil.toSoil()));
    }
    
    @ZenCodeType.Method
    public static void removeSoil(CrTSoilInfo soil) {
        
        CraftTweakerAPI.apply(new ActionRemoveSoil(soil.toSoil()));
    }
    
    @ZenCodeType.Method
    public static void removeSoil(String soil) {
        
        CraftTweakerAPI.apply(new ActionRemoveSoil(SoilReloadListener.registeredSoil.get(ResourceLocation.tryCreate(soil))));
    }
}