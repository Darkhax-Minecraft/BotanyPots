package net.darkhax.botanypots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public abstract class RecipeData implements IRecipe<IInventory> {

    @Override
    public boolean matches (IInventory inv, World worldIn) {
        
        // Not used
        return false;
    }

    @Override
    public ItemStack getCraftingResult (IInventory inv) {
        
        // Not used
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canFit (int width, int height) {
        
        // Not used
        return false;
    }

    @Override
    public ItemStack getRecipeOutput () {
        
        // Not used
        return ItemStack.EMPTY;
    }
}