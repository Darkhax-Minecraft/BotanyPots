package net.darkhax.botanypots;

import java.util.Collection;
import java.util.function.Supplier;

import com.mojang.datafixers.types.Type;

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
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.registry.Registry;

public class Content {
    
    public final IRecipeType<SoilInfo> recipeTypeSoil;
    public final IRecipeSerializer<?> recipeSerializerSoil;
    
    public final IRecipeType<CropInfo> recipeTypeCrop;
    public final IRecipeSerializer<?> recipeSerializerCrop;
    
    public final IRecipeType<FertilizerInfo> recipeTypeFertilizer;
    public final IRecipeSerializer<?> recipeSerializerFertilizer;
    
    public final TileEntityType<TileEntityBotanyPot> tileBotanyPot;
    
    public final Block basicBotanyPot;
    public final Block hopperBotanyPot;
    
    public Content(RegistryHelper registry) {
        
        // Recipe types
        this.recipeTypeSoil = register("soil");
        this.recipeSerializerSoil = registry.recipeSerializers.register(SoilSerializer.INSTANCE, "soil");
        
        this.recipeTypeCrop = register("crop");
        this.recipeSerializerCrop = registry.recipeSerializers.register(CropSerializer.INSTANCE, "crop");
        
        this.recipeTypeFertilizer = register("fertilizer");
        this.recipeSerializerFertilizer = registry.recipeSerializers.register(FertilizerSerializer.INSTANCE, "fertilizer");
        
        // Blocks
        this.basicBotanyPot = registry.blocks.register(new BlockBotanyPot(), "botany_pot");
        this.hopperBotanyPot = registry.blocks.register(new BlockBotanyPot(true), "hopper_botany_pot");
        
        for (final DyeColor dyeColor : DyeColor.values()) {
            
            registry.blocks.register(new BlockBotanyPot(), dyeColor.getString() + "_botany_pot");
            registry.blocks.register(new BlockBotanyPot(true), "hopper_" + dyeColor.getString() + "_botany_pot");
        }
        
        // Tile Entity
        this.tileBotanyPot = register("botany_pot", TileEntityBotanyPot::new, BlockBotanyPot.botanyPots);
        registry.tileEntities.register(this.tileBotanyPot, "botany_pot");
    }
    
    @Deprecated // Hopefully forge will do something with this.
    static <T extends IRecipe<?>> IRecipeType<T> register (final String key) {
        
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(key), new IRecipeType<T>() {
            @Override
            public String toString () {
                
                return key;
            }
        });
    }
    
    @Deprecated // Hopefully forge will do something with this.
    private static <T extends TileEntity> TileEntityType<T> register (String key, Supplier<? extends T> factoryIn, Collection<Block> validBlocks) {
        
        final Type<?> type = Util.attemptDataFix(TypeReferences.BLOCK_ENTITY, key);
        final TileEntityType.Builder<T> builder = TileEntityType.Builder.create(factoryIn, validBlocks.toArray(new Block[0]));
        return builder.build(type);
    }
}