package net.darkhax.botanypots.addons.crafttweaker.soil;

import java.util.Arrays;
import java.util.HashSet;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.impl.blocks.MCBlockState;
import com.blamejared.crafttweaker.impl.managers.CTCraftingTableManager;

import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.util.ResourceLocation;

public class ActionSoilCreate implements IRuntimeAction {
    
    private final SoilInfo info;
    
    public ActionSoilCreate(String id, IIngredient ingredient, MCBlockState displayState, float growthModifier, String[] categories) {
        
        this.info = new SoilInfo(ResourceLocation.tryCreate(id), ingredient.asVanillaIngredient(), displayState.getInternal(), growthModifier, new HashSet<>(Arrays.asList(categories)));
    }
    
    @Override
    public void apply () {
        
        BotanyPotHelper.getSoilData(CTCraftingTableManager.recipeManager).put(this.info.getId(), this.info);
    }
    
    @Override
    public String describe () {
        
        BotanyPots.LOGGER.info("A CraftTweaker script created a new soil with ID {}.", this.info.getId());
        return "[Botany Pots] Created new soil with ID " + this.info.getId();
    }
    
}