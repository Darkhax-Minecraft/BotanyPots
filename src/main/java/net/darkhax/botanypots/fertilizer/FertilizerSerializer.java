package net.darkhax.botanypots.fertilizer;

import java.math.BigInteger;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class FertilizerSerializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<FertilizerInfo> {
    
    public static final FertilizerSerializer INSTANCE = new FertilizerSerializer();
    
    @Override
    public FertilizerInfo read (ResourceLocation id, JsonObject json) {
        
        final Ingredient ingredient = Ingredient.deserialize(json.getAsJsonObject("fertilizer"));
        final int minTicks = readIntClamped(json, "minTicks", 0, Integer.MAX_VALUE, null);
        final int maxTicks = readIntClamped(json, "maxTicks", minTicks, Integer.MAX_VALUE, null);
        
        if (minTicks > maxTicks || maxTicks < minTicks) {
            
            throw new JsonSyntaxException("minTicks (" + minTicks + ") must be less than or equal to maxTicks (" + maxTicks + ").");
        }
        
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
    
    private static int readIntClamped (JsonObject json, String memberName, int min, int max, @Nullable Integer defaultValue) {
        
        if (json.has(memberName)) {
            
            final BigInteger bigMin = BigInteger.valueOf(min);
            final BigInteger bigMax = BigInteger.valueOf(max);
            final BigInteger bigInt = new BigInteger(json.get(memberName).getAsString());
            
            if (bigInt.compareTo(bigMin) < 0) {
                
                throw new JsonParseException("Property " + memberName + " is too small. Must be greater than or equal to " + min);
            }
            
            if (bigInt.compareTo(bigMax) > 0) {
                
                throw new JsonParseException("Property " + memberName + " is too big. Must be less than or equal to " + max);
            }
            
            try {
                
                return bigInt.intValueExact();
            }
            
            catch (final ArithmeticException e) {
                
                throw new JsonParseException("Expected property " + memberName + " to be an integer but it's value is out of range.", e);
            }
        }
        
        if (defaultValue != null) {
            
            return defaultValue;
        }
        
        throw new JsonParseException("Property " + memberName + " was undefined.");
    }
}