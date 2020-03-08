package net.darkhax.botanypots.soil;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.darkhax.bookshelf.util.MCJsonUtils;
import net.darkhax.botanypots.PacketUtils;
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
        final BlockState renderState = MCJsonUtils.deserializeBlockState(json.getAsJsonObject("display"));
        final int tickRate = JSONUtils.getInt(json, "ticks");
        final Set<String> categories = new HashSet<>();
        
        for (final JsonElement element : json.getAsJsonArray("categories")) {
            
            categories.add(element.getAsString().toLowerCase());
        }
        
        return new SoilInfo(id, input, renderState, tickRate, categories);
    }
    
    @Override
    public SoilInfo read (ResourceLocation id, PacketBuffer buf) {
        
        final Ingredient ingredient = Ingredient.read(buf);
        final BlockState renderState = PacketUtils.deserializeBlockState(buf);
        final float growthModifier = buf.readFloat();
        final Set<String> categories = new HashSet<>();
        PacketUtils.deserializeStringCollection(buf, categories);
        
        return new SoilInfo(id, ingredient, renderState, growthModifier, categories);
    }
    
    @Override
    public void write (PacketBuffer buffer, SoilInfo info) {
        
        info.getIngredient().write(buffer);
        PacketUtils.serializeBlockState(buffer, info.getRenderState());
        buffer.writeFloat(info.getGrowthModifier());
        PacketUtils.serializeStringCollection(buffer, info.getCategories());
    }
}