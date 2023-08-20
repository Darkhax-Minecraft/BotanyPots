package net.darkhax.botanypots.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.darkhax.botanypots.data.recipes.soil.Soil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class CommandDump {

    public static void build(LiteralArgumentBuilder<CommandSourceStack> parent) {

        final LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("dump");
        cmd.then(Commands.literal("missing_crops").executes(MissingCrops::dump));
        parent.then(cmd);
    }

    private static class MissingCrops {

        private static final TagKey<Item> FORGE_SEEDS = TagKey.create(Registries.ITEM, new ResourceLocation("forge", "seeds"));
        private static final TagKey<Item> COMMON_SEEDS = TagKey.create(Registries.ITEM, new ResourceLocation("c", "seeds"));

        private static int dump(CommandContext<CommandSourceStack> ctx) {

            final Set<Item> missingSeedItems = new HashSet<>();
            final RecipeManager manager = ctx.getSource().getServer().getRecipeManager();

            // Look through item registry
            for (Item item : BuiltInRegistries.ITEM) {

                // Skip any item that can already be planted in the pot.
                if (isCrop(item.getDefaultInstance(), manager) || isSoil(item.getDefaultInstance(), manager)) {

                    continue;
                }

                // Find items that place a block
                if (item instanceof BlockItem itemBlock) {

                    final Block placedBlock = itemBlock.getBlock();

                    // Check based on block class
                    if (placedBlock instanceof CropBlock || placedBlock instanceof GrowingPlantBlock || placedBlock instanceof BonemealableBlock || placedBlock instanceof SaplingBlock || placedBlock instanceof BushBlock) {

                        missingSeedItems.add(item);
                    }

                    // Check based on blockstate properties
                    else {

                        for (Property<?> property : placedBlock.getStateDefinition().getProperties()) {

                            if (property.getName().equalsIgnoreCase("age")) {
                                missingSeedItems.add(item);
                                break;
                            }
                        }
                    }
                }
            }

            // Search through the seeds tag(s)
            final Consumer<Item> tagProcessor = item -> {

                if (!isCrop(item.getDefaultInstance(), manager)) {
                    missingSeedItems.add(item);
                }
            };

            processTag(FORGE_SEEDS, tagProcessor);
            processTag(COMMON_SEEDS, tagProcessor);

            if (missingSeedItems.isEmpty()) {

                ctx.getSource().sendSuccess(() -> BotanyPotsCommands.modMessage(Component.translatable("commands.botanypots.dump.no_results")), false);
            }

            else {

                final StringJoiner entries = new StringJoiner(System.lineSeparator());
                entries.add("Potential missing crop IDs");
                missingSeedItems.stream().sorted(MissingCrops::compareById).forEach(e -> entries.add(BuiltInRegistries.ITEM.getKey(e).toString()));
                ctx.getSource().sendSuccess(() -> BotanyPotsCommands.modMessage(Component.translatable("commands.botanypots.dump.missing_crops", Component.literal(Integer.toString(missingSeedItems.size())).withStyle(style -> style.withColor(ChatFormatting.RED))).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entries.toString())))), false);
            }

            return 0;
        }

        private static void processTag(TagKey<Item> key, Consumer<Item> consumer) {

            BuiltInRegistries.ITEM.getTag(key).ifPresent(named -> named.forEach(entry -> consumer.accept(entry.value())));
        }

        private static boolean isCrop(ItemStack stack, RecipeManager recipes) {

            for (final Crop crop : recipes.getAllRecipesFor(BotanyPotHelper.CROP_TYPE.get())) {

                if (crop.matchesLookup(null, null, null, stack)) {

                    return true;
                }
            }

            return false;
        }

        private static boolean isSoil(ItemStack stack, RecipeManager recipes) {

            for (final Soil soil : recipes.getAllRecipesFor(BotanyPotHelper.SOIL_TYPE.get())) {

                if (soil.matchesLookup(null, null, null, stack)) {

                    return true;
                }
            }

            return false;
        }

        private static int compareById(Item e1, Item e2) {

            // Using toString because vanilla only compares on path and not namespace.
            return BuiltInRegistries.ITEM.getKey(e1).toString().compareTo(BuiltInRegistries.ITEM.getKey(e2).toString());
        }
    }
}