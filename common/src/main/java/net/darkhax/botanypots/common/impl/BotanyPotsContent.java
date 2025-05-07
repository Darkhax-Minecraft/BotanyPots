package net.darkhax.botanypots.common.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.MapCodec;
import net.darkhax.bookshelf.common.api.data.conditions.ILoadCondition;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.registry.IContentProvider;
import net.darkhax.bookshelf.common.api.registry.register.ItemComponentRegister;
import net.darkhax.bookshelf.common.api.registry.register.MenuRegister;
import net.darkhax.bookshelf.common.api.registry.register.Register;
import net.darkhax.bookshelf.common.api.registry.register.RegisterBlockEntityRenderer;
import net.darkhax.bookshelf.common.api.registry.register.RegisterItem;
import net.darkhax.bookshelf.common.api.registry.register.RegisterItemTab;
import net.darkhax.bookshelf.common.api.registry.register.RegisterMenuScreen;
import net.darkhax.bookshelf.common.api.registry.register.RegisterRecipeType;
import net.darkhax.bookshelf.common.api.service.Services;
import net.darkhax.botanypots.common.api.BotanyPotsPlugin;
import net.darkhax.botanypots.common.api.data.components.CropOverride;
import net.darkhax.botanypots.common.api.data.components.SoilOverride;
import net.darkhax.botanypots.common.impl.block.BotanyPotBlock;
import net.darkhax.botanypots.common.impl.block.BotanyPotRenderer;
import net.darkhax.botanypots.common.impl.block.PotType;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.darkhax.botanypots.common.impl.block.menu.BotanyPotMenu;
import net.darkhax.botanypots.common.impl.block.menu.BotanyPotScreen;
import net.darkhax.botanypots.common.impl.command.BotanyPotsCommands;
import net.darkhax.botanypots.common.impl.data.BotanyPotFileGenerator;
import net.darkhax.botanypots.common.impl.data.conditions.ConfigLoadCondition;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BasicCrop;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BlockDerivedCrop;
import net.darkhax.botanypots.common.impl.data.recipe.fertilizer.BasicFertilizer;
import net.darkhax.botanypots.common.impl.data.recipe.interaction.BasicPotInteraction;
import net.darkhax.botanypots.common.impl.data.recipe.soil.BasicSoil;
import net.darkhax.botanypots.common.impl.data.recipe.soil.BlockDerivedSoil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class BotanyPotsContent implements IContentProvider {

    public static final Supplier<ItemStack> TAB_ICON = CachedSupplier.cache(() -> BuiltInRegistries.ITEM.get(BotanyPotsMod.id("terracotta_botany_pot")).getDefaultInstance());
    private static final String[] BRICK_TYPES = {"brick", "stone", "mossy_stone", "deepslate", "tuff", "mud", "prismarine", "nether", "red_nether", "polished_blackstone", "end_stone", "quartz"};
    private final Map<ResourceLocation, Block> allPotBlocks = new LinkedHashMap<>();

    public BotanyPotsContent() {

//        final BotanyPotFileGenerator gen = new BotanyPotFileGenerator(new File("outdir"), BotanyPotsMod.MOD_ID);
//        make(gen, "terracotta");
//        for (DyeColor color : DyeColor.values()) {
//            make(gen, color.getName() + "_terracotta");
//            make(gen, color.getName() + "_glazed_terracotta");
//            make(gen, color.getName() + "_concrete");
//        }
    }

    private void make(BotanyPotFileGenerator gen, String block) {
        final ResourceLocation blockId = ResourceLocation.withDefaultNamespace(block);
        gen.potRecipes(blockId);
        gen.models(blockId);
        gen.lootTables(blockId);
    }

    @Override
    public void registerBlocks(Register<Block> registry) {
        createPots(registry, "terracotta");
        for (DyeColor color : DyeColor.values()) {
            createPots(registry, color.getName() + "_terracotta");
            createPots(registry, color.getName() + "_glazed_terracotta");
            createPots(registry, color.getName() + "_concrete");
        }
        for (String brickType : BRICK_TYPES) {
            createPots(registry, "brick".equals(brickType) ? "bricks" : brickType + "_bricks");
        }
    }

    private void createPots(Register<Block> registry, String name) {
        final ResourceLocation blockId = ResourceLocation.withDefaultNamespace(name);
        final MapColor color = BuiltInRegistries.BLOCK.containsKey(blockId) ? BuiltInRegistries.BLOCK.get(blockId).defaultMapColor() : MapColor.COLOR_ORANGE;
        registerPot(registry, name + "_botany_pot", new BotanyPotBlock(color, PotType.BASIC));
        registerPot(registry, name + "_hopper_botany_pot", new BotanyPotBlock(color, PotType.HOPPER));
        registerPot(registry, name + "_waxed_botany_pot", new BotanyPotBlock(color, PotType.WAXED));
    }

    private void registerPot(Register<Block> registry, String id, Block block) {
        final ResourceLocation blockId = ResourceLocation.fromNamespaceAndPath(this.contentNamespace(), id);
        registry.add(blockId, block);
        allPotBlocks.put(blockId, block);
    }

    @Override
    public void registerItems(RegisterItem registry) {
        for (Map.Entry<ResourceLocation, Block> block : allPotBlocks.entrySet()) {
            registry.addBlock(block.getValue());
        }
    }

    @Override
    public void registerBlockEntities(Register<BlockEntityType.Builder<?>> registry) {
        registry.add("botany_pot", Services.GAMEPLAY.blockEntityBuilder(BotanyPotBlockEntity::new, this.allPotBlocks.values().toArray(Block[]::new)));
        BotanyPotsPlugin.PLUGINS.get().forEach(BotanyPotsPlugin::registerDisplayTypes);
        BotanyPotsPlugin.PLUGINS.get().forEach(BotanyPotsPlugin::registerDropProviders);
        BotanyPotsPlugin.PLUGINS.get().forEach(BotanyPotsPlugin::registerGrowthAmountTypes);
    }

    @Override
    public void registerMenus(MenuRegister registry) {
        registry.add("basic_pot_menu", BotanyPotMenu::basicMenuClient);
        registry.add("hopper_pot_menu", BotanyPotMenu::hopperMenuClient);
    }

    @Override
    public void registerRecipeTypes(RegisterRecipeType registry) {
        registry.add("soil");
        registry.add("crop");
        registry.add("pot_interaction");
        registry.add("fertilizer");
    }

    @Override
    public void registerRecipeSerializers(Register<RecipeSerializer<?>> registry) {
        registry.add("soil", BasicSoil.SERIALIZER);
        registry.add("block_derived_soil", BlockDerivedSoil.SERIALIZER);
        registry.add("crop", BasicCrop.SERIALIZER);
        registry.add("block_derived_crop", BlockDerivedCrop.SERIALIZER);
        registry.add("pot_interaction", BasicPotInteraction.SERIALIZER);
        registry.add("fertilizer", BasicFertilizer.SERIALIZER);
    }

    @Override
    public void registerLoadConditions(Register<MapCodec<? extends ILoadCondition>> registry) {
        registry.add(ConfigLoadCondition.TYPE_ID, ConfigLoadCondition.CODEC);
    }

    @Override
    public void registerItemTabs(RegisterItemTab registry) {
        registry.add("tab", TAB_ICON, (params, builder) -> {
            for (Block block : this.allPotBlocks.values()) {
                builder.accept(block.asItem());
            }
        });
    }

    @Override
    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        BotanyPotsCommands.build(dispatcher, context, selection);
    }

    @Override
    public void bindBlockEntityRenderer(RegisterBlockEntityRenderer registry) {
        registry.bind(BotanyPotBlockEntity.TYPE.get(), BotanyPotRenderer::new);
        BotanyPotsPlugin.PLUGINS.get().forEach(BotanyPotsPlugin::bindDisplayRenderers);
    }

    @Override
    public void registerItemComponents(ItemComponentRegister registry) {
        registry.accept(CropOverride.TYPE_ID, (UnaryOperator<DataComponentType.Builder<CropOverride>>) b -> b.persistent(CropOverride.CODEC).networkSynchronized(CropOverride.STREAM));
        registry.accept(SoilOverride.TYPE_ID, (UnaryOperator<DataComponentType.Builder<SoilOverride>>) b -> b.persistent(SoilOverride.CODEC).networkSynchronized(SoilOverride.STREAM));
    }

    @Override
    public void registerMenuScreens(RegisterMenuScreen registry) {
        registry.bind(BotanyPotMenu.BASIC_MENU.get(), BotanyPotScreen::new);
        registry.bind(BotanyPotMenu.HOPPER_MENU.get(), BotanyPotScreen::new);
    }

    @Override
    public void bindRenderLayers(BiConsumer<Block, RenderType> registry) {
        for (Block block : this.allPotBlocks.values()) {
            registry.accept(block, RenderType.cutout());
        }
    }

    @Override
    public String contentNamespace() {
        return BotanyPotsMod.MOD_ID;
    }

    public static Component modMessage(Component component) {
        return Component.translatable("commands.botanypots.mod_message", component);
    }
}
