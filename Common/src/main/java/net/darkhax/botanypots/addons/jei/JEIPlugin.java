package net.darkhax.botanypots.addons.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPotsCommon;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.addons.jei.ui.BasicCropDisplayInfo;
import net.darkhax.botanypots.addons.jei.ui.CropDisplayInfo;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.BasicCrop;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.data.recipes.fertilizer.Fertilizer;
import net.darkhax.botanypots.data.recipes.potinteraction.PotInteraction;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID = new ResourceLocation(Constants.MOD_ID, "jei_support");

    public static final RecipeType<CropDisplayInfo> SOIL = RecipeType.create(Constants.MOD_ID, "soil", CropDisplayInfo.class);
    public static final RecipeType<CropDisplayInfo> CROP = RecipeType.create(Constants.MOD_ID, "crop", CropDisplayInfo.class);
    public static final RecipeType<PotInteraction> POT_INTERACTION = RecipeType.create(Constants.MOD_ID, "pot_interaction", PotInteraction.class);
    public static final RecipeType<Fertilizer> FERTILIZER = RecipeType.create(Constants.MOD_ID, "fertilizer", Fertilizer.class);

    @Override
    public ResourceLocation getPluginUid() {

        return PLUGIN_ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {

        final IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CropDisplayCategory(gui, SOIL));
        registration.addRecipeCategories(new CropDisplayCategory(gui, CROP));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        for (Item potItem : BotanyPotsCommon.content.items) {
            if (potItem instanceof BlockItem blockItem && blockItem.getBlock() instanceof BlockBotanyPot pot) {
                registration.addRecipeCatalyst(potItem.getDefaultInstance(), CROP);
            }
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        final RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        final List<Soil> soils = BotanyPotHelper.getAllRecipes(recipeManager, BotanyPotHelper.SOIL_TYPE.get());
        final List<Crop> crops = BotanyPotHelper.getAllRecipes(recipeManager, BotanyPotHelper.CROP_TYPE.get());

        crops.forEach(crop -> {

            if (crop instanceof BasicCrop basic) {
                registration.addRecipes(CROP, BasicCropDisplayInfo.getCropRecipes(basic, soils));
            }
        });
    }
}