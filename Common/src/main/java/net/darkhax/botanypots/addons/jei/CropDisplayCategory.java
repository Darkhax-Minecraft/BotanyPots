package net.darkhax.botanypots.addons.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.darkhax.bookshelf.api.Services;
import net.darkhax.botanypots.Constants;
import net.darkhax.botanypots.addons.jei.ui.CropDisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class CropDisplayCategory implements IRecipeCategory<CropDisplayInfo> {

    private final String type;
    private final IGuiHelper guiHelper;
    private final IDrawable icon;
    private final IDrawableStatic background;
    private final Component localizedName;

    public CropDisplayCategory(IGuiHelper gui, String type) {

        this.type = type;
        this.guiHelper = gui;
        this.icon = gui.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Services.REGISTRIES.items().get(new ResourceLocation(Constants.MOD_ID, "terracotta_botany_pot"))));
        this.background = gui.createBlankDrawable(155, 57);
        this.localizedName = new TranslatableComponent("gui.jei.category.botanypots." + type);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CropDisplayInfo recipe, IFocusGroup focuses) {

        recipe.setRecipe(builder, focuses);
    }

    @Override
    public void draw(CropDisplayInfo recipe, IRecipeSlotsView view, PoseStack stack, double mouseX, double mouseY) {

        recipe.draw(view, stack, mouseX, mouseY, this.guiHelper);
    }

    @Override
    public Component getTitle() {

        return this.localizedName;
    }

    @Override
    public IDrawable getBackground() {

        return this.background;
    }

    @Override
    public IDrawable getIcon() {

        return this.icon;
    }

    @Override
    public ResourceLocation getUid() {

        return new ResourceLocation(Constants.MOD_ID, this.type);
    }

    @Override
    public Class<? extends CropDisplayInfo> getRecipeClass() {

        return CropDisplayInfo.class;
    }
}
