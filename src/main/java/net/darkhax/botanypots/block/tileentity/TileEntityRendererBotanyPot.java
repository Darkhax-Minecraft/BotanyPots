package net.darkhax.botanypots.block.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntityRendererBotanyPot extends TileEntityRenderer<TileEntityBotanyPot> {
    
    public TileEntityRendererBotanyPot(TileEntityRendererDispatcher dispatcher) {
        
        super(dispatcher);
    }
    
    @Override
    public void render (TileEntityBotanyPot tile, float partial, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        
        final Minecraft mc = Minecraft.getInstance();
        
        if (tile.getSoil() != null) {
            
            matrix.push();
            matrix.scale(0.625f, 0.384f, 0.625f);
            matrix.translate(0.3, 0.01, 0.3);
            mc.getBlockRendererDispatcher().renderBlock(tile.getSoil().getRenderState(), matrix, buffer, light, overlay);
            matrix.pop();
            
            if (tile.getCrop() != null) {
                
                final float growth = MathHelper.clamp((tile.getCurrentGrowthTicks() + partial) / tile.getTotalGrowthTicks() * (10 / 16f), 0f, 1f);
                matrix.push();
                matrix.translate(0.5, 0.40, 0.5);
                matrix.scale(growth, growth, growth);
                matrix.translate(-0.5, 0, -0.5);
                mc.getBlockRendererDispatcher().renderBlock(tile.getCrop().getDisplayState(), matrix, buffer, light, overlay);
                matrix.pop();
            }
        }
    }
}