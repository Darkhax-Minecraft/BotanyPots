package net.darkhax.botanypots;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Constants {

    public static final String MOD_ID = "botanypots";
    public static final String MOD_NAME = "Botany Pots";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final Random RANDOM = new Random();

    public static ResourceLocation id(String id) {

        return new ResourceLocation(MOD_ID, id);
    }
}