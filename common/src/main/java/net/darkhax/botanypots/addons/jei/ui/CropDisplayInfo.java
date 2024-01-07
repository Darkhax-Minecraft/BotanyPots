package net.darkhax.botanypots.addons.jei.ui;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public abstract class CropDisplayInfo {

    public abstract void setRecipe (IRecipeLayoutBuilder builder, IFocusGroup focuses);

    public abstract void draw(IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY, IGuiHelper guiHelper);

    public abstract ResourceLocation getCropId();
}