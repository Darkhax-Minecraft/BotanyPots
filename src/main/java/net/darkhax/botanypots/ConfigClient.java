package net.darkhax.botanypots;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;

public class ConfigClient {
    
    private final ForgeConfigSpec spec;
    
    private final BooleanValue growthAnimation;
    private final BooleanValue renderSoil;
    private final BooleanValue renderCrop;
    private final BooleanValue breakEffects;
    private final DoubleValue renderDistance;
    
    public ConfigClient() {
        
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        
        // General Configs
        builder.comment("Client side configurations for the mod. Modpacks should not ship non-default versions of this file!");
        builder.push("client");
        
        builder.comment("Whether or not the growth animation should be played.");
        this.growthAnimation = builder.define("useGrowthAnimation", true);
        
        builder.comment("Whether or not the soil in pots should be rendered.");
        this.renderSoil = builder.define("renderSoil", true);
        
        builder.comment("Whether or not crops should render in pots.");
        this.renderCrop = builder.define("renderCrop", true);
        
        builder.comment("Whether or not break effects should occur when harvested.");
        this.breakEffects = builder.define("breakEffects", true);
        
        builder.comment("The render distance of a botany pot.");
        this.renderDistance = builder.defineInRange("renderDistance", 64d, 1d, 4096d);
        
        builder.pop();
        this.spec = builder.build();
    }
    
    public boolean shouldDoGrowthAnimation () {
        
        return this.growthAnimation.get();
    }
    
    public boolean shouldRenderSoil () {
        
        return this.renderSoil.get();
    }
    
    public boolean shouldRenderCrop () {
        
        return this.renderCrop.get();
    }
    
    public boolean shouldDoBreakEffects () {
        
        return this.breakEffects.get();
    }
    
    public double getRenderDistance () {
        
        return this.renderDistance.get();
    }
    
    public ForgeConfigSpec getSpec () {
        
        return this.spec;
    }
}