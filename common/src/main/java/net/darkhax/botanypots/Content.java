package net.darkhax.botanypots;

import net.darkhax.bookshelf.api.Services;
import net.darkhax.bookshelf.api.registry.RegistryDataProvider;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.block.inv.BotanyPotMenu;
import net.darkhax.botanypots.commands.BotanyPotsCommands;
import net.darkhax.botanypots.data.recipes.crop.BasicCropSerializer;
import net.darkhax.botanypots.data.recipes.fertilizer.BasicFertilizerSerializer;
import net.darkhax.botanypots.data.recipes.potinteraction.BasicPotInteractionSerializer;
import net.darkhax.botanypots.data.recipes.soil.BasicSoilSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Set;
import java.util.stream.Collectors;

public class Content extends RegistryDataProvider {

    public Content() {

        super(Constants.MOD_ID);

        this.withItemTab(() -> new ItemStack(BuiltInRegistries.ITEM.get(new ResourceLocation(Constants.MOD_ID, "terracotta_botany_pot"))));
        this.withAutoItemBlocks();

        // Recipe Types
        this.recipeTypes.add("soil");
        this.recipeTypes.add("crop");
        this.recipeTypes.add("pot_interaction");
        this.recipeTypes.add("fertilizer");

        // Recipe Serializers
        this.recipeSerializers.add(() -> BasicSoilSerializer.SERIALIZER, "soil");
        this.recipeSerializers.add(() -> BasicCropSerializer.SERIALIZER, "crop");
        this.recipeSerializers.add(() -> BasicPotInteractionSerializer.SERIALIZER, "pot_interaction");
        this.recipeSerializers.add(() -> BasicFertilizerSerializer.SERIALIZER, "fertilizer");

        // Basic Pot
        this.blocks.add(() -> new BlockBotanyPot(false), "terracotta_botany_pot");
        this.blocks.add(() -> new BlockBotanyPot(true), "terracotta_hopper_botany_pot");

        for (DyeColor color : DyeColor.values()) {

            final BlockBehaviour.Properties properties = Block.Properties.of().mapColor(color).strength(1.25F, 4.2F).noOcclusion();

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

        this.blockEntities.add(() -> Services.CONSTRUCTS.blockEntityType((pos, state) -> new BlockEntityBotanyPot(BlockEntityBotanyPot.POT_TYPE.get(), pos, state), this::getAllPots).get(), "botany_pot");
        this.menus.add(() -> Services.CONSTRUCTS.menuType(BotanyPotMenu::fromNetwork), "pot_menu");
        this.commands.add(BotanyPotsCommands::new, "commands");
    }

    private Set<Block> getAllPots() {

        return this.blocks.getEntries().values().stream().filter(b -> b instanceof BlockBotanyPot).collect(Collectors.toSet());
    }
}