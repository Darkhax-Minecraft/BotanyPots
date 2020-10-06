package net.darkhax.botanypots.data;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.fertilizer.FertilizerBuilder;
import net.darkhax.botanypots.soil.SoilBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator generator) {
        super(generator);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        SoilBuilder.create().setInput(Blocks.COARSE_DIRT).setVisibleBlock(Blocks.COARSE_DIRT)
                .addCategory("dirt", "coarse_dirt").setGrowthModifier(-0.65F).setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.CRIMSON_NYLIUM).setVisibleBlock(Blocks.CRIMSON_NYLIUM)
                .addCategory("dirt", "crimson_nylium", "nylium", "mushroom").setGrowthModifier(0.05F)
                .setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.DIRT).setVisibleBlock(Blocks.DIRT).addCategory("dirt")
                .setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.END_STONE).setVisibleBlock(Blocks.END_STONE).addCategory("end_stone")
                .setGrowthModifier(-0.5F).setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.FARMLAND).setVisibleBlock(Blocks.FARMLAND.getDefaultState().with(BlockStateProperties.MOISTURE_0_7, 7))
                .addCategory("dirt", "farmland").setGrowthModifier(0.15F).setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.GRASS_BLOCK).setVisibleBlock(Blocks.GRASS_BLOCK).addCategory("dirt", "grass")
                .setGrowthModifier(0.05F).setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.MYCELIUM).setVisibleBlock(Blocks.MYCELIUM).addCategory("dirt", "mushroom")
                .setGrowthModifier(0.05F).setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.NETHERRACK).setVisibleBlock(Blocks.NETHERRACK).addCategory("nether")
                .setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.PODZOL).setVisibleBlock(Blocks.PODZOL).setModID(BotanyPots.MOD_ID)
                .addCategory("dirt", "grass", "podzol", "mushroom").setGrowthModifier(0.05F).build(consumer);
        SoilBuilder.create().setInput(Blocks.RED_SAND).setVisibleBlock(Blocks.RED_SAND).addCategory("sand", "red_sand")
                .setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.SAND).setVisibleBlock(Blocks.SAND).addCategory("sand").setModID(BotanyPots.MOD_ID)
                .build(consumer);
        SoilBuilder.create().setInput(Blocks.SOUL_SAND).setVisibleBlock(Blocks.SOUL_SAND).addCategory("soul_sand", "nether")
                .setGrowthModifier(-0.3F).setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.SOUL_SOIL).setVisibleBlock(Blocks.SOUL_SOIL).addCategory("soul_sand", "nether")
                .setModID(BotanyPots.MOD_ID).build(consumer);
        SoilBuilder.create().setInput(Blocks.WARPED_NYLIUM).setVisibleBlock(Blocks.WARPED_NYLIUM)
                .addCategory("dirt", "warped_nylium", "nylium", "mushroom").setModID(BotanyPots.MOD_ID).build(consumer);

        FertilizerBuilder.create().setFertilizer(Items.BONE_MEAL).setMinTicks(120).setMaxTicks(170).setModID(BotanyPots.MOD_ID).build(consumer);
    }
}
