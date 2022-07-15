package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.registry.RegistryDataProvider;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.block.inv.BotanyPotMenu;
import net.darkhax.botanypots.data.crop.CropRecipeSerializer;
import net.darkhax.botanypots.data.soil.SoilRecipeSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import java.util.Set;
import java.util.stream.Collectors;

public class Content extends RegistryDataProvider {

    public Content() {

        super(Constants.MOD_ID);

        this.withCreativeTab(() -> Services.REGISTRIES.items().get(new ResourceLocation(Constants.MOD_ID, "terracotta_botany_pot")));
        this.withAutoItemBlocks();
        this.bindBlockRenderLayers();

        // Recipe Types
        this.recipeTypes.add("soil");
        this.recipeTypes.add("crop");

        // Recipe Serializers
        this.recipeSerializers.add(() -> SoilRecipeSerializer.SERIALIZER, "soil");
        this.recipeSerializers.add(() -> CropRecipeSerializer.SERIALIZER, "crop");

        // Basic Pot
        this.blocks.add(() -> new BlockBotanyPot(false), "terracotta_botany_pot");
        this.blocks.add(() -> new BlockBotanyPot(true), "terracotta_hopper_botany_pot");

        for (DyeColor color : DyeColor.values()) {

            final BlockBehaviour.Properties properties = Block.Properties.of(Material.CLAY, color.getMaterialColor()).strength(1.25F, 4.2F).noOcclusion();

            // Coloured Terracotta
            this.blocks.add(() -> new BlockBotanyPot(properties, false), color.getName() + "_terracotta_botany_pot");
            this.blocks.add(() -> new BlockBotanyPot(properties, true), color.getName() + "_terracotta_hopper_botany_pot");

            // Coloured Concrete
            this.blocks.add(() -> new BlockBotanyPot(properties, false), color.getName() + "_concrete_botany_pot");
            this.blocks.add(() -> new BlockBotanyPot(properties, true), color.getName() + "_concrete_hopper_botany_pot");

            // Glazed Terracotta
            this.blocks.add(() -> new BlockBotanyPot(properties, false), color.getName() + "_glazed_terracotta_botany_pot");
            this.blocks.add(() -> new BlockBotanyPot(properties, true), color.getName() + "_glazed_terracotta_hopper_botany_pot");
        }

        this.blockEntities.add(() -> Services.CONSTRUCTS.blockEntityType(BlockEntityBotanyPot::new, this::getAllPots).get(), "botany_pot");
        this.menus.add(() -> Services.CONSTRUCTS.menuType(BotanyPotMenu::fromNetwork), "pot_menu");
    }

    private Set<Block> getAllPots() {

        return this.blocks.getEntries().values().stream().filter(b -> b instanceof BlockBotanyPot).collect(Collectors.toSet());
    }
}