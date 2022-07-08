package net.darkhax.botanypots.data.soil;

import net.darkhax.botanypots.tempshelf.DisplayState;
import net.darkhax.botanypots.tempshelf.SimpleDisplayState;
import net.darkhax.bookshelf.api.data.recipes.RecipeBaseData;
import net.darkhax.botanypots.BotanyPotHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class SoilInfo extends RecipeBaseData<Container> {

    /**
     * The item used to get the soil into the pot.
     */
    private Ingredient ingredient;

    /**
     * The blockstate used to render the soil.
     */
    private DisplayState renderState;

    /**
     * A modifier applied to the growth time of the crop.
     */
    private float growthModifier;

    /**
     * An array of associated soil categories.
     */
    private Set<String> categories;

    /**
     * The light level for the soil when placed in a botany pot.
     */
    private int lightLevel;

    public SoilInfo(ResourceLocation id, Ingredient ingredient, DisplayState renderState, float growthModifier, Set<String> categories, int lightLevel) {

        super(id);
        this.ingredient = ingredient;
        this.renderState = renderState;
        this.growthModifier = growthModifier;
        this.categories = categories;
        this.lightLevel = lightLevel;
    }

    public float getGrowthModifier() {

        return this.growthModifier;
    }

    public Ingredient getIngredient() {

        return this.ingredient;
    }

    public DisplayState getRenderState() {

        return this.renderState;
    }

    public Set<String> getCategories() {

        return this.categories;
    }

    public void setIngredient(Ingredient ingredient) {

        this.ingredient = ingredient;
    }

    public void setRenderState(DisplayState renderState) {

        this.renderState = renderState;
    }

    public void setGrowthModifier(float modifier) {

        this.growthModifier = modifier;
    }

    public void setCategories(Set<String> categories) {

        this.categories = categories;
    }

    public void setLightLevel(int lightLevel) {

        this.lightLevel = lightLevel;
    }

    public int getLightLevel() {

        return this.lightLevel;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {

        return BotanyPotHelper.SOIL_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {

        return BotanyPotHelper.SOIL_TYPE.get();
    }
}
