package net.darkhax.botanypots;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityRendererBotanyPot extends TileEntityRenderer<TileEntityBotanyPot> {
    
    @Override
    public void render (TileEntityBotanyPot tile, double x, double y, double z, float partialTicks, int destroyStage) {
        
    }
}