package net.darkhax.botanypots.common.impl.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.darkhax.bookshelf.common.api.function.ReloadableCache;
import net.darkhax.bookshelf.common.api.util.CommandHelper;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.fertilizer.Fertilizer;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BasicCrop;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseCoralPlantTypeBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.GrowingPlantBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SporeBlossomBlock;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MissingCommand {

    private static final TagKey<Item> SOIL_WATER = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("botanypots", "soil/water"));
    private static final TagKey<Item> SOIL_LAVA = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("botanypots", "soil/lava"));
    private static final TagKey<Item> SOIL_SNOW = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("botanypots", "soil/snow"));

    private static final Comparator<ResourceLocation> ID_COMPARE = Comparator.comparing(ResourceLocation::toString);
    private static final TagKey<Item> FORGE_SEEDS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("forge", "seeds"));
    private static final TagKey<Item> COMMON_SEEDS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "seeds"));
    private static final ReloadableCache<Set<Item>> IGNORED_ITEMS = ReloadableCache.of(() -> {
        final Set<Item> items = new HashSet<>();
        items.add(Items.DEAD_BUSH);
        items.add(Items.MANGROVE_LEAVES);
        items.add(Items.NETHERRACK);
        items.add(Items.ROOTED_DIRT);
        items.add(Items.GRASS_BLOCK);
        items.add(Items.WARPED_NYLIUM);
        items.add(Items.CRIMSON_NYLIUM);
        return items;
    });
    public static final String SEED_TEMPLATE = """
            {
              "bookshelf:load_conditions": [
                {
                  "type": "bookshelf:block_exists",
                  "values": [
                    "$block_id$"
                  ]
                }
              ],
              "type": "botanypots:block_derived_crop",
              "block": "$block_id$"
            }
            """;

    public static final String WATER_SOIL = """
            {
              "bookshelf:load_conditions": [
                {
                  "type": "bookshelf:item_exists",
                  "values": [
                    "$item_id$"
                  ]
                }
              ],
              "type": "botanypots:soil",
              "input": {
                "item": "$item_id$"
              },
              "display": {
                "type": "botanypots:simple",
                "block_state": {
                  "block": "minecraft:water"
                },
                "options": {
                  "render_fluid": true
                }
              }
            }
            """;

    public static final String SNOW_SOIL = """
            {
              "bookshelf:load_conditions": [
                {
                  "type": "bookshelf:item_exists",
                  "values": [
                    "$item_id$"
                  ]
                }
              ],
              "type": "botanypots:soil",
              "input": {
                "item": "$item_id$"
              },
              "display": {
                "type": "botanypots:simple",
                "block_state": {
                  "block": "minecraft:snow_block"
                }
              }
            }
            """;

    public static final String BLOCK_SOIL = """
            {
              "bookshelf:load_conditions": [
                {
                  "type": "bookshelf:block_exists",
                  "values": [
                    "$block_id$"
                  ]
                }
              ],
              "type": "botanypots:block_derived_soil",
              "block": "$block_id$"
            }
            """;

    public static void build(LiteralArgumentBuilder<CommandSourceStack> parent) {
        final LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("missing");
        final LiteralArgumentBuilder<CommandSourceStack> seeds = Commands.literal("seeds");
        seeds.executes(MissingCommand::dumpMissingCrops);
        seeds.then(Commands.argument("include_saplings", BoolArgumentType.bool()).executes(MissingCommand::dumpMissingCrops).then(Commands.argument("generate", BoolArgumentType.bool()).executes(MissingCommand::dumpMissingCrops)));
        cmd.then(seeds);

        final LiteralArgumentBuilder<CommandSourceStack> soils = Commands.literal("soils");
        soils.executes(MissingCommand::dumpMissingSoils);
        soils.then(Commands.argument("generate", BoolArgumentType.bool()).executes(MissingCommand::dumpMissingSoils));
        cmd.then(soils);

        parent.then(cmd);
    }

    private static int dumpMissingSoils(CommandContext<CommandSourceStack> ctx) {
        final ServerLevel level = ctx.getSource().getLevel();
        final boolean generate = testArg("generate", ctx, false);

        final Set<Item> missing = new HashSet<>();
        for (Item item : BuiltInRegistries.ITEM) {
            final ItemStack stack = item.getDefaultInstance();
            if (!isSoil(stack, level)) {
                for (RecipeHolder<Crop> crop : Objects.requireNonNull(Crop.RECIPES.apply(level)).values()) {
                    if (crop.value() instanceof BasicCrop basic && basic.isValidSoil(stack)) {
                        missing.add(item);
                    }
                }
            }
        }
        addMissingSoils(level, SOIL_WATER, missing);
        addMissingSoils(level, SOIL_LAVA, missing);
        addMissingSoils(level, SOIL_SNOW, missing);

        if (missing.isEmpty()) {
            ctx.getSource().sendSuccess(() -> BotanyPotsCommands.modMessage(Component.translatable("commands.botanypots.dump.no_results")), false);
            return 0;
        }
        else {
            if (generate) {
                generateMissingSoils(level, missing);
            }
            final StringJoiner entries = new StringJoiner(System.lineSeparator());
            entries.add("Potential missing soil IDs");
            missing.stream().map(BuiltInRegistries.ITEM::getKey).sorted(Comparator.comparing(ResourceLocation::toString)).forEach(entry -> entries.add(entry.toString()));
            ctx.getSource().sendSuccess(() -> BotanyPotsCommands.modMessage(Component.translatable("commands.botanypots.dump.missing_soils", Component.literal(Integer.toString(missing.size())).withStyle(style -> style.withColor(ChatFormatting.RED))).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entries.toString())))), false);
        }
        return 0;
    }

    private static void generateMissingSoils(ServerLevel level, Set<Item> missing) {
        final File outDir = setupDir("botanypots/generated/soils");
        for (Item item : missing) {
            final ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            final File soilFile = new File(outDir, itemId.getNamespace() + "/" + itemId.getPath() + ".json");
            final ItemStack stack = item.getDefaultInstance();
            if (stack.is(SOIL_WATER)) {
                writeFile(soilFile, WATER_SOIL.replace("$item_id$", itemId.toString()));
            }
            else if (stack.is(SOIL_SNOW)) {
                writeFile(soilFile, SNOW_SOIL.replace("$item_id$", itemId.toString()));
            }
            else if (item instanceof BlockItem blockItem) {
                writeFile(soilFile, BLOCK_SOIL.replace("$item_id$", itemId.toString()).replace("$block_id$", BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).toString()));
            }
        }
    }

    private static int dumpMissingCrops(CommandContext<CommandSourceStack> ctx) {
        final boolean includeSaplings = testArg("include_saplings", ctx, false);
        final boolean generate = testArg("generate", ctx, false);
        final Set<ResourceLocation> missingCrops = getMissingCrops(ctx.getSource().getLevel(), includeSaplings);

        if (missingCrops.isEmpty()) {
            ctx.getSource().sendSuccess(() -> BotanyPotsCommands.modMessage(Component.translatable("commands.botanypots.dump.no_results")), false);
            return 0;
        }

        if (generate) {
            final File outdir = setupDir("botanypots/generated/crops");
            for (ResourceLocation itemId : missingCrops) {
                final Item item = BuiltInRegistries.ITEM.get(itemId);
                if (item instanceof BlockItem blockItem) {
                    final ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
                    try (FileWriter writer = new FileWriter(new File(outdir, blockId.getPath() + ".json"))) {
                        writer.append(SEED_TEMPLATE.replace("$block_id$", blockId.toString()));
                    }
                    catch (IOException e) {
                        BotanyPotsMod.LOG.error("Failed to generate crop for item {}.", itemId, e);
                    }
                }
            }
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.botanypots.dump.generated").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, outdir.getAbsolutePath()))), false);
        }
        else {
            final StringJoiner entries = new StringJoiner(System.lineSeparator());
            entries.add("Potential missing crop IDs");
            missingCrops.forEach(entry -> entries.add(entry.toString()));
            ctx.getSource().sendSuccess(() -> BotanyPotsCommands.modMessage(Component.translatable("commands.botanypots.dump.missing_crops", Component.literal(Integer.toString(missingCrops.size())).withStyle(style -> style.withColor(ChatFormatting.RED))).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entries.toString())))), false);
        }
        return 0;
    }

    private static Set<ResourceLocation> getMissingCrops(Level level, boolean includeSaplings) {
        final Set<Item> missingSeedItems = new HashSet<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (isCrop(item.getDefaultInstance(), level) || isSoil(item.getDefaultInstance(), level)) {
                continue;
            }
            if (item instanceof BlockItem itemBlock) {
                final Block placedBlock = itemBlock.getBlock();
                final ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(placedBlock);
                if (placedBlock instanceof CropBlock || placedBlock instanceof GrowingPlantBlock || placedBlock instanceof BonemealableBlock || placedBlock instanceof SaplingBlock || placedBlock instanceof BushBlock || placedBlock instanceof SporeBlossomBlock || (placedBlock instanceof BaseCoralPlantTypeBlock && !blockId.getPath().startsWith("dead_"))) {
                    missingSeedItems.add(item);
                }
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
        final Consumer<Item> tagProcessor = item -> {
            if (!isCrop(item.getDefaultInstance(), level) && !isSoil(item.getDefaultInstance(), level)) {
                missingSeedItems.add(item);
            }
        };
        processTag(FORGE_SEEDS, tagProcessor);
        processTag(COMMON_SEEDS, tagProcessor);
        return missingSeedItems.stream().filter(item -> (includeSaplings || !isSapling(item)) && !Objects.requireNonNull(IGNORED_ITEMS.apply(level)).contains(item)).map(BuiltInRegistries.ITEM::getKey).sorted(ID_COMPARE).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private static void processTag(TagKey<Item> key, Consumer<Item> consumer) {
        BuiltInRegistries.ITEM.getTag(key).ifPresent(named -> named.forEach(entry -> consumer.accept(entry.value())));
    }

    private static boolean isCrop(ItemStack stack, Level level) {
        return Objects.requireNonNull(Crop.CACHE.apply(level)).isCached(stack);
    }

    private static boolean isSoil(ItemStack stack, Level level) {
        return Objects.requireNonNull(Soil.CACHE.apply(level)).isCached(stack);
    }

    private static boolean isFertilizer(ItemStack stack, Level level) {
        return Objects.requireNonNull(Fertilizer.CACHE.apply(level)).isCached(stack);
    }

    private static boolean isSapling(Item item) {
        return item instanceof BlockItem blockItem && blockItem.getBlock() instanceof SaplingBlock;
    }

    private static boolean testArg(String name, CommandContext<CommandSourceStack> ctx, boolean fallback) {
        return CommandHelper.hasArgument(name, ctx) ? BoolArgumentType.getBool(ctx, name) : fallback;
    }

    private static void addMissingSoils(ServerLevel level, TagKey<Item> tag, Collection<Item> items) {
        addFromTag(tag, items, item -> !isSoil(item.getDefaultInstance(), level));
    }

    private static void addFromTag(TagKey<Item> tag, Collection<Item> items, Predicate<Item> test) {
        for (Holder<Item> entry : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            if (test.test(entry.value())) {
                items.add(entry.value());
            }
        }
    }

    private static void writeFile(File file, String text) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.append(text);
        }
        catch (IOException e) {
            // No
        }
    }

    private static File setupDir(String dir) {
        final File file = new File(dir);
        if (file.exists()) {
            try {
                FileUtils.deleteDirectory(file);
            }
            catch (Exception e) {
                BotanyPotsMod.LOG.error("Failed to setup dir {}.", dir, e);
            }
        }
        file.mkdirs();
        return file;
    }
}
