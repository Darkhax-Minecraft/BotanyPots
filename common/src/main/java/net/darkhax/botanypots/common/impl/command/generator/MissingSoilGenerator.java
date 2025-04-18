package net.darkhax.botanypots.common.impl.command.generator;

import com.google.gson.JsonObject;
import net.darkhax.botanypots.common.api.command.generator.DataHelper;
import net.darkhax.botanypots.common.api.command.generator.soil.SoilGenerator;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.interaction.PotInteraction;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BasicCrop;
import net.darkhax.botanypots.common.impl.data.recipe.interaction.BasicPotInteraction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

/**
 * This soil generator is used as a fallback when none of the other generators have claimed an item. It looks for items
 * that are accepted by existing crops, or items that are part of a pot interaction recipe.
 */
public class MissingSoilGenerator implements SoilGenerator {

    private static final TagKey<Item> IGNORED_ITEMS = TagKey.create(Registries.ITEM, BotanyPotsMod.id("soil_generator_ignores"));

    @Override
    public boolean canGenerateSoil(ServerLevel level, ItemStack stack) {
        if (!stack.is(IGNORED_ITEMS) && stack.getItem() instanceof BlockItem) {
            for (RecipeHolder<Crop> crop : Objects.requireNonNull(Crop.RECIPES.apply(level)).values()) {
                if (crop.value() instanceof BasicCrop basic && basic.isValidSoil(stack)) {
                    return true;
                }
            }
            for (RecipeHolder<PotInteraction> interaction : Objects.requireNonNull(PotInteraction.RECIPES.apply(level)).values()) {
                if (interaction.value() instanceof BasicPotInteraction basic) {
                    final Ingredient soilTest = basic.properties.soilTest().orElse(null);
                    if (soilTest != null && !soilTest.isEmpty() && soilTest.test(stack)) {
                        return true;
                    }
                    final ItemStack output = basic.properties.newSoil().orElse(ItemStack.EMPTY);
                    if (!output.isEmpty() && ItemStack.isSameItemSameComponents(stack, output)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public JsonObject generateData(ServerLevel level, ItemStack stack) {
        final Block block = ((BlockItem) stack.getItem()).getBlock();
        final JsonObject output = new JsonObject();
        output.add("bookshelf:load_conditions", DataHelper.array(DataHelper.requiresBlock(block)));
        output.addProperty("type", "botanypots:block_derived_soil");
        output.addProperty("block", DataHelper.BLOCKS.getKey(block).toString());
        return output;
    }
}