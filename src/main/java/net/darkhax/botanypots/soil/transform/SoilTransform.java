package net.darkhax.botanypots.soil.transform;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.darkhax.bookshelf.item.crafting.RecipeDataBase;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.soil.SoilInfo;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class SoilTransform extends RecipeDataBase {
    
    private final ResourceLocation id;
    private final Ingredient item;
    private final SoilInfo[] inputs;
    private final SoilInfo output;
    
    public SoilTransform(ResourceLocation id, Ingredient item, SoilInfo[] inputs, SoilInfo output) {
        
        this.id = id;
        this.item = item;
        this.inputs = inputs;
        this.output = output;
    }
    
    @Override
    public ResourceLocation getId () {
        
        return this.id;
    }
    
    @Override
    public IRecipeSerializer<?> getSerializer () {
        
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public IRecipeType<?> getType () {
        
        // TODO Auto-generated method stub
        return null;
    }
    
    static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<SoilTransform> {
        
        @Override
        public SoilTransform read (ResourceLocation recipeId, JsonObject json) {
            
            final Ingredient item = Ingredient.deserialize(json.get("item"));
            
            final JsonElement inputElement = json.get("inputSoil");
            final SoilInfo[] inputs;
            
            return null;
        }
        
        private SoilInfo getSoil (JsonElement json) {
            
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                
                final SoilInfo soil = BotanyPotHelper.getSoil(ResourceLocation.tryCreate(json.getAsString()));
                
                if (soil == null) {
                    
                    throw new JsonSyntaxException("No soil found for ID " + json.getAsString());
                }
                
                return soil;
            }
            
            throw new JsonSyntaxException("Could not read " + json.toString() + ". Expected a string.");
        }
        
        private SoilInfo[] readSoils (JsonObject json) {
            
            final List<SoilInfo> soils = NonNullList.create();
            
            if (json.has("inputSoil")) {
                
                soils.add(this.getSoil(json.get("inputSoil")));
            }
            
            if (json.has("inputSoils")) {
                
                final JsonElement inputSoils = json.get("inputSoils");
                
                if (inputSoils.isJsonArray()) {
                    
                    for (final JsonElement element : inputSoils.getAsJsonArray()) {
                        
                        soils.add(this.getSoil(element));
                    }
                }
                
                else {
                    
                    throw new JsonSyntaxException("Expected inputSoils to be an array.");
                }
            }
            
            return soils.toArray(new SoilInfo[0]);
        }
        
        @Override
        public SoilTransform read (ResourceLocation recipeId, PacketBuffer buffer) {
            
            return null;
        }
        
        @Override
        public void write (PacketBuffer buffer, SoilTransform recipe) {
            
        }
    }
}
