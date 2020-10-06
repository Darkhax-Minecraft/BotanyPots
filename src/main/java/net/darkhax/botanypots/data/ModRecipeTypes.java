package net.darkhax.botanypots.data;

import net.darkhax.botanypots.BotanyPots;
import net.darkhax.botanypots.fertilizer.FertilizerInfo;
import net.darkhax.botanypots.fertilizer.FertilizerSerializer;
import net.darkhax.botanypots.soil.SoilInfo;
import net.darkhax.botanypots.soil.SoilSerializer;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.RegistryEvent;

public class ModRecipeTypes {
    public static final ResourceLocation SOIL = new ResourceLocation(BotanyPots.MOD_ID, "soil");
    public static final IRecipeType<SoilInfo> SOIL_INFO_TYPE = IRecipeType.register(SOIL.toString());
    public static final IRecipeSerializer<SoilInfo> SOIL_INFO_SERIALIZER = SoilSerializer.INSTANCE;

    public static final ResourceLocation FERTILIZER = new ResourceLocation(BotanyPots.MOD_ID, "fertilizer");
    public static final IRecipeType<FertilizerInfo> FERTILIZER_TYPE = IRecipeType.register(FERTILIZER.toString());
    public static final IRecipeSerializer<FertilizerInfo> FERTILIZER_SERIALIZER = FertilizerSerializer.INSTANCE;

    public static void register(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        Registry.register(Registry.RECIPE_TYPE, SOIL, SOIL_INFO_TYPE);
        event.getRegistry().register(SOIL_INFO_SERIALIZER.setRegistryName(SOIL));

        Registry.register(Registry.RECIPE_TYPE, FERTILIZER, FERTILIZER_TYPE);
        event.getRegistry().register(FERTILIZER_SERIALIZER.setRegistryName(FERTILIZER));
    }
}
