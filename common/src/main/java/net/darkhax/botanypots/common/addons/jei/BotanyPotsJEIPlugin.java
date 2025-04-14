package net.darkhax.botanypots.common.addons.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.darkhax.botanypots.common.addons.jei.crop.CropCategory;
import net.darkhax.botanypots.common.addons.jei.crop.CropInfo;
import net.darkhax.botanypots.common.addons.jei.interaction.InteractionCategory;
import net.darkhax.botanypots.common.addons.jei.interaction.InteractionInfo;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.interaction.PotInteraction;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BasicCrop;
import net.darkhax.botanypots.common.impl.data.recipe.interaction.BasicPotInteraction;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class BotanyPotsJEIPlugin implements IModPlugin {

    public static final ResourceLocation PLUGIN_ID = BotanyPotsMod.id("jei_plugin");
    public static final RecipeType<CropInfo> CROP = RecipeType.create(BotanyPotsMod.MOD_ID, "crop", CropInfo.class);
    public static final RecipeType<InteractionInfo> INTERACTION = RecipeType.create(BotanyPotsMod.MOD_ID, "interaction", InteractionInfo.class);

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        final IGuiHelper gui = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CropCategory(gui));
        registration.addRecipeCategories(new InteractionCategory(gui));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        final Level level = Minecraft.getInstance().level;
        if (level != null) {
            final List<CropInfo> basicCrops = new ArrayList<>();
            for (RecipeHolder<Crop> crop : Objects.requireNonNull(Crop.RECIPES.apply(level)).values()) {
                if (crop.value() instanceof BasicCrop basic) {
                    final BasicCrop.Properties properties = basic.getBasicProperties();
                    basicCrops.add(new CropInfo(crop.id(), properties.input(), properties.soil(), properties.growTime(), properties.drops()));
                }
            }
            registration.addRecipes(CROP, basicCrops);

            final List<InteractionInfo> interactions = new ArrayList<>();
            for (RecipeHolder<PotInteraction> interaction : Objects.requireNonNull(PotInteraction.RECIPES.apply(level)).values()) {
                if (interaction.value() instanceof BasicPotInteraction basic) {
                    interactions.add(new InteractionInfo(basic));
                }
            }
            registration.addRecipes(INTERACTION, interactions);
        }
    }

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }
}