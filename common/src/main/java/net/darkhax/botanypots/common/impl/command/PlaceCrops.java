package net.darkhax.botanypots.common.impl.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.util.CommandHelper;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BasicCrop;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PlaceCrops {

    public static final List<Supplier<Block>> pots = List.of(
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("red_concrete_waxed_botany_pot")),
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("orange_concrete_waxed_botany_pot")),
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("yellow_concrete_waxed_botany_pot")),
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("lime_concrete_waxed_botany_pot")),
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("green_concrete_waxed_botany_pot")),
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("cyan_concrete_waxed_botany_pot")),
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("light_blue_concrete_waxed_botany_pot")),
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("blue_concrete_waxed_botany_pot")),
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("purple_concrete_waxed_botany_pot")),
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("magenta_concrete_waxed_botany_pot")),
            CachedSupplier.of(BuiltInRegistries.BLOCK, BotanyPotsMod.id("pink_concrete_waxed_botany_pot"))
    );

    public static void build(LiteralArgumentBuilder<CommandSourceStack> parent) {
        final LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("place_seeds");
        cmd.executes(PlaceCrops::execute);
        cmd.then(Commands.argument("pos", BlockPosArgument.blockPos()).executes(PlaceCrops::execute).then(Commands.argument("all_soils", BoolArgumentType.bool()).executes(PlaceCrops::execute)));
        parent.then(cmd);
    }

    private static BlockPos getPos(String name, CommandContext<CommandSourceStack> ctx, Supplier<BlockPos> fallback) {
        return CommandHelper.hasArgument(name, ctx, Coordinates.class) ? BlockPosArgument.getBlockPos(ctx, name) : fallback.get();
    }

    private static int execute(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final BlockPos.MutableBlockPos mutable = getPos("pos", ctx, () -> ctx.getSource().getPlayer().getOnPos()).mutable();
        final boolean allSoils = CommandHelper.getBooleanArg("all_soils", ctx);
        final Level level = ctx.getSource().getLevel();
        int count = 0;
        for (Map.Entry<Item, RecipeHolder<Crop>> crop : Objects.requireNonNull(Crop.CACHE.apply(ctx.getSource().getLevel())).getCachedValues().entries().stream().sorted(Comparator.comparing(s -> BuiltInRegistries.ITEM.getKey(s.getKey()).toString())).collect(Collectors.toCollection(LinkedHashSet::new))) {

            if (allSoils) {
                for (ItemStack soilStack : findAllSoils(crop.getValue().value())) {
                    level.setBlock(mutable, pots.get(count).get().defaultBlockState(), 3);
                    if (level.getBlockEntity(mutable) instanceof BotanyPotBlockEntity pot) {
                        pot.setItem(0, soilStack);
                        pot.setItem(1, crop.getKey().getDefaultInstance());
                        pot.updateGrowthTime(pot.getRequiredGrowthTicks());
                    }
                    count++;
                    mutable.move(Direction.NORTH);

                    if (count == pots.size()) {
                        count = 0;
                        mutable.move(Direction.SOUTH, pots.size());
                        mutable.move(Direction.EAST);
                    }
                }
            }
            else {
                level.setBlock(mutable, pots.get(count).get().defaultBlockState(), 3);
                if (level.getBlockEntity(mutable) instanceof BotanyPotBlockEntity pot) {
                    pot.setItem(0, findFirstSoil(crop.getValue().value()));
                    pot.setItem(1, crop.getKey().getDefaultInstance());
                    pot.updateGrowthTime(pot.getRequiredGrowthTicks());
                }
                count++;
                mutable.move(Direction.NORTH);

                if (count == pots.size()) {
                    count = 0;
                    mutable.move(Direction.SOUTH, pots.size());
                    mutable.move(Direction.EAST);
                }
            }
        }
        return 0;
    }

    private static List<ItemStack> findAllSoils(Crop crop) {
        final List<ItemStack> items = new ArrayList<>();
        if (crop instanceof BasicCrop basic) {
            for (Item item : BuiltInRegistries.ITEM) {
                final ItemStack stack = item.getDefaultInstance();
                if (basic.getBasicProperties().soil().test(stack)) {
                    items.add(stack);
                }
            }
        }
        return items;
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
}