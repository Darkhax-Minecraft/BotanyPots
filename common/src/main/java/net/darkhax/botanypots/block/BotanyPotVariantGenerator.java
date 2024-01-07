package net.darkhax.botanypots.block;

import net.darkhax.botanypots.Constants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BotanyPotVariantGenerator {

    public static File blockDir = makeDir("assets/botanypots/models/block");
    public static File itemDir = makeDir("assets/botanypots/models/item");
    public static File stateDir = makeDir("assets/botanypots/blockstates");
    public static File lootDir = makeDir("data/botanypots/loot_tables/blocks");
    public static File craftingDir = makeDir("data/botanypots/recipes/crafting");


    public static void generate() {

        // Default
        generatePot(Blocks.TERRACOTTA);

        // Terracotta
        generatePot(Blocks.WHITE_TERRACOTTA);
        generatePot(Blocks.ORANGE_TERRACOTTA);
        generatePot(Blocks.MAGENTA_TERRACOTTA);
        generatePot(Blocks.LIGHT_BLUE_TERRACOTTA);
        generatePot(Blocks.YELLOW_TERRACOTTA);
        generatePot(Blocks.LIME_TERRACOTTA);
        generatePot(Blocks.PINK_TERRACOTTA);
        generatePot(Blocks.GRAY_TERRACOTTA);
        generatePot(Blocks.LIGHT_GRAY_TERRACOTTA);
        generatePot(Blocks.CYAN_TERRACOTTA);
        generatePot(Blocks.PURPLE_TERRACOTTA);
        generatePot(Blocks.BLUE_TERRACOTTA);
        generatePot(Blocks.BROWN_TERRACOTTA);
        generatePot(Blocks.GREEN_TERRACOTTA);
        generatePot(Blocks.RED_TERRACOTTA);
        generatePot(Blocks.BLACK_TERRACOTTA);

        // Glazed Terracotta
        generatePot(Blocks.WHITE_GLAZED_TERRACOTTA);
        generatePot(Blocks.ORANGE_GLAZED_TERRACOTTA);
        generatePot(Blocks.MAGENTA_GLAZED_TERRACOTTA);
        generatePot(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
        generatePot(Blocks.YELLOW_GLAZED_TERRACOTTA);
        generatePot(Blocks.LIME_GLAZED_TERRACOTTA);
        generatePot(Blocks.PINK_GLAZED_TERRACOTTA);
        generatePot(Blocks.GRAY_GLAZED_TERRACOTTA);
        generatePot(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
        generatePot(Blocks.CYAN_GLAZED_TERRACOTTA);
        generatePot(Blocks.PURPLE_GLAZED_TERRACOTTA);
        generatePot(Blocks.BLUE_GLAZED_TERRACOTTA);
        generatePot(Blocks.BROWN_GLAZED_TERRACOTTA);
        generatePot(Blocks.GREEN_GLAZED_TERRACOTTA);
        generatePot(Blocks.RED_GLAZED_TERRACOTTA);
        generatePot(Blocks.BLACK_GLAZED_TERRACOTTA);

        // Concrete
        generatePot(Blocks.WHITE_CONCRETE);
        generatePot(Blocks.ORANGE_CONCRETE);
        generatePot(Blocks.MAGENTA_CONCRETE);
        generatePot(Blocks.LIGHT_BLUE_CONCRETE);
        generatePot(Blocks.YELLOW_CONCRETE);
        generatePot(Blocks.LIME_CONCRETE);
        generatePot(Blocks.PINK_CONCRETE);
        generatePot(Blocks.GRAY_CONCRETE);
        generatePot(Blocks.LIGHT_GRAY_CONCRETE);
        generatePot(Blocks.CYAN_CONCRETE);
        generatePot(Blocks.PURPLE_CONCRETE);
        generatePot(Blocks.BLUE_CONCRETE);
        generatePot(Blocks.BROWN_CONCRETE);
        generatePot(Blocks.GREEN_CONCRETE);
        generatePot(Blocks.RED_CONCRETE);
        generatePot(Blocks.BLACK_CONCRETE);
    }

    public static void generatePot(Block block) {

        try {

            final ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);

            Constants.LOG.info("botanypots:" + blockId.getPath() + "_botany_pot");
            Constants.LOG.info("botanypots:" + blockId.getPath() + "_hopper_botany_pot");

            // Normal
            generateBlockState(blockId, "botany_pot");
            generateBlockModel(blockId, "botany_pot", false);
            generateItemModel(blockId, "botany_pot");
            generateLootTable(blockId, "botany_pot");
            generateBasicCrafting(blockId);

            // Hopper
            generateBlockState(blockId, "hopper_botany_pot");
            generateBlockModel(blockId, "hopper_botany_pot", true);
            generateItemModel(blockId, "hopper_botany_pot");
            generateLootTable(blockId, "hopper_botany_pot");
            generateHopperCrafting(blockId);
            generateCompactHopperCrafting(blockId);
        }

        catch (Exception e) {

        }
    }

    public static void generateCompactHopperCrafting(ResourceLocation blockId) throws IOException {

        final String potId = "botanypots:" + blockId.getPath() + "_hopper_botany_pot";

        try (FileWriter writer = new FileWriter(new File(craftingDir, blockId.getPath() + "_compact_hopper_botany_pot.json"))) {

            writer.append("{\n" +
                    "    \"type\": \"crafting_shaped\",\n" +
                    "    \"pattern\": [\n" +
                    "        \"SHS\",\n" +
                    "        \"SPS\",\n" +
                    "        \" S \"\n" +
                    "    ],\n" +
                    "    \"key\": {\n" +
                    "        \"S\": {\n" +
                    "            \"item\": \"" + blockId.toString() + "\"\n" +
                    "        },\n" +
                    "        \"P\": {\n" +
                    "            \"item\": \"minecraft:flower_pot\"\n" +
                    "        },\n" +
                    "        \"H\": {\n" +
                    "            \"item\": \"minecraft:hopper\"\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"result\": {\n" +
                    "        \"item\": \"" + potId.toString() + "\",\n" +
                    "        \"count\": 1\n" +
                    "    }\n" +
                    "}");
        }
    }

    public static void generateHopperCrafting(ResourceLocation blockId) throws IOException {

        final String potId = "botanypots:" + blockId.getPath() + "_hopper_botany_pot";

        try (FileWriter writer = new FileWriter(new File(craftingDir, blockId.getPath() + "_hopper_botany_pot.json"))) {

            writer.append("{\n" +
                    "    \"type\": \"minecraft:crafting_shapeless\",\n" +
                    "    \"ingredients\": [\n" +
                    "        {\n" +
                    "            \"item\": \"botanypots:" + blockId.getPath() + "_botany_pot\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"item\": \"minecraft:hopper\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"result\": {\n" +
                    "        \"item\": \"" + potId + "\",\n" +
                    "        \"count\": 1\n" +
                    "    }\n" +
                    "}");
        }
    }

    public static void generateBasicCrafting(ResourceLocation blockId) throws IOException {

        final String potId = "botanypots:" + blockId.getPath() + "_botany_pot";

        try (FileWriter writer = new FileWriter(new File(craftingDir, blockId.getPath() + "_botany_pot.json"))) {

            writer.append("{\n" +
                    "    \"type\": \"crafting_shaped\",\n" +
                    "    \"pattern\": [\n" +
                    "        \"S S\",\n" +
                    "        \"SPS\",\n" +
                    "        \" S \"\n" +
                    "    ],\n" +
                    "    \"key\": {\n" +
                    "        \"S\": {\n" +
                    "            \"item\": \"" + blockId.toString() + "\"\n" +
                    "        },\n" +
                    "        \"P\": {\n" +
                    "            \"item\": \"minecraft:flower_pot\"\n" +
                    "        }\n" +
                    "    },\n" +
                    "    \"result\": {\n" +
                    "        \"item\": \"" + potId.toString() + "\",\n" +
                    "        \"count\": 1\n" +
                    "    }\n" +
                    "}");
        }
    }

    public static void generateLootTable(ResourceLocation blockId, String suffix) throws IOException {

        final String potId = blockId.getPath() + "_" + suffix;

        try (FileWriter writer = new FileWriter(new File(lootDir, potId + ".json"))) {

            writer.append("{\n" +
                    "  \"type\": \"minecraft:block\",\n" +
                    "  \"pools\": [\n" +
                    "    {\n" +
                    "      \"bonus_rolls\": 0.0,\n" +
                    "      \"conditions\": [\n" +
                    "        {\n" +
                    "          \"condition\": \"minecraft:survives_explosion\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"entries\": [\n" +
                    "        {\n" +
                    "          \"type\": \"minecraft:item\",\n" +
                    "          \"functions\": [\n" +
                    "            {\n" +
                    "              \"function\": \"minecraft:copy_name\",\n" +
                    "              \"source\": \"block_entity\"\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"name\": \"" + Constants.MOD_ID + ":" + potId + "\"\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"rolls\": 1.0\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
        }
    }

    public static void generateBlockState(ResourceLocation blockId, String suffix) throws IOException {

        final String potId = blockId.getPath() + "_" + suffix;

        try (FileWriter writer = new FileWriter(new File(stateDir, potId + ".json"))) {

            writer.append("{\n" +
                    "    \"variants\": {\n" +
                    "        \"\": {\n" +
                    "            \"model\": \"botanypots:block/" + potId + "\"\n" +
                    "        }\n" +
                    "    }\n" +
                    "}");
        }
    }

    public static void generateItemModel(ResourceLocation blockId, String suffix) throws IOException {

        final String potId = blockId.getPath() + "_" + suffix;

        try (FileWriter writer = new FileWriter(new File(itemDir, potId + ".json"))) {

            writer.append("{\n" +
                    "    \"parent\": \"botanypots:block/" + potId + "\"\n" +
                    "}");
        }
    }

    public static void generateBlockModel(ResourceLocation blockId, String suffix, boolean hopper) throws IOException {

        final String potId = blockId.getPath() + "_" + suffix;
        final String parent = hopper ? "hopper_botany_pot_base" : "botany_pot_base";

        try (FileWriter writer = new FileWriter(new File(blockDir, potId + ".json"))) {

            writer.append("{\n" +
                    "    \"parent\": \"botanypots:block/" + parent + "\",\n" +
                    "    \"textures\": {\n" +
                    "        \"terracotta\": \"minecraft:block/" + blockId.getPath() + "\",\n" +
                    "        \"particle\": \"minecraft:block/" + blockId.getPath() + "\"\n" +
                    "    }\n" +
                    "}");
        }
    }

    private static File makeDir(String path) {

        final File dir = new File(new File("datagen_out"), path);

        if (!dir.exists()) {

            dir.mkdirs();
        }

        return dir;
    }
}