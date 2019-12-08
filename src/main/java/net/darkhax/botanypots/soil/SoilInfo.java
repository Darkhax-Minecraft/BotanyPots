package net.darkhax.botanypots.soil;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.darkhax.bookshelf.Bookshelf;
import net.darkhax.bookshelf.util.MCJsonUtils;
import net.darkhax.botanypots.PacketUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class SoilInfo {
    
    /**
     * The id of the soil entry.
     */
    private final ResourceLocation id;
    
    /**
     * The item used to get the soil into the pot.
     */
    private Ingredient ingredient;
    
    /**
     * The blockstate used to render the soil.
     */
    private BlockState renderState;
    
    /**
     * The base tick rate of the soil.
     */
    private int tickRate;
    
    /**
     * An array of associated soil categories.
     */
    private Set<String> categories;
    
    public SoilInfo(ResourceLocation id, Ingredient ingredient, BlockState renderState, int tickRate, Set<String> categories) {
        
        this.id = id;
        this.ingredient = ingredient;
        this.renderState = renderState;
        this.tickRate = tickRate;
        this.categories = categories;
    }
    
    public ResourceLocation getId () {
        
        return this.id;
    }
    
    public int getTickRate () {
        
        return this.tickRate;
    }
    
    public Ingredient getIngredient () {
        
        return this.ingredient;
    }
    
    public BlockState getRenderState () {
        
        return this.renderState;
    }
    
    public Set<String> getCategories () {
        
        return this.categories;
    }
    
    public static SoilInfo deserialize (ResourceLocation id, JsonObject json) {
        
        final Ingredient input = Ingredient.deserialize(json.getAsJsonObject("input"));
        final BlockState renderState = MCJsonUtils.deserializeBlockState(json.getAsJsonObject("display"));
        final int tickRate = JSONUtils.getInt(json, "ticks");
        final Set<String> categories = new HashSet<>();
        
        for (final JsonElement element : json.getAsJsonArray("categories")) {
            
            categories.add(element.getAsString().toLowerCase());
        }
        
        return new SoilInfo(id, input, renderState, tickRate, categories);
    }
    
    public static SoilInfo deserialize (PacketBuffer buf) {
        
        final ResourceLocation id = buf.readResourceLocation();
        final Ingredient ingredient = Ingredient.read(buf);
        final BlockState renderState = PacketUtils.deserializeBlockState(buf);
        final int tickRate = buf.readInt();
        final Set<String> categories = new HashSet<>();
        PacketUtils.deserializeStringCollection(buf, categories);
        
        return new SoilInfo(id, ingredient, renderState, tickRate, categories);
    }
    
    public static void serialize (PacketBuffer buffer, SoilInfo info) {
        
        buffer.writeResourceLocation(info.getId());
        info.getIngredient().write(buffer);
        PacketUtils.serializeBlockState(buffer, info.getRenderState());
        buffer.writeInt(info.getTickRate());
        PacketUtils.serializeStringCollection(buffer, info.getCategories());
    }
    
    public ItemStack getRandomSoilBlock () {
        
        final ItemStack[] matchingStacks = this.ingredient.getMatchingStacks();
        return matchingStacks.length > 0 ? matchingStacks[Bookshelf.RANDOM.nextInt(matchingStacks.length)] : ItemStack.EMPTY;
    }
    
    public void setIngredient (Ingredient ingredient) {
        
        this.ingredient = ingredient;
    }
    
    public void setRenderState (BlockState renderState) {
        
        this.renderState = renderState;
    }
    
    public void setTickRate (int tickRate) {
        
        this.tickRate = tickRate;
    }
    
    public void setCategories (Set<String> categories) {
        
        this.categories = categories;
    }
}