package net.darkhax.botanypots.data;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.crop.CropBuilder;
import net.darkhax.botanypots.crop.HarvestEntry;
import net.darkhax.botanypots.fertilizer.FertilizerBuilder;
import net.darkhax.botanypots.soil.SoilBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

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

        buildFlowerRecipe(Blocks.ALLIUM, consumer);
        buildFlowerRecipe(Blocks.AZURE_BLUET, consumer);
        buildFlowerRecipe(Blocks.CORNFLOWER, consumer);
        buildFlowerRecipe(Blocks.DANDELION, consumer);
        buildFlowerRecipe(Blocks.ORANGE_TULIP, consumer);
        buildFlowerRecipe(Blocks.OXEYE_DAISY, consumer);
        buildFlowerRecipe(Blocks.PINK_TULIP, consumer);
        buildFlowerRecipe(Blocks.POPPY, consumer);
        buildFlowerRecipe(Blocks.RED_TULIP, consumer);
        buildFlowerRecipe(Blocks.WHITE_TULIP, consumer);
        buildDoubleFlowerRecipe(Blocks.LILAC, consumer);
        buildDoubleFlowerRecipe(Blocks.PEONY, consumer);
        buildDoubleFlowerRecipe(Blocks.ROSE_BUSH, consumer);
        buildDoubleFlowerRecipe(Blocks.SUNFLOWER, consumer);
        buildMushroomRecipe(Blocks.BROWN_MUSHROOM, consumer);
        buildMushroomRecipe(Blocks.RED_MUSHROOM, consumer);
        buildFungusRecipe(Blocks.CRIMSON_FUNGUS, "crimson_nylium", Blocks.CRIMSON_STEM, consumer);
        buildFungusRecipe(Blocks.WARPED_FUNGUS, "warped_nylium", Blocks.WARPED_STEM, consumer);

        CropBuilder.create().setSeed(Items.BAMBOO)
                .addVisibleBlocks(Blocks.BAMBOO)
                .addCategories("dirt", "sand")
                .setGrowthTicks(1800)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Blocks.BAMBOO), 1, 3),
                        new HarvestEntry(0.05F, new ItemStack(Blocks.BAMBOO), 1, 3))
                .build(consumer, loc(Items.BAMBOO));
        CropBuilder.create().setSeed(Tags.Items.SEEDS_BEETROOT)
                .addVisibleBlocks(Blocks.BEETROOTS.getDefaultState().with(BlockStateProperties.AGE_0_3, 3))
                .addCategories("dirt")
                .setGrowthTicks(1500)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Blocks.BEETROOTS), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(Blocks.BEETROOTS), 1, 2),
                        new HarvestEntry(0.05F, new ItemStack(Items.BEETROOT_SEEDS), 1, 2))
                .build(consumer, loc(Items.BEETROOT_SEEDS));
        CropBuilder.create().setSeed(Items.CACTUS)
                .addVisibleBlocks(Blocks.CACTUS)
                .addCategories("sand")
                .setGrowthTicks(1600)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Blocks.CACTUS), 1, 1))
                .build(consumer, loc(Items.CACTUS));
        CropBuilder.create().setSeed(Items.CARROT)
                .addVisibleBlocks(Blocks.CARROTS.getDefaultState().with(BlockStateProperties.AGE_0_7, 7))
                .addCategories("dirt")
                .setGrowthTicks(1100)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Items.CARROT), 1, 2),
                        new HarvestEntry(0.05F, new ItemStack(Items.CARROT), 1, 2))
                .build(consumer, loc(Items.CARROT));
        CropBuilder.create().setSeed(Items.CHORUS_FLOWER)
                .addVisibleBlocks(Blocks.CHORUS_FLOWER.getDefaultState().with(BlockStateProperties.AGE_0_5, 0))
                .addCategories("end_stone")
                .setGrowthTicks(2400)
                .addResults(new HarvestEntry(1.0F, new ItemStack(Items.CHORUS_FRUIT), 1, 12),
                        new HarvestEntry(0.05F, new ItemStack(Blocks.CHORUS_FLOWER), 1, 1))
                .build(consumer, loc(Items.CHORUS_FLOWER));
        CropBuilder.create().setSeed(Items.CRIMSON_ROOTS)
                .addVisibleBlocks(Blocks.CRIMSON_ROOTS)
                .addCategories("crimson_nylium")
                .setGrowthTicks(600)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Blocks.CRIMSON_ROOTS), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(Items.CRIMSON_ROOTS), 1, 2))
                .build(consumer, loc(Items.CRIMSON_ROOTS));
        CropBuilder.create().setSeed(Items.FERN)
                .addVisibleBlocks(Blocks.FERN)
                .addCategories("grass")
                .setGrowthTicks(600)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Blocks.FERN), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(Blocks.FERN), 1, 2),
                        new HarvestEntry(0.05F, new ItemStack(Blocks.LARGE_FERN), 1, 1))
                .build(consumer, loc(Items.FERN));
        CropBuilder.create().setSeed(Items.LILY_OF_THE_VALLEY)
                .addVisibleBlocks(Blocks.LILY_OF_THE_VALLEY)
                .addCategories("dirt")
                .setGrowthTicks(1400)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Items.LILY_OF_THE_VALLEY), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(Items.LILY_OF_THE_VALLEY), 1, 2))
                .build(consumer, loc(Items.LILY_OF_THE_VALLEY));
        CropBuilder.create().setSeed(Tags.Items.SEEDS_MELON)
                .addVisibleBlocks(Blocks.MELON)
                .addCategories("dirt")
                .setGrowthTicks(1800)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Blocks.MELON), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(Items.MELON_SEEDS), 1, 2))
                .build(consumer, loc(Items.MELON_SEEDS));
        CropBuilder.create().setSeed(Items.NETHER_WART)
                .addVisibleBlocks(Blocks.NETHER_WART.getDefaultState().with(BlockStateProperties.AGE_0_3, 3))
                .addCategories("soul_sand")
                .setGrowthTicks(1600)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Items.NETHER_WART), 1, 3),
                        new HarvestEntry(0.05F, new ItemStack(Items.NETHER_WART), 1, 3))
                .build(consumer, loc(Items.NETHER_WART));
        CropBuilder.create().setSeed(Items.POTATO)
                .addVisibleBlocks(Blocks.POTATOES.getDefaultState().with(BlockStateProperties.AGE_0_7, 7))
                .addCategories("dirt")
                .setGrowthTicks(900)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Items.POTATO), 1, 2),
                        new HarvestEntry(0.05F, new ItemStack(Items.POISONOUS_POTATO), 1, 2))
                .build(consumer, loc(Items.POTATO));
        CropBuilder.create().setSeed(Tags.Items.SEEDS_PUMPKIN)
                .addVisibleBlocks(Blocks.PUMPKIN)
                .addCategories("dirt")
                .setGrowthTicks(1800)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Blocks.PUMPKIN), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(Items.PUMPKIN_SEEDS), 1, 2))
                .build(consumer, loc(Items.PUMPKIN_SEEDS));
        CropBuilder.create().setSeed(Items.SUGAR_CANE)
                .addVisibleBlocks(Blocks.SUGAR_CANE)
                .addCategories("sand")
                .setGrowthTicks(800)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Items.SUGAR_CANE), 1, 1))
                .build(consumer, loc(Items.SUGAR_CANE));
        CropBuilder.create().setSeed(Items.SWEET_BERRIES)
                .addVisibleBlocks(Blocks.SWEET_BERRY_BUSH.getDefaultState().with(BlockStateProperties.AGE_0_3, 3))
                .addCategories("dirt")
                .setGrowthTicks(800)
                .addResults(new HarvestEntry(0.9F, new ItemStack(Items.SWEET_BERRIES), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(Items.SWEET_BERRIES), 1, 2))
                .build(consumer, loc(Items.SWEET_BERRIES));
        CropBuilder.create().setSeed(Items.TWISTING_VINES)
                .addVisibleBlocks(Blocks.TWISTING_VINES_PLANT.getDefaultState(),
                        Blocks.TWISTING_VINES.getDefaultState().with(BlockStateProperties.AGE_0_25, 0))
                .addCategories("warped_nylium")
                .setGrowthTicks(1600)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Items.TWISTING_VINES), 1, 1))
                .build(consumer, loc(Items.TWISTING_VINES));
        CropBuilder.create().setSeed(Items.WARPED_ROOTS)
                .addVisibleBlocks(Blocks.WARPED_ROOTS)
                .addCategories("warped_nylium")
                .setGrowthTicks(600)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Items.WARPED_ROOTS), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(Items.WARPED_ROOTS), 1, 2))
                .build(consumer, loc(Items.WARPED_ROOTS));
        CropBuilder.create().setSeed(Items.WEEPING_VINES)
                .addVisibleBlocks(Blocks.WEEPING_VINES_PLANT, Blocks.WEEPING_VINES_PLANT)
                .addCategories("crimson_nylium")
                .setGrowthTicks(1600)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Items.WEEPING_VINES), 1, 1))
                .build(consumer, loc(Items.WEEPING_VINES));
        CropBuilder.create().setSeed(Tags.Items.SEEDS_WHEAT)
                .addVisibleBlocks(Blocks.WHEAT.getDefaultState().with(BlockStateProperties.AGE_0_7, 7))
                .addCategories("dirt")
                .setGrowthTicks(1000)
                .addResults(new HarvestEntry(0.75F, new ItemStack(Items.WHEAT), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(Items.WHEAT), 1, 2),
                        new HarvestEntry(0.05F, new ItemStack(Items.WHEAT_SEEDS), 1, 2))
                .build(consumer, loc(Items.WHEAT_SEEDS));
        CropBuilder.create().setSeed(Items.WITHER_ROSE)
                .addVisibleBlocks(Blocks.WITHER_ROSE)
                .addCategories("soul_sand")
                .setGrowthTicks(1800)
                .addResults(new HarvestEntry(0.6F, new ItemStack(Items.WITHER_ROSE), 1, 1))
                .build(consumer, loc(Items.WITHER_ROSE));
    }

    private static ResourceLocation loc(IItemProvider item) {
        return new ResourceLocation(BotanyPots.MOD_ID, item.asItem().getRegistryName().getPath());
    }

    private static void buildFlowerRecipe(CropBuilder builder, Block flower, Consumer<IFinishedRecipe> consumer) {
        builder.setSeed(flower)
                .addCategories("dirt")
                .setGrowthTicks(1200)
                .addResults(new HarvestEntry(0.75F, new ItemStack(flower), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(flower), 1, 2))
                .build(consumer, loc(flower));
    }

    private static void buildFlowerRecipe(Block flower, Consumer<IFinishedRecipe> consumer) {
        buildFlowerRecipe(CropBuilder.create().addVisibleBlocks(flower), flower, consumer);
    }

    private static void buildDoubleFlowerRecipe(Block flower, Consumer<IFinishedRecipe> consumer) {
        CropBuilder builder = CropBuilder.create()
                .addVisibleBlocks(flower.getDefaultState().with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER),
                        flower.getDefaultState().with(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER));
        buildFlowerRecipe(builder, flower, consumer);
    }

    private static void buildMushroomRecipe(Block mushroom, Consumer<IFinishedRecipe> consumer) {
        CropBuilder.create().setSeed(mushroom)
                .addVisibleBlocks(mushroom)
                .addCategories("mushroom")
                .setGrowthTicks(1600)
                .addResults(new HarvestEntry(0.75F, new ItemStack(mushroom), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(mushroom), 1, 2))
                .build(consumer, loc(mushroom));
    }

    private static void buildFungusRecipe(Block fungus, String category, Block extra, Consumer<IFinishedRecipe> consumer) {
        CropBuilder.create().setSeed(fungus)
                .addVisibleBlocks(fungus)
                .addCategories(category)
                .setGrowthTicks(1600)
                .addResults(new HarvestEntry(0.75F, new ItemStack(fungus), 1, 1),
                        new HarvestEntry(0.15F, new ItemStack(Blocks.NETHER_WART_BLOCK), 1, 1),
                        new HarvestEntry(0.15F, new ItemStack(extra), 1, 1),
                        new HarvestEntry(0.05F, new ItemStack(Blocks.SHROOMLIGHT), 1, 1))
                .build(consumer, loc(fungus));
    }
}
