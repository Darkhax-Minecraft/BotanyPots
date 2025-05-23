package net.darkhax.botanypots.common.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BasicCrop;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class InvalidCrops {

    private static final CachedSupplier<Block> potBlockType = CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("terracotta_hopper_botany_pot"));

    public static void build(LiteralArgumentBuilder<CommandSourceStack> parent) {
        final LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("check_crops");
        cmd.executes(InvalidCrops::execute);
        parent.then(cmd);
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final BlockPos targetPos = ctx.getSource().getEntityOrException().getOnPos();
        final Level level = ctx.getSource().getLevel();
        level.setBlock(targetPos, potBlockType.get().defaultBlockState(), 3);
        final BotanyPotBlockEntity potEntity = (BotanyPotBlockEntity) level.getBlockEntity(targetPos);
        if (potEntity == null) {
            throw new IllegalStateException("Test pot was null!");
        }
        final Set<Map.Entry<Item, RecipeHolder<Crop>>> crops = Objects.requireNonNull(Crop.CACHE.apply(ctx.getSource().getLevel())).getCachedValues().entries().stream().sorted(Comparator.comparing(s -> BuiltInRegistries.ITEM.getKey(s.getKey()).toString())).collect(Collectors.toCollection(LinkedHashSet::new));
        final List<ItemStack> toolList = buildToolList(ctx.getSource().registryAccess());
        int errorCount = 0;
        cropLoop:
        for (Map.Entry<Item, RecipeHolder<Crop>> cropEntry : crops) {
            final Crop crop = cropEntry.getValue().value();
            potEntity.clearContent();
            final ItemStack firstSoil = findFirstSoil(crop);
            if (firstSoil.isEmpty()) {
                BotanyPotsMod.LOG.error("Could not find a valid soil for crop '{}'!", cropEntry.getValue().id());
                errorCount++;
                continue;
            }
            potEntity.setItem(0, findFirstSoil(crop));
            potEntity.setItem(1, cropEntry.getKey().getDefaultInstance());
            for (ItemStack tool : toolList) {
                potEntity.setItem(2, tool.copy());
                final List<ItemStack> items = new ArrayList<>();
                for (int i = 0; i < 1000; i++) {
                    potEntity.updateGrowthTime(potEntity.getRequiredGrowthTicks());
                    crop.onHarvest(potEntity.getRecipeContext(), level, items::add);
                    if (!items.isEmpty()) {
                        continue cropLoop;
                    }
                }
            }
            errorCount++;
            BotanyPotsMod.LOG.error("Crop '{}' did not drop any items!", cropEntry.getValue().id());
        }
        if (errorCount > 0) {
            ctx.getSource().sendFailure(Component.translatable("commands.botanypots.debug.crop_errors", crops.size(), errorCount));
        }
        else {
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.botanypots.debug.crop_success", crops.size()), false);
        }
        return errorCount;
    }

    private static ItemStack findFirstSoil(Crop crop) {
        if (crop instanceof BasicCrop basic) {
            for (Item item : BuiltInRegistries.ITEM) {
                final ItemStack stack = item.getDefaultInstance();
                if (basic.getBasicProperties().soil().test(stack)) {
                    return stack;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private static List<ItemStack> buildToolList(RegistryAccess registries) {
        final List<ItemStack> tools = new ArrayList<>();
        tools.add(ItemStack.EMPTY);
        tools.add(withSilkTouch(Items.SHEARS, registries));
        tools.add(withSilkTouch(Items.NETHERITE_HOE, registries));
        tools.add(withSilkTouch(Items.NETHERITE_PICKAXE, registries));
        tools.add(withSilkTouch(Items.NETHERITE_AXE, registries));
        tools.add(withSilkTouch(Items.NETHERITE_SWORD, registries));
        tools.add(withSilkTouch(Items.NETHERITE_SHOVEL, registries));
        return tools;
    }

    private static ItemStack withSilkTouch(Item item, RegistryAccess registries) {
        final ItemStack stack = item.getDefaultInstance();
        stack.enchant(registries.registryOrThrow(Registries.ENCHANTMENT).getHolder(Enchantments.SILK_TOUCH).orElseThrow(), 1);
        return stack;
    }
}