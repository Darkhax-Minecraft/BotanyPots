package net.darkhax.botanypots.fertilizer;

import com.google.gson.JsonObject;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FertilizerSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FertilizerInfo> {
    
    public static final FertilizerSerializer INSTANCE = new FertilizerSerializer();
    
    @Override
    public FertilizerInfo read (ResourceLocation id, JsonObject json) {
        
        final Ingredient ingredient = Ingredient.deserialize(json.getAsJsonObject("fertilizer"));
        final int minTicks = JSONUtils.getInt(json, "minTicks");
        final int maxTicks = JSONUtils.getInt(json, "maxTicks");
        
        return new FertilizerInfo(id, ingredient, minTicks, maxTicks);
    }
    
    @Override
    public FertilizerInfo read (ResourceLocation id, PacketBuffer buf) {
        
        final Ingredient ingredient = Ingredient.read(buf);
        final int minTicks = buf.readInt();
        final int maxTicks = buf.readInt();
        
        return new FertilizerInfo(id, ingredient, minTicks, maxTicks);
    }
    
    @Override
    public void write (PacketBuffer buffer, FertilizerInfo info) {
        
        info.getIngredient().write(buffer);
        buffer.writeInt(info.getMinTicks());
        buffer.writeInt(info.getMaxTicks());
    }
}