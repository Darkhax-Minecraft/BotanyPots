package net.darkhax.botanypots.addons.jei.ui;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;

public abstract class CropDisplayInfo {

    public abstract void setRecipe (IRecipeLayoutBuilder builder, IFocusGroup focuses);

    public abstract void draw(IRecipeSlotsView view, PoseStack stack, double mouseX, double mouseY, IGuiHelper guiHelper);
}