package net.darkhax.botanypots.addons.crt;

import java.util.Map;
import java.util.Set;

import com.blamejared.crafttweaker.CraftTweaker;
import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.impl.commands.CTCommandCollectionEvent;
import com.blamejared.crafttweaker.impl.commands.CTCommands;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class BotanyDump implements CTCommands.CommandCallerPlayer {
    private final String dumpCommandName;
    private final String dumpedContentName;
    private final DumpConverter dumpConverter;
    
    public BotanyDump(String dumpCommandName, String dumpedContentName, DumpConverter dumpConverter) {
        
        this.dumpCommandName = dumpCommandName;
        this.dumpedContentName = dumpedContentName;
        this.dumpConverter = dumpConverter;
    }
    
    @Override
    public int executeCommand (PlayerEntity playerEntity, ItemStack itemStack) {
        
        this.dumpToLog(playerEntity);
        this.sendFeedBack(playerEntity);
        return 0;
    }
    
    private void sendFeedBack (PlayerEntity player) {
        
        final String format = "%sList of %s info generated! Check the crafttweaker.log file!%s";
        final String message = String.format(format, TextFormatting.GREEN, this.dumpedContentName, TextFormatting.RESET);
        final ITextComponent toSend = new StringTextComponent(message);
        
        player.sendMessage(toSend, CraftTweaker.CRAFTTWEAKER_UUID);
    }
    
    private void dumpToLog (PlayerEntity player) {
        
        final RecipeManager recipeManager = player.getEntityWorld().getRecipeManager();
        final Set<ResourceLocation> locationsToDump = this.dumpConverter.getFromManager(recipeManager).keySet();
        
        CraftTweakerAPI.logDump("List of all known %s:", this.dumpedContentName);
        
        for (final ResourceLocation location : locationsToDump) {
            CraftTweakerAPI.logDump("- %s", location);
        }
    }
    
    public void registerTo (CTCommandCollectionEvent event) {
        
        final String description = this.getDescription();
        event.registerDump(this.dumpCommandName, description, this);
        
    }
    
    private String getDescription () {
        
        return String.format("Outputs all known %s names to the log.", this.dumpedContentName);
    }
    
    interface DumpConverter {
        Map<ResourceLocation, ?> getFromManager (RecipeManager manager);
    }
}