package net.darkhax.botanypots.addons.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.BotanyPotsCommon;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.addons.rei.ui.BasicCropDisplay;
import net.darkhax.botanypots.addons.rei.ui.CropDisplay;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;

public class REIPlugin implements REIClientPlugin {
    public static final CategoryIdentifier<CropDisplay> SOIL = CategoryIdentifier.of(Constants.MOD_ID, "soil");
    public static final CategoryIdentifier<CropDisplay> CROP = CategoryIdentifier.of(Constants.MOD_ID, "crop");

    @Override
    public void registerCategories(CategoryRegistry registry) {

        registry.add(new CropDisplayCategory(SOIL));
        registry.add(new CropDisplayCategory(CROP));

        EntryIngredient.Builder builder = EntryIngredient.builder();

        for (Item potItem : BotanyPotsCommon.content.items) {
            if (potItem instanceof BlockItem blockItem && blockItem.getBlock() instanceof BlockBotanyPot pot) {
                builder.add(EntryStacks.of(potItem));
            }
        }

        registry.addWorkstations(CROP, builder.build());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {

        final RecipeManager recipeManager = registry.getRecipeManager();
        final List<RecipeHolder<Soil>> soils = BotanyPotHelper.getAllRecipes(recipeManager, BotanyPotHelper.SOIL_TYPE.get());
        final List<RecipeHolder<Crop>> crops = BotanyPotHelper.getAllRecipes(recipeManager, BotanyPotHelper.CROP_TYPE.get());

        crops.forEach(crop -> {

            final List<CropDisplay> displays = BasicCropDisplay.getCropRecipes(crop, soils);

            for (CropDisplay display : displays) {

                registry.add(display, crop);
            }
        });
    }
}
