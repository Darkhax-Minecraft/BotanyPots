package net.darkhax.botanypots.common.impl.data;

import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BotanyPotFileGenerator {

    private final File outDir;
    private final String ownerId;

    public BotanyPotFileGenerator(File outDir, String ownerId) {
        this.outDir = outDir;
        this.ownerId = ownerId;
    }

    public void potRecipes(ResourceLocation material) {
        final File craftingDir = setup(new File(outDir, "data/" + this.ownerId + "/recipe/" + this.ownerId + "/crafting"));
        basicPotRecipe(craftingDir, material);
        hopperPotRecipe(craftingDir, material);
        quickHopperPotRecipe(craftingDir, material);
        waxedPotRecipe(craftingDir, material);
    }

    public void lootTables(ResourceLocation material) {
        final File lootDir = setup(new File(outDir, "loot_table/blocks"));
        lootTable(lootDir, ResourceLocation.fromNamespaceAndPath(this.ownerId, material.getPath() + "_botany_pot"));
        lootTable(lootDir, ResourceLocation.fromNamespaceAndPath(this.ownerId, material.getPath() + "_hopper_botany_pot"));
        lootTable(lootDir, ResourceLocation.fromNamespaceAndPath(this.ownerId, material.getPath() + "_waxed_botany_pot"));
    }

    public void models(ResourceLocation material) {
        final File modelDir = setup(new File(outDir, "assets/" + this.ownerId + "/models"));
        final File blockDir = setup(new File(modelDir, "block"));
        final File itemDir = setup(new File(modelDir, "item"));
        final File stateDir = setup(new File(outDir, "assets/" + this.ownerId + "/blockstates"));

        write(new File(blockDir, material.getPath() + "_botany_pot.json"), BASIC_POT_MODEL_TEMPLATE.replace("$texture", material.getPath()));
        write(new File(itemDir, material.getPath() + "_botany_pot.json"), BLOCK_ITEM_MODEL_TEMPLATE.replace("$owner", this.ownerId).replace("$block_name", material.getPath() + "_botany_pot"));
        write(new File(stateDir, material.getPath() + "_botany_pot.json"), BLOCK_STATE_TEMPLATE.replace("$owner", this.ownerId).replace("$model_name", material.getPath() + "_botany_pot"));

        write(new File(blockDir, material.getPath() + "_hopper_botany_pot.json"), HOPPER_POT_MODEL_TEMPLATE.replace("$texture", material.getPath()));
        write(new File(itemDir, material.getPath() + "_hopper_botany_pot.json"), BLOCK_ITEM_MODEL_TEMPLATE.replace("$owner", this.ownerId).replace("$block_name", material.getPath() + "_hopper_botany_pot"));
        write(new File(stateDir, material.getPath() + "_hopper_botany_pot.json"), BLOCK_STATE_TEMPLATE.replace("$owner", this.ownerId).replace("$model_name", material.getPath() + "_hopper_botany_pot"));

        write(new File(blockDir, material.getPath() + "_waxed_botany_pot.json"), BASIC_POT_MODEL_TEMPLATE.replace("$texture", material.getPath()));
        write(new File(itemDir, material.getPath() + "_waxed_botany_pot.json"), BLOCK_ITEM_MODEL_TEMPLATE.replace("$owner", this.ownerId).replace("$block_name", material.getPath() + "_botany_pot"));
        write(new File(stateDir, material.getPath() + "_waxed_botany_pot.json"), BLOCK_STATE_TEMPLATE.replace("$owner", this.ownerId).replace("$model_name", material.getPath() + "_botany_pot"));
    }

    public void basicPotRecipe(File recipeDir, ResourceLocation material) {
        write(new File(recipeDir, material.getPath() + "_botany_pot.json"), format(BASIC_POT_RECIPE_TEMPLATE, material));
    }

    public void hopperPotRecipe(File recipeDir, ResourceLocation material) {
        write(new File(recipeDir, material.getPath() + "_hopper_botany_pot.json"), format(HOPPER_POT_RECIPE_TEMPLATE, material));
    }

    public void quickHopperPotRecipe(File recipeDir, ResourceLocation material) {
        write(new File(recipeDir, material.getPath() + "_hopper_botany_pot_quick.json"), format(QUICK_HOPPER_POT_RECIPE_TEMPLATE, material));
    }

    public void waxedPotRecipe(File recipeDir, ResourceLocation material) {
        write(new File(recipeDir, material.getPath() + "_waxed_botany_pot.json"), format(WAX_POT_RECIPE_TEMPLATE, material));
    }

    public void lootTable(File lootDir, ResourceLocation blockId) {
        write(new File(lootDir, blockId.getPath() + ".json"), DROP_SELF_TABLE.replace("$block_id", blockId.toString()));
    }

    private String format(String template, ResourceLocation material) {
        return template
                .replace("$owner", this.ownerId)
                .replace("$material_id", material.toString())
                .replace("$material_name", material.getPath());
    }

    private static void write(File file, String text) {
        try {
            file.createNewFile();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter writer = new FileWriter(file)) {
            writer.append(text);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String... args) {
        File blockTextures = new File("F:\\Minecraft\\game_data\\1.21\\assets\\minecraft\\textures\\block");
        File outDir = new File("botanypots/images");
        outDir.mkdirs();

        for (File file : blockTextures.listFiles()) {
            if (file.getName().endsWith(".png")) {
                final File outputImg = new File(outDir, file.getName().substring(0, file.getName().length() - 4) + "_pot_top.png");
                try {
                    outputImg.createNewFile();
                    makeTopTexture(file, outputImg);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static File setup(File dir) {
        dir.mkdirs();
        return dir;
    }

    public static void makeTopTexture(File inputFile, File outputFile) {
        try {
            final BufferedImage image = ImageIO.read(inputFile);
            final BufferedImage output = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
            if (image.getWidth() != 16 || image.getHeight() != 16) {
                return;
            }
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 16; y++) {
                    // top
                    if (y == 0 && x <= 11) {
                        output.setRGB(x, y, image.getRGB(x, 0));
                    }
                    // left
                    else if (x == 0 && y <= 11) {
                        output.setRGB(x, y, image.getRGB(y, 0));
                    }
                    // right
                    else if (x == 11 && y <= 11) {
                        output.setRGB(x, y, image.getRGB(11 - y, 0));
                    }
                    // bottom
                    else if (y == 11 && x <= 11) {
                        output.setRGB(x, y, image.getRGB(11 - x, 0));
                    }
                    // everywhere else
                    else {
                        output.setRGB(x, y, 0x00ffffff);
                    }
                }
            }
            ImageIO.write(output, "PNG", outputFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String BASIC_POT_MODEL_TEMPLATE = """
            {
                "parent": "botanypots:block/template/pot",
                "render_type": "minecraft:cutout",
                "textures": {
                    "material": "minecraft:block/$texture",
                    "material_top": "botanypots:block/$texture_pot_top"
                }
            }
            """;

    private static final String HOPPER_POT_MODEL_TEMPLATE = """
            {
                "parent": "botanypots:block/template/hopper_pot",
                "render_type": "minecraft:cutout",
                "textures": {
                    "material": "minecraft:block/$texture",
                    "material_top": "botanypots:block/$texture_pot_top"
                }
            }
            """;

    private static final String BLOCK_ITEM_MODEL_TEMPLATE = """
            {
                "parent": "$owner:block/$block_name"
            }
            """;

    private static final String BLOCK_STATE_TEMPLATE = """
            {
              "variants": {
                "": {
                  "model": "$owner:block/$model_name"
                }
              }
            }
            """;

    private static final String BASIC_POT_RECIPE_TEMPLATE = """
            {
                "bookshelf:load_conditions": [
                    {
                        "type": "botanypots:config",
                        "property": "can_craft_basic_pots"
                    }
                ],
                "type": "minecraft:crafting_shaped",
                "category": "misc",
                "group": "botanypots:basic_pot",
                "pattern": ["M M", "MPM", " M "],
                "key": {
                    "M": {
                        "item": "$material_id"
                    },
                    "P": {
                        "item": "minecraft:flower_pot"
                    }
                },
                "result": {
                    "id": "$owner:$material_name_botany_pot",
                    "count": 1
                }
            }""";

    private static final String HOPPER_POT_RECIPE_TEMPLATE = """
            {
                "bookshelf:load_conditions": [
                    {
                        "type": "botanypots:config",
                        "property": "can_craft_hopper_pots"
                    }
                ],
                "type": "minecraft:crafting_shapeless",
                "category": "misc",
                "group": "botanypots:hopper_pot",
                "ingredients": [
                    {
                        "item": "minecraft:hopper"
                    },
                    {
                        "item": "$owner:$material_name_botany_pot"
                    }
                ],
                "result": {
                    "id": "$owner:$material_name_hopper_botany_pot",
                    "count": 1
                }
            }""";

    private static final String QUICK_HOPPER_POT_RECIPE_TEMPLATE = """
            {
                "bookshelf:load_conditions": [
                    {
                        "type": "botanypots:config",
                        "property": "can_craft_hopper_pots"
                    }
                ],
                "type": "minecraft:crafting_shaped",
                "category": "misc",
                "group": "botanypots:quick_hopper_pot",
                "pattern": ["MHM", "MPM", " M "],
                "key": {
                    "M": {
                        "item": "$material_id"
                    },
                    "P": {
                        "item": "minecraft:flower_pot"
                    },
                    "H": {
                        "item": "minecraft:hopper"
                    }
                },
                "result": {
                    "id": "$owner:$material_name_hopper_botany_pot",
                    "count": 1
                }
            }""";

    private static final String WAX_POT_RECIPE_TEMPLATE = """
            {
                "bookshelf:load_conditions": [
                    {
                        "type": "botanypots:config",
                        "property": "can_wax_pots"
                    }
                ],
                "type": "minecraft:crafting_shapeless",
                "category": "misc",
                "group": "botanypots:waxed_pot",
                "ingredients": [
                    {
                        "item": "minecraft:honeycomb"
                    },
                    {
                        "item": "$owner:$material_name_botany_pot"
                    }
                ],
                "result": {
                    "id": "$owner:$material_name_waxed_botany_pot",
                    "count": 1
                }
            }""";

    private static final String DROP_SELF_TABLE = """
            {
              "type": "minecraft:block",
              "pools": [
                {
                  "conditions": [
                    {
                      "condition": "minecraft:survives_explosion"
                    }
                  ],
                  "entries": [
                    {
                      "type": "minecraft:item",
                      "name": "$block_id"
                    }
                  ],
                  "rolls": 1.0
                }
              ],
              "random_sequence": "$block_id"
            }
            """;
}