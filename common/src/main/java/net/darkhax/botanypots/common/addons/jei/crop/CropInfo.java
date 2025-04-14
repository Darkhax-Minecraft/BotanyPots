package net.darkhax.botanypots.common.addons.jei.crop;

import net.darkhax.botanypots.common.api.data.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.context.DisplayContext;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;

public final class CropInfo {

    private final BotanyPotContext context;
    private final ResourceLocation id;
    private final Ingredient seed;
    private final Ingredient soil;
    private final int growthTime;
    private final List<ItemDropProvider> drops;
    private final List<ItemStack> inventory = new ArrayList<>(15);

    public CropInfo(ResourceLocation id, Ingredient seed, Ingredient soil, int growthTime, List<ItemDropProvider> drops) {
        this.context = new DisplayContext(this.inventory);
        this.id = id;
        this.seed = seed;
        this.soil = soil;
        this.growthTime = growthTime;
        this.drops = drops;
    }

    public BotanyPotContext context() {
        return context;
    }

    public ResourceLocation id() {
        return id;
    }

    public Ingredient seed() {
        return seed;
    }

    public Ingredient soil() {
        return soil;
    }

    public int growthTime() {
        return growthTime;
    }

    public List<ItemDropProvider> drops() {
        return drops;
    }

    public List<ItemStack> inventory() {
        return this.inventory;
    }
}