package net.darkhax.botanypots.common.impl;

import net.darkhax.botanypots.common.api.BotanyPotsPlugin;
import net.darkhax.botanypots.common.api.command.generator.DataHelper;
import net.darkhax.botanypots.common.api.command.generator.soil.SoilGenerator;
import net.darkhax.botanypots.common.api.data.display.render.DisplayRenderer;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.api.data.growthamount.GrowthAmountType;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProviderType;
import net.darkhax.botanypots.common.impl.command.generator.TaggedSoilGenerator;
import net.darkhax.botanypots.common.impl.data.display.renderer.EntityDisplayStateRenderer;
import net.darkhax.botanypots.common.impl.data.display.renderer.PhasedDisplayStateRenderer;
import net.darkhax.botanypots.common.impl.data.display.renderer.SimpleDisplayStateRenderer;
import net.darkhax.botanypots.common.impl.data.display.renderer.TexturedCubeStateRenderer;
import net.darkhax.botanypots.common.impl.data.display.types.AgingDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.EntityDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.SimpleDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.TexturedCubeDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.TransitionalDisplayState;
import net.darkhax.botanypots.common.impl.data.growthamount.ConstantGrowthAmount;
import net.darkhax.botanypots.common.impl.data.growthamount.PercentageGrowthAmount;
import net.darkhax.botanypots.common.impl.data.growthamount.RangedGrowthAmount;
import net.darkhax.botanypots.common.impl.data.itemdrops.BlockDrops;
import net.darkhax.botanypots.common.impl.data.itemdrops.BlockStateDrops;
import net.darkhax.botanypots.common.impl.data.itemdrops.EntityDrops;
import net.darkhax.botanypots.common.impl.data.itemdrops.LootTableDrops;
import net.darkhax.botanypots.common.impl.data.itemdrops.SimpleDropProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;

public class BaseBotanyPotsPlugin implements BotanyPotsPlugin {

    @Override
    public void registerSoilGenerators(BiConsumer<ResourceLocation, SoilGenerator> register) {
        register.accept(BotanyPotsMod.id("water"), new TaggedSoilGenerator("botanypots:soil/water", DataHelper.simpleDisplay(Blocks.WATER, true)));
        register.accept(BotanyPotsMod.id("lava"), new TaggedSoilGenerator("botanypots:soil/lava", DataHelper.simpleDisplay(Blocks.LAVA, true)));
        register.accept(BotanyPotsMod.id("snow"), new TaggedSoilGenerator("botanypots:soil/snow", DataHelper.simpleDisplay(Blocks.SNOW_BLOCK)));
    }

    @Override
    public void registerDisplayTypes() {
        DisplayType.register(SimpleDisplayState.TYPE_ID, SimpleDisplayState.CODEC, SimpleDisplayState.STREAM);
        DisplayType.register(TransitionalDisplayState.TYPE_ID, TransitionalDisplayState.CODEC, TransitionalDisplayState.STREAM);
        DisplayType.register(AgingDisplayState.TYPE_ID, AgingDisplayState.CODEC, AgingDisplayState.STREAM);
        DisplayType.register(EntityDisplayState.TYPE_ID, EntityDisplayState.CODEC, EntityDisplayState.STREAM);
        DisplayType.register(TexturedCubeDisplayState.TYPE_ID, TexturedCubeDisplayState.CODEC, TexturedCubeDisplayState.STREAM);
    }

    @Override
    public void bindDisplayRenderers() {
        DisplayRenderer.bind(SimpleDisplayState.TYPE.get(), SimpleDisplayStateRenderer.RENDERER);
        DisplayRenderer.bind(TransitionalDisplayState.TYPE.get(), PhasedDisplayStateRenderer.TRANSITIONAL);
        DisplayRenderer.bind(AgingDisplayState.TYPE.get(), PhasedDisplayStateRenderer.AGING);
        DisplayRenderer.bind(EntityDisplayState.TYPE.get(), EntityDisplayStateRenderer.RENDERER);
        DisplayRenderer.bind(TexturedCubeDisplayState.TYPE.get(), TexturedCubeStateRenderer.RENDERER);
    }

    @Override
    public void registerDropProviders() {
        ItemDropProviderType.register(BotanyPotsMod.id("loot_table"), LootTableDrops.CODEC, LootTableDrops.STREAM);
        ItemDropProviderType.register(BotanyPotsMod.id("items"), SimpleDropProvider.CODEC, SimpleDropProvider.STREAM);
        ItemDropProviderType.register(BotanyPotsMod.id("block"), BlockDrops.CODEC, BlockDrops.STREAM);
        ItemDropProviderType.register(BotanyPotsMod.id("block_state"), BlockStateDrops.CODEC, BlockStateDrops.STREAM);
        ItemDropProviderType.register(BotanyPotsMod.id("entity"), EntityDrops.CODEC, EntityDrops.STREAM);
    }

    @Override
    public void registerGrowthAmountTypes() {
        GrowthAmountType.register(ConstantGrowthAmount.ID, ConstantGrowthAmount.CODEC, ConstantGrowthAmount.STREAM);
        GrowthAmountType.register(PercentageGrowthAmount.ID, PercentageGrowthAmount.CODEC, PercentageGrowthAmount.STREAM);
        GrowthAmountType.register(RangedGrowthAmount.ID, RangedGrowthAmount.CODEC, RangedGrowthAmount.STREAM);
    }
}