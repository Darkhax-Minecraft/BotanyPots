package net.darkhax.botanypots;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class PotStateProvider extends BlockStateProvider {

    public PotStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, BotanyPots.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.generatePots(BotanyPots.MOD_ID + ":%sbotany_pot", new ResourceLocation(BotanyPots.MOD_ID, "botany_pot_base"), "terracotta", "%sterracotta");
        this.generatePots(BotanyPots.MOD_ID + ":hopper_%sbotany_pot", new ResourceLocation(BotanyPots.MOD_ID, "hopper_botany_pot_base"), "terracotta", "%sterracotta");
    }

    private void generatePots(String formatString, ResourceLocation parent, String textureKey, String formatTexture) {
        for(final DyeColor color : DyeColor.values()) {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(String.format(formatString, color.getString() + "_")));
            ModelFile file = this.models().singleTexture(block.getRegistryName().toString(), extendWithFolder(parent), textureKey, extendWithFolder(new ResourceLocation(String.format(formatTexture, color.getString() + "_"))));
            this.simpleBlock(block, file);
            this.simpleBlockItem(block, file);
        }
        Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(String.format(formatString, "")));
        ModelFile file = this.models().singleTexture(block.getRegistryName().toString(), extendWithFolder(parent), textureKey, extendWithFolder(new ResourceLocation(String.format(formatTexture, ""))));
        this.simpleBlock(block, file);
        this.simpleBlockItem(block, file);
    }

    private ResourceLocation extendWithFolder(ResourceLocation loc) {
        return new ResourceLocation(loc.getNamespace(), ModelProvider.BLOCK_FOLDER + "/" + loc.getPath());
    }
}
