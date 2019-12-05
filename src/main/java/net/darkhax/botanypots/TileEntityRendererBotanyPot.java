package net.darkhax.botanypots;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityRendererBotanyPot extends TileEntityRenderer<TileEntityBotanyPot> {
    
    @Override
    public void render (TileEntityBotanyPot tile, double x, double y, double z, float partialTicks, int destroyStage) {
        
        if (tile.getSoil() != null) {
            
            // TODO Render soil
            final BlockState soilRenderState = tile.getSoil().getRenderState();
            
            if (tile.getCrop() != null) {
                
                //TODO Render Crop
                final BlockState cropRenderState = tile.getCrop().getDisplayState();
                final float growthRate = (float) tile.getCurrentGrowthTicks() / tile.getTotalGrowthTicks();
            }
        }
    }
}