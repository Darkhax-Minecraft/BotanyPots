package net.darkhax.botanypots;

import java.util.ArrayList;
import java.util.List;

import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.botanypots.block.BlockBotanyPot;
import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.darkhax.botanypots.crop.CropInfo;
import net.darkhax.botanypots.crop.CropSerializer;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.fertilizer.FertilizerSerializer;
import net.darkhax.botanypots.soil.SoilInfo;
import net.darkhax.botanypots.soil.SoilSerializer;
import net.minecraft.block.Block;
import net.minecraft.item.DyeColor;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntityType;

public class Content {
    
    private final IRecipeType<SoilInfo> recipeTypeSoil;
    private final IRecipeSerializer<SoilInfo> recipeSerializerSoil;
    
    private final IRecipeType<CropInfo> recipeTypeCrop;
    private final IRecipeSerializer<CropInfo> recipeSerializerCrop;
    
    private final IRecipeType<FertilizerInfo> recipeTypeFertilizer;
    private final IRecipeSerializer<FertilizerInfo> recipeSerializerFertilizer;
    
    /**
     * The tile entity type for all botany pots. They share the same tile entity type. Keep in
     * mind that in modern versions of Minecraft a tile entity can only be associated with a
     * predefined list of blocks. If you want to make your own type of botany pot you will need
     * to hack it into the original type's list of blocks somehow, or make your own tile entity
     * type.
     */
    private final TileEntityType<TileEntityBotanyPot> tileBotanyPot;
    
    /**
     * The basic botany pot with no fancy colors or upgrades. This is primarily stored so it
     * can be referenced later on as an icon.
     */
    private final Block basicBotanyPotBlock;
    
    /**
     * A list of all the known botany pot blocks.
     */
    private final List<Block> botanyPots = new ArrayList<>();
    
    public Content(RegistryHelper registry) {
        
        // Recipe types
        this.recipeTypeSoil = registry.registerRecipeType("soil");
        this.recipeSerializerSoil = registry.registerRecipeSerializer(SoilSerializer.INSTANCE, "soil");
        
        this.recipeTypeCrop = registry.registerRecipeType("crop");
        this.recipeSerializerCrop = registry.registerRecipeSerializer(CropSerializer.INSTANCE, "crop");
        
        this.recipeTypeFertilizer = registry.registerRecipeType("fertilizer");
        this.recipeSerializerFertilizer = registry.registerRecipeSerializer(FertilizerSerializer.INSTANCE, "fertilizer");
        
        // Normal Botany Pots
        this.basicBotanyPotBlock = registry.registerBlock(new BlockBotanyPot(), "botany_pot");
        this.botanyPots.add(this.basicBotanyPotBlock);
        
        for (final DyeColor dyeColor : DyeColor.values()) {
            
            this.botanyPots.add(registry.registerBlock(new BlockBotanyPot(), dyeColor.getName() + "_botany_pot"));
        }
        
        // Hopper Botany Pots
        this.botanyPots.add(registry.registerBlock(new BlockBotanyPot(true), "hopper_botany_pot"));
        
        for (final DyeColor dyeColor : DyeColor.values()) {
            
            this.botanyPots.add(registry.registerBlock(new BlockBotanyPot(true), "hopper_" + dyeColor.getName() + "_botany_pot"));
        }
        
        // Tile Entity
        this.tileBotanyPot = registry.registerTileEntity(TileEntityBotanyPot::new, "botany_pot", this.botanyPots.toArray(new Block[0]));
    }
    
    public TileEntityType<TileEntityBotanyPot> getPotTileType () {
        
        return this.tileBotanyPot;
    }
    
    public Block getBasicBotanyPot () {
        
        return this.basicBotanyPotBlock;
    }
    
    public IRecipeType<SoilInfo> getRecipeTypeSoil () {
        
        return this.recipeTypeSoil;
    }
    
    public IRecipeSerializer<SoilInfo> getRecipeSerializerSoil () {
        
        return this.recipeSerializerSoil;
    }
    
    public IRecipeType<FertilizerInfo> getRecipeTypeFertilizer () {
        
        return this.recipeTypeFertilizer;
    }
    
    public IRecipeSerializer<FertilizerInfo> getRecipeSerializerFertilizer () {
        
        return this.recipeSerializerFertilizer;
    }
    
    public IRecipeType<CropInfo> getRecipeTypeCrop () {
        
        return this.recipeTypeCrop;
    }
    
    public IRecipeSerializer<CropInfo> getRecipeSerializerCrop () {
        
        return this.recipeSerializerCrop;
    }
    
    public List<Block> getBotanyPotBlocks () {
        
        return this.botanyPots;
    }
}