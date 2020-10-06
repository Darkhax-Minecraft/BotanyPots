package net.darkhax.botanypots.data;

import net.darkhax.botanypots.BotanyPots;
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

    public static void register(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        Registry.register(Registry.RECIPE_TYPE, SOIL, SOIL_INFO_TYPE);
        event.getRegistry().register(SOIL_INFO_SERIALIZER.setRegistryName(SOIL));
    }
}
