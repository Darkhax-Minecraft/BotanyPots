package net.darkhax.botanypots.soil;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.google.gson.JsonObject;

import net.darkhax.bookshelf.serialization.Serializers;
import net.darkhax.bookshelf.util.PacketUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SoilSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SoilInfo> {
    
    public static final SoilSerializer INSTANCE = new SoilSerializer();
    
    @Override
    public SoilInfo read (ResourceLocation id, JsonObject json) {
        
        final Ingredient input = Ingredient.deserialize(json.getAsJsonObject("input"));
        final BlockState renderState = Serializers.BLOCK_STATE.read(json.get("display"));
        final float growthModifier = JSONUtils.getFloat(json, "growthModifier");
        final Set<String> categories = Serializers.STRING.readSet(json, "categories");
        final Optional<Integer> lightLevel = Serializers.INT.readOptional(json, "lightLevel");
        
        if (growthModifier <= -1) {
            
            throw new IllegalArgumentException("Soil " + id + " has an invalid growth modifier. It must be greater than -1.");
        }
        
        if (lightLevel.isPresent()) {
            
            final int lightVal = lightLevel.get();
            
            if (lightVal > 15 || lightVal < 0) {
                
                throw new IllegalArgumentException("Light level should be between 0 and 15. Value was " + lightLevel.get());
            }
        }
        
        return new SoilInfo(id, input, renderState, growthModifier, categories, lightLevel);
    }
    
    @Override
    public SoilInfo read (ResourceLocation id, PacketBuffer buf) {
        
        final Ingredient ingredient = Ingredient.read(buf);
        final BlockState renderState = PacketUtils.deserializeBlockState(buf);
        final float growthModifier = buf.readFloat();
        final Set<String> categories = new HashSet<>();
        PacketUtils.deserializeStringCollection(buf, categories);
        final Optional<Integer> lightLevel = Serializers.INT.readOptional(buf);
        
        return new SoilInfo(id, ingredient, renderState, growthModifier, categories, lightLevel);
    }
    
    @Override
    public void write (PacketBuffer buffer, SoilInfo info) {
        
        info.getIngredient().write(buffer);
        PacketUtils.serializeBlockState(buffer, info.getRenderState());
        buffer.writeFloat(info.getGrowthModifier());
        PacketUtils.serializeStringCollection(buffer, info.getCategories());
        Serializers.INT.writeOptional(buffer, info.getLightLevel());
    }
}