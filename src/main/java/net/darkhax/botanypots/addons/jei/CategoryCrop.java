package net.darkhax.botanypots.addons.jei;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.HarvestEntry;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CategoryCrop implements IRecipeCategory<CropWrapper> {
    
    public static final ResourceLocation ID = new ResourceLocation(BotanyPots.MOD_ID, "soil");
    
    private final IDrawable icon;
    private final IDrawableStatic background;
    private final IDrawableStatic slotDrawable;
    
    public CategoryCrop(IGuiHelper guiHelper) {
        
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(BotanyPots.instance.getContent().getBasicBotanyPot()));
        this.background = guiHelper.createBlankDrawable(155, 57);
        this.slotDrawable = guiHelper.getSlotDrawable();
    }
    
    @Override
    public ResourceLocation getUid () {
        
        return ID;
    }
    
    @Override
    public Class<? extends CropWrapper> getRecipeClass () {
        
        return CropWrapper.class;
    }
    
    @Override
    public String getTitle () {
        
        return I18n.format("botanypots.title");
    }
    
    @Override
    public IDrawable getBackground () {
        
        return this.background;
    }
    
    @Override
    public IDrawable getIcon () {
        
        return this.icon;
    }
    
    @Override
    public void setIngredients (CropWrapper recipe, IIngredients ingredients) {
        
        recipe.setIngredients(ingredients);
    }
    
    @Override
    public void draw (CropWrapper recipe, double mouseX, double mouseY) {
        
        // Seed & Soil
        this.slotDrawable.draw(0, 19 * 0);
        this.slotDrawable.draw(0, 19 * 1);
        
        for (int nextSlotId = 2; nextSlotId < 14; nextSlotId++) {
            
            final int relativeSlotId = nextSlotId - 2;
            this.slotDrawable.draw(80 + 19 * (relativeSlotId % 4), 19 * (relativeSlotId / 4));
        }
    }
    
    @Override
    public void setRecipe (IRecipeLayout recipeLayout, CropWrapper recipe, IIngredients ingredients) {
        
        final IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
        
        // Seed Input
        stacks.init(0, true, 0, 19 * 0);
        stacks.set(0, recipe.getSeedItems());
        
        // Soil Inputs
        stacks.init(1, true, 0, 19 * 1);
        stacks.set(1, recipe.getSoilItems());
        
        int nextSlotId = 2;
        
        for (final HarvestEntry entry : recipe.getDrops()) {
            
            final int relativeSlotId = nextSlotId - 2;
            stacks.init(nextSlotId, false, 80 + 19 * (relativeSlotId % 4), 19 * (relativeSlotId / 4));
            stacks.set(nextSlotId, entry.getItem());
            nextSlotId++;
        }
    }
}