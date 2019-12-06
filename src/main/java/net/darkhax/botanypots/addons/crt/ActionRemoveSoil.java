package net.darkhax.botanypots.addons.crt;

import org.openzen.zenscript.codemodel.expression.ThisExpression;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.blamejared.crafttweaker.api.logger.ILogger;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.soil.SoilInfo;
import net.darkhax.botanypots.soil.SoilReloadListener;

public class ActionRemoveSoil implements IRuntimeAction {

    public final SoilInfo toRemove;

    public ActionRemoveSoil(SoilInfo toRemove) {
        
        this.toRemove = toRemove;
    }

    @Override
    public boolean validate(ILogger logger) {
        
        if (toRemove == null) {
            
            logger.error("Attempted to remove a null soil.");
            BotanyPots.LOGGER.error("A CraftTweaker script attempted to remove a null soil.");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void apply() {
        
        SoilReloadListener.registeredSoil.remove(this.toRemove.getId());
    }

    @Override
    public String describe() {

        BotanyPots.LOGGER.warn("A CraftTweaker script has removed the soil {}.", toRemove.getId());
        return "Removing soil info " + toRemove.getId();
    }
}
