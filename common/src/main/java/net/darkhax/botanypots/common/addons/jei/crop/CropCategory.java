package net.darkhax.botanypots.common.addons.jei.crop;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.darkhax.botanypots.common.addons.jei.BotanyPotsJEIPlugin;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.BotanyPotsContent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CropCategory implements IRecipeCategory<CropInfo> {

    private final Component title;
    private final IDrawable icon;
    private final IDrawableStatic background;
    private final IDrawableStatic blankSlot;

    public CropCategory(IGuiHelper guiHelper) {
        this.title = Component.translatable("gui.jei.category.botanypots.crop");
        this.icon = guiHelper.createDrawableItemStack(BotanyPotsContent.TAB_ICON.get());
        this.background = guiHelper.createBlankDrawable(155, 57);
        this.blankSlot = guiHelper.getSlotDrawable();
    }

    @NotNull
    @Override
    public RecipeType<CropInfo> getRecipeType() {
        return BotanyPotsJEIPlugin.CROP;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return this.title;
    }

    @NotNull
    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Nullable
    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void onDisplayedIngredientsUpdate(@NotNull CropInfo recipe, @NotNull List<IRecipeSlotDrawable> recipeSlots, @NotNull IFocusGroup focuses) {
        if (recipeSlots.size() >= 2) {
            recipe.inventory().clear();
            recipe.inventory().add(recipeSlots.get(1).getDisplayedItemStack().orElse(ItemStack.EMPTY));
            recipe.inventory().add(recipeSlots.get(0).getDisplayedItemStack().orElse(ItemStack.EMPTY));
            recipe.inventory().add(ItemStack.EMPTY);
        }
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull CropInfo recipe, @NotNull IFocusGroup focuses) {
        // Seed Slot
        final IRecipeSlotBuilder seedSlot = builder.addSlot(RecipeIngredientRole.INPUT, 31, 11);
        seedSlot.addIngredients(recipe.seed());
        seedSlot.addRichTooltipCallback((view, tooltip) -> {
            final RecipeHolder<Crop> crop = Crop.getCrop(Minecraft.getInstance().level, recipe.context(), recipe.context().getSeedItem());
            if (crop != null) {
                crop.value().hoverTooltip(recipe.context().getSeedItem(), recipe.context(), Minecraft.getInstance().level, tooltip::add);
                if (Minecraft.getInstance().options.advancedItemTooltips) {
                    tooltip.add(Component.translatable("tooltip.botanypots.crop_id", recipe.id().toString()).withStyle(ChatFormatting.DARK_GRAY));
                }
            }
        });

        // Soil Slot
        final IRecipeSlotBuilder soilSlot = builder.addSlot(RecipeIngredientRole.INPUT, 31, 29);
        soilSlot.addIngredients(recipe.soil());
        soilSlot.addRichTooltipCallback((view, tooltip) -> {
            if (Minecraft.getInstance().level != null) {
                view.getDisplayedItemStack().ifPresent(stack -> {
                    final RecipeHolder<Soil> soil = Soil.getSoil(Minecraft.getInstance().level, recipe.context(), recipe.context().getSoilItem());
                    if (soil != null) {
                        soil.value().hoverTooltip(stack, recipe.context(), Minecraft.getInstance().level, tooltip::add);
                        if (Minecraft.getInstance().options.advancedItemTooltips) {
                            tooltip.add(Component.translatable("tooltip.botanypots.soil_id", soil.id().toString()).withStyle(ChatFormatting.DARK_GRAY));
                        }
                    }
                });
            }
        });

        // drops
        final IRecipeSlotBuilder[] dropSlots = new IRecipeSlotBuilder[12];
        for (int nextSlotId = 0; nextSlotId < dropSlots.length; nextSlotId++) {
            dropSlots[nextSlotId] = builder.addSlot(RecipeIngredientRole.OUTPUT, 81 + 18 * (nextSlotId % 4), 1 + 18 * (nextSlotId / 4));
        }
        int nextOutputSlot = 0;
        for (ItemDropProvider dropProvider : recipe.drops()) {
            for (ItemStack stack : dropProvider.getDisplayItems()) {
                if (!stack.isEmpty()) {
                    dropSlots[nextOutputSlot].addItemStack(stack);
                    nextOutputSlot++;
                    if (nextOutputSlot >= dropSlots.length) {
                        nextOutputSlot = 0;
                    }
                }
            }
        }
    }

    @Override
    public void draw(@NotNull CropInfo recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        this.blankSlot.draw(graphics, 30, 10); // Seed
        this.blankSlot.draw(graphics, 30, 28); // Soil
        // Drops
        for (int nextSlotId = 0; nextSlotId < 12; nextSlotId++) {
            this.blankSlot.draw(graphics, 80 + 18 * (nextSlotId % 4), 18 * (nextSlotId / 4));
        }
    }
}