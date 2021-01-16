package net.darkhax.botanypots.addons.crt;

import java.util.Map;
import java.util.function.Function;

import com.blamejared.crafttweaker.CraftTweaker;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.impl.commands.CTCommandCollectionEvent;
import com.blamejared.crafttweaker.impl.commands.CTCommands;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class BotanyDump implements CTCommands.CommandCallerPlayer {
    
    private final String dumpCommandName;
    private final String dumpedContentName;
    private final Function<RecipeManager, Map<ResourceLocation, ?>> dumpConverter;
    private final ITextComponent positiveFeedback;
    
    public BotanyDump(String dumpCommandName, String dumpedContentName, Function<RecipeManager, Map<ResourceLocation, ?>> dumpConverter) {
        
        this.dumpCommandName = dumpCommandName;
        this.dumpedContentName = dumpedContentName;
        this.dumpConverter = dumpConverter;
        this.positiveFeedback = new TranslationTextComponent("botanypots.log.crt.dump", this.dumpCommandName).mergeStyle(TextFormatting.GREEN);
    }
    
    @Override
    public int executeCommand (PlayerEntity player, ItemStack stack) {
        
        final RecipeManager recipeManager = player.getEntityWorld().getRecipeManager();      
        CraftTweakerAPI.logDump("List of all known %s:", this.dumpedContentName);       
        this.dumpConverter.apply(recipeManager).keySet().forEach(id -> CraftTweakerAPI.logDump("- %s", id));       
        player.sendMessage(this.positiveFeedback, CraftTweaker.CRAFTTWEAKER_UUID);
        return 0;
    }
    
    public void registerTo (CTCommandCollectionEvent event) {
        
        final String description = String.format("Outputs all known %s names to the log.", this.dumpedContentName);
        event.registerDump(this.dumpCommandName, description, this);        
    }
}