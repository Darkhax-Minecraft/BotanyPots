package net.darkhax.botanypots.common.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.serialization.MapCodec;
import net.darkhax.bookshelf.common.api.data.conditions.ILoadCondition;
import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.bookshelf.common.api.registry.ContentProvider;
import net.darkhax.bookshelf.common.api.registry.adapters.GameRegistryAdapter;
import net.darkhax.bookshelf.common.api.registry.adapters.GenericRegistryAdapter;
import net.darkhax.bookshelf.common.api.service.Services;
import net.darkhax.bookshelf.common.impl.registry.adapter.BlockEntityRendererAdapter;
import net.darkhax.bookshelf.common.impl.registry.adapter.BlockRegistryAdapter;
import net.darkhax.bookshelf.common.impl.registry.adapter.BlockRenderTypeAdapter;
import net.darkhax.bookshelf.common.impl.registry.adapter.CreativeModeTabAdapter;
import net.darkhax.bookshelf.common.impl.registry.adapter.MenuScreenAdapter;
import net.darkhax.bookshelf.common.impl.registry.adapter.MenuTypeAdapter;
import net.darkhax.bookshelf.common.impl.registry.adapter.RecipeTypeAdapter;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Unbreakable;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.MapColor;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BotanyPotsContent implements ContentProvider {

    public static final Supplier<ItemStack> TAB_ICON = CachedSupplier.cache(() -> BuiltInRegistries.ITEM.get(BotanyPotsMod.id("terracotta_botany_pot")).getDefaultInstance());
    private static final String[] BRICK_TYPES = {"brick", "stone", "mossy_stone", "deepslate", "tuff", "mud", "prismarine", "nether", "red_nether", "polished_blackstone", "end_stone", "quartz"};
    private final Map<ResourceLocation, Block> allPotBlocks = new LinkedHashMap<>();

    public BotanyPotsContent() {
        //generatePotFiles();
    }

    private void generatePotFiles() {
        final BotanyPotFileGenerator gen = new BotanyPotFileGenerator(new File("outdir"), BotanyPotsMod.MOD_ID);
        make(gen, "terracotta");
        for (DyeColor color : DyeColor.values()) {
            make(gen, color.getName() + "_terracotta");
            make(gen, color.getName() + "_glazed_terracotta");
            make(gen, color.getName() + "_concrete");
        }
        for (String brickType : BRICK_TYPES) {
            make(gen, brickType.equalsIgnoreCase("brick") ? "bricks" : brickType + "_bricks");
        }
    }

    private void make(BotanyPotFileGenerator gen, String block) {
        final ResourceLocation blockId = ResourceLocation.withDefaultNamespace(block);
        gen.potRecipes(blockId);
        gen.models(blockId);
        gen.lootTables(blockId);
    }

    @Override
    public void defineBlocks(BlockRegistryAdapter registry) {
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

    private void createPots(BlockRegistryAdapter registry, String name) {
        final ResourceLocation blockId = ResourceLocation.withDefaultNamespace(name);
        final MapColor color = BuiltInRegistries.BLOCK.containsKey(blockId) ? BuiltInRegistries.BLOCK.get(blockId).defaultMapColor() : MapColor.COLOR_ORANGE;
        definePot(registry, name + "_botany_pot", new BotanyPotBlock(color, PotType.BASIC));
        definePot(registry, name + "_hopper_botany_pot", new BotanyPotBlock(color, PotType.HOPPER));
        definePot(registry, name + "_waxed_botany_pot", new BotanyPotBlock(color, PotType.WAXED));
    }

    private void definePot(BlockRegistryAdapter registry, String id, Block block) {
        final ResourceLocation blockId = ResourceLocation.fromNamespaceAndPath(this.namespace(), id);
        registry.addPlaceable(id, () -> block);
        allPotBlocks.put(blockId, block);
    }

    @Override
    public void defineBlockEntities(GameRegistryAdapter<BlockEntityType<?>> registry) {
        registry.add("botany_pot", Services.GAMEPLAY.blockEntityBuilder(BotanyPotBlockEntity::new, this.allPotBlocks.values().toArray(Block[]::new)).build(null));
        BotanyPotsPlugin.PLUGINS.get().forEach(BotanyPotsPlugin::registerDisplayTypes);
        BotanyPotsPlugin.PLUGINS.get().forEach(BotanyPotsPlugin::registerDropProviders);
        BotanyPotsPlugin.PLUGINS.get().forEach(BotanyPotsPlugin::registerGrowthAmountTypes);
    }

    @Override
    public void defineMenuType(MenuTypeAdapter registry) {
        registry.add("basic_pot_menu", BotanyPotMenu::basicMenuClient);
        registry.add("hopper_pot_menu", BotanyPotMenu::hopperMenuClient);
    }

    @Override
    public void defineRecipeTypes(RecipeTypeAdapter registry) {
        registry.add("soil");
        registry.add("crop");
        registry.add("pot_interaction");
        registry.add("fertilizer");
    }

    @Override
    public void defineRecipeSerializers(GameRegistryAdapter<RecipeSerializer<?>> registry) {
        registry.add("soil", BasicSoil.SERIALIZER);
        registry.add("block_derived_soil", BlockDerivedSoil.SERIALIZER);
        registry.add("crop", BasicCrop.SERIALIZER);
        registry.add("block_derived_crop", BlockDerivedCrop.SERIALIZER);
        registry.add("pot_interaction", BasicPotInteraction.SERIALIZER);
        registry.add("fertilizer", BasicFertilizer.SERIALIZER);
    }

    @Override
    public void defineLoadConditions(GenericRegistryAdapter<MapCodec<? extends ILoadCondition>> registry) {
        registry.add(ConfigLoadCondition.TYPE_ID, ConfigLoadCondition.CODEC);
    }

    @Override
    public void defineCreativeTabs(CreativeModeTabAdapter registry) {
        registry.add("tab", TAB_ICON, (params, builder) -> {
            for (Block block : this.allPotBlocks.values()) {
                builder.accept(block.asItem());
            }
            final float[] buffs = {0.25f, 0.5f, 0.75f, 1f, 5f, 10f, 15f, 50f, 100f, 1000f};
            final ResourceLocation yieldId = BotanyPotsMod.id("test_yield");
            final ResourceLocation growthId = BotanyPotsMod.id("test_growth");
            for (float buff : buffs) {
                final ItemStack yield = new ItemStack(Items.DIAMOND_HOE);
                Helpers.addModifier(yield, Helpers.YIELD_MOD_ATTRIBUTE.get(), new AttributeModifier(yieldId, buff, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
                yield.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
                builder.accept(yield);
            }
            for (float buff : buffs) {
                final ItemStack growth = new ItemStack(Items.GOLDEN_HOE);
                Helpers.addModifier(growth, Helpers.GROWTH_MOD_ATTRIBUTE.get(), new AttributeModifier(growthId, buff, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
                growth.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
                builder.accept(growth);
            }
            for (float buff : buffs) {
                final ItemStack both = new ItemStack(Items.NETHERITE_HOE);
                Helpers.addModifier(both, Helpers.YIELD_MOD_ATTRIBUTE.get(), new AttributeModifier(yieldId, buff, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
                Helpers.addModifier(both, Helpers.GROWTH_MOD_ATTRIBUTE.get(), new AttributeModifier(growthId, buff, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
                both.set(DataComponents.UNBREAKABLE, new Unbreakable(true));
                builder.accept(both);
            }
        });
    }

    @Override
    public void defineCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        BotanyPotsCommands.build(dispatcher, context, selection);
    }

    @Override
    public void defineBlockRenderers(BlockEntityRendererAdapter registry) {
        registry.bind(BotanyPotBlockEntity.TYPE.get(), BotanyPotRenderer::new);
        BotanyPotsPlugin.PLUGINS.get().forEach(BotanyPotsPlugin::bindDisplayRenderers);
    }

    @Override
    public void defineItemComponents(GameRegistryAdapter<DataComponentType<?>> registry) {
        registry.add(CropOverride.TYPE_ID.getPath(), new DataComponentType.Builder<CropOverride>().persistent(CropOverride.CODEC).networkSynchronized(CropOverride.STREAM).build());
        registry.add(SoilOverride.TYPE_ID.getPath(), new DataComponentType.Builder<SoilOverride>().persistent(SoilOverride.CODEC).networkSynchronized(SoilOverride.STREAM).build());
    }

    @Override
    public void defineMenuScreens(MenuScreenAdapter registry) {
        registry.bind(BotanyPotMenu.BASIC_MENU.get(), BotanyPotScreen::new);
        registry.bind(BotanyPotMenu.HOPPER_MENU.get(), BotanyPotScreen::new);
    }

    @Override
    public void defineBlockRenderTypes(BlockRenderTypeAdapter registry) {
        for (Block block : this.allPotBlocks.values()) {
            registry.add(block, RenderType.cutout());
        }
    }

    @Override
    public void defineAttributes(GameRegistryAdapter<Attribute> registry) {
        // TODO Write a mixin to display these as a percentage in tooltips
        registry.add("growth", new RangedAttribute("attribute.botanypots.growth", 0f, -Float.MAX_VALUE, Float.MAX_VALUE));
        registry.add("yield", new RangedAttribute("attribute.botanypots.yield", 0f, -Float.MAX_VALUE, Float.MAX_VALUE));
    }

    @Override
    public String namespace() {
        return BotanyPotsMod.MOD_ID;
    }

    public static Component modMessage(Component component) {
        return Component.translatable("commands.botanypots.mod_message", component);
    }
}
