package net.darkhax.botanypots.common.impl.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.util.CommandHelper;
import net.darkhax.botanypots.common.api.BotanyPotsPlugin;
import net.darkhax.botanypots.common.api.command.generator.DataHelper;
import net.darkhax.botanypots.common.api.command.generator.crop.CropGenerator;
import net.darkhax.botanypots.common.api.command.generator.soil.SoilGenerator;
import net.darkhax.botanypots.common.api.data.recipes.crop.Crop;
import net.darkhax.botanypots.common.api.data.recipes.fertilizer.Fertilizer;
import net.darkhax.botanypots.common.api.data.recipes.soil.Soil;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.command.generator.MissingCropGenerator;
import net.darkhax.botanypots.common.impl.command.generator.MissingSoilGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SaplingBlock;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class MissingCommand {

    private static final CachedSupplier<Map<ResourceLocation, SoilGenerator>> SOIL_GENERATORS = CachedSupplier.cache(() -> {
        final Map<ResourceLocation, SoilGenerator> generators = new LinkedHashMap<>();
        BotanyPotsPlugin.CONTENT_PROVIDERS.get().forEach(plugin -> plugin.registerSoilGenerators(generators::put));
        generators.put(BotanyPotsMod.id("missing_fallback"), new MissingSoilGenerator());
        return generators;
    });

    private static final CachedSupplier<Map<ResourceLocation, CropGenerator>> CROP_GENERATORS = CachedSupplier.cache(() -> {
        final Map<ResourceLocation, CropGenerator> generators = new LinkedHashMap<>();
        BotanyPotsPlugin.CONTENT_PROVIDERS.get().forEach(plugin -> plugin.registerCropGenerators(generators::put));
        generators.put(BotanyPotsMod.id("missing_fallback"), new MissingCropGenerator());
        return generators;
    });

    private static final TagKey<Item> SAPLING_TAG = TagKey.create(Registries.ITEM, ResourceLocation.withDefaultNamespace("saplings"));
    private static final Comparator<ResourceLocation> ID_COMPARE = Comparator.comparing(ResourceLocation::toString);

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

    private static Map<ItemStack, SoilGenerator> collectMissingSoilItems(ServerLevel level) {
        final Map<ItemStack, SoilGenerator> missing = new HashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            final ItemStack stack = item.getDefaultInstance();
            if (!isSoil(stack, level) && !isCrop(stack, level)) {
                for (SoilGenerator generator : SOIL_GENERATORS.get().values()) {
                    if (generator.canGenerateSoil(level, stack)) {
                        missing.put(stack, generator);
                        break;
                    }
                }
            }
        }
        return missing;
    }

    private static int dumpMissingSoils(CommandContext<CommandSourceStack> ctx) {
        final ServerLevel level = ctx.getSource().getLevel();
        final boolean generate = CommandHelper.getBooleanArg("generate", ctx, () -> false);
        final Map<ItemStack, SoilGenerator> missing = collectMissingSoilItems(level);
        if (missing.isEmpty()) {
            ctx.getSource().sendSuccess(() -> BotanyPotsCommands.modMessage(Component.translatable("commands.botanypots.dump.no_results")), false);
            return 0;
        }
        else {
            if (generate) {
                final File outDir = setupDir("botanypots/generated/soils");
                for (Map.Entry<ItemStack, SoilGenerator> entry : missing.entrySet()) {
                    final ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(entry.getKey().getItem());
                    final File soilFile = new File(outDir, itemId.getNamespace() + "/soil/" + itemId.getPath() + ".json");
                    final JsonObject obj = entry.getValue().generateData(level, entry.getKey());
                    writeFile(soilFile, DataHelper.GSON.toJson(obj));
                }
            }
            final StringJoiner entries = new StringJoiner(System.lineSeparator());
            entries.add("Potential missing soil IDs");
            missing.keySet().stream().map(stack -> BuiltInRegistries.ITEM.getKey(stack.getItem())).sorted(Comparator.comparing(ResourceLocation::toString)).forEach(entry -> entries.add(entry.toString()));
            ctx.getSource().sendSuccess(() -> BotanyPotsCommands.modMessage(Component.translatable("commands.botanypots.dump.missing_soils", Component.literal(Integer.toString(missing.size())).withStyle(style -> style.withColor(ChatFormatting.RED))).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entries.toString())))), false);
            return missing.size();
        }
    }

    private static Map<ItemStack, CropGenerator> collectMissingCropItems(boolean collectSaplings, ServerLevel level) {
        final Map<ItemStack, CropGenerator> missing = new HashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            final ItemStack stack = item.getDefaultInstance();
            if (!isSoil(stack, level) && !isCrop(stack, level) && (collectSaplings || !isSapling(item))) {
                for (CropGenerator generator : CROP_GENERATORS.get().values()) {
                    if (generator.canGenerateCrop(level, stack)) {
                        missing.put(stack, generator);
                        break;
                    }
                }
            }
        }
        return missing;
    }

    private static int dumpMissingCrops(CommandContext<CommandSourceStack> ctx) {
        final ServerLevel level = ctx.getSource().getLevel();
        final boolean includeSaplings = CommandHelper.getBooleanArg("include_saplings", ctx, () -> false);
        final boolean generate = CommandHelper.getBooleanArg("generate", ctx, () -> false);
        final Map<ItemStack, CropGenerator> missingCrops = collectMissingCropItems(includeSaplings, level);
        if (missingCrops.isEmpty()) {
            ctx.getSource().sendSuccess(() -> BotanyPotsCommands.modMessage(Component.translatable("commands.botanypots.dump.no_results")), false);
            return 0;
        }
        if (generate) {
            final File outdir = setupDir("botanypots/generated/crops");
            for (Map.Entry<ItemStack, CropGenerator> entry : missingCrops.entrySet()) {
                final ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(entry.getKey().getItem());
                final File cropFile = new File(outdir, itemId.getNamespace() + "/crop/" + itemId.getPath() + ".json");
                writeFile(cropFile, DataHelper.GSON.toJson(entry.getValue().generateData(level, entry.getKey())));
            }
            ctx.getSource().sendSuccess(() -> Component.translatable("commands.botanypots.dump.generated").withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, outdir.getAbsolutePath()))), false);
        }
        else {
            final StringJoiner entries = new StringJoiner(System.lineSeparator());
            entries.add("Potential missing crop IDs");
            missingCrops.forEach((k, v) -> entries.add(BuiltInRegistries.ITEM.getKey(k.getItem()).toString()));
            ctx.getSource().sendSuccess(() -> BotanyPotsCommands.modMessage(Component.translatable("commands.botanypots.dump.missing_crops", Component.literal(Integer.toString(missingCrops.size())).withStyle(style -> style.withColor(ChatFormatting.RED))).withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, entries.toString())))), false);
        }
        return missingCrops.size();
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
        return item.getDefaultInstance().is(SAPLING_TAG) || item instanceof BlockItem blockItem && (blockItem.getBlock() instanceof SaplingBlock);
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
