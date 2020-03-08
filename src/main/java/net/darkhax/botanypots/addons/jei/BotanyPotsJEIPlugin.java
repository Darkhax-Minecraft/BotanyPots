package net.darkhax.botanypots.addons.jei;

import java.util.Collection;
import java.util.stream.Collectors;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.CropInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class BotanyPotsJEIPlugin implements IModPlugin {
    
    @Override
    public ResourceLocation getPluginUid() {
        
        return new ResourceLocation(BotanyPots.MOD_ID, "jei");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        
        registration.addRecipeCatalyst(new ItemStack(BotanyPots.instance.getContent().getBasicBotanyPot()), CategoryCrop.ID);
        registration.addRecipeCatalyst(new ItemStack(BotanyPots.instance.getContent().getHopperBotanyPot()), CategoryCrop.ID);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        Collection<CropInfo> crops = BotanyPotHelper.getCropData(BotanyPots.instance.getActiveRecipeManager()).values();
        registration.addRecipes(crops.stream().map(CropWrapper::new).collect(Collectors.toList()), CategoryCrop.ID);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        
        registration.addRecipeCategories(new CategoryCrop(registration.getJeiHelpers().getGuiHelper()));
    }
}
