package net.darkhax.botanypots.common.impl.command.generator;

import com.google.gson.JsonObject;
import net.darkhax.botanypots.common.api.command.generator.DataHelper;
import net.darkhax.botanypots.common.api.command.generator.crop.CropGenerator;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SporeBlossomBlock;

/**
 * This crop generator is used as a fallback when none of the other generators have claimed an item. It will generate a
 * block derived crop for a wide range of blocks in the game such as flowers, seeds, crop blocks, bonemealable blocks,
 * and corals.
 */
public class MissingCropGenerator implements CropGenerator {

    private static final TagKey<Item> FLOWERS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "flowers"));
    private static final TagKey<Item> FORGE_SEEDS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", "seeds"));
    private static final TagKey<Item> COMMON_SEEDS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "seeds"));
    private static final TagKey<Item> IGNORED_ITEMS = TagKey.create(Registries.ITEM, BotanyPotsMod.id("crop_generator_ignores"));

    @Override
    public boolean canGenerateCrop(ServerLevel level, ItemStack stack) {
        if (stack.is(IGNORED_ITEMS)) {
            return false;
        }
        if (stack.getItem() instanceof BlockItem blockItem) {
            if (stack.is(FORGE_SEEDS) || stack.is(COMMON_SEEDS) || stack.is(FLOWERS)) {
                return true;
            }
            final Block placedBlock = blockItem.getBlock();
            final ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(placedBlock);
            if (placedBlock instanceof CropBlock || placedBlock instanceof GrowingPlantBlock || placedBlock instanceof BonemealableBlock || placedBlock instanceof SaplingBlock || placedBlock instanceof BushBlock || placedBlock instanceof SporeBlossomBlock || (placedBlock instanceof BaseCoralPlantTypeBlock && !blockId.getPath().startsWith("dead_"))) {
                return true;
            }

        }
        return false;
    }

    @Override
    public JsonObject generateData(ServerLevel level, ItemStack stack) {
        final JsonObject output = new JsonObject();
        final Block block = ((BlockItem) stack.getItem()).getBlock();
        output.add("bookshelf:load_conditions", DataHelper.array(DataHelper.requiresBlock(block)));
        output.addProperty("type", "botanypots:block_derived_crop");
        output.addProperty("block", BuiltInRegistries.BLOCK.getKey(block).toString());
        if (block instanceof BaseCoralPlantTypeBlock) {
            output.add("soil", DataHelper.tag(BotanyPotsMod.id("soil/water")));
        }
        if (block instanceof MushroomBlock) {
            final JsonObject blockTag = DataHelper.blockTag(ResourceLocation.withDefaultNamespace("mushroom_grow_block"));
            final JsonObject itemTag = DataHelper.tag(BotanyPotsMod.id("soil/mushroom"));
            output.add("soil", DataHelper.ingredients(blockTag, itemTag));
        }
        return output;
    }
}
