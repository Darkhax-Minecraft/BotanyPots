package net.darkhax.botanypots.block.tileentity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;

@OnlyIn(Dist.CLIENT)
public class TileEntityRendererBotanyPot extends TileEntityRenderer<TileEntityBotanyPot> {
    
    private static final Random RANDOM = new Random();
    
    public TileEntityRendererBotanyPot(TileEntityRendererDispatcher dispatcher) {
        
        super(dispatcher);
    }
    
    @Override
    public void render (TileEntityBotanyPot tile, float partial, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        
        if (tile.getSoil() != null) {
            
            matrix.push();
            matrix.scale(0.625f, 0.384f, 0.625f);
            matrix.translate(0.3, 0.01, 0.3);
            this.renderBlock(tile.getSoil().getRenderState(), tile.getWorld(), tile.getPos(), matrix, buffer);
            matrix.pop();
            
            if (tile.getCrop() != null) {
                
                final float growth = MathHelper.clamp((tile.getCurrentGrowthTicks() + partial) / tile.getTotalGrowthTicks() * (10 / 16f), 0f, 1f);
                matrix.push();
                matrix.translate(0.5, 0.40, 0.5);
                matrix.scale(growth, growth, growth);
                matrix.translate(-0.5, 0, -0.5);
                this.renderBlock(tile.getCrop().getDisplayState(), tile.getWorld(), tile.getPos(), matrix, buffer);
                matrix.pop();
            }
        }
    }
    
    private void renderBlock (BlockState state, World world, BlockPos pos, MatrixStack matrix, IRenderTypeBuffer buffer) {
        
        final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        final IBakedModel model = dispatcher.getModelForState(state);
        final IModelData data = model.getModelData(world, pos, state, ModelDataManager.getModelData(world, pos));
        
        for (final RenderType type : RenderType.getBlockRenderTypes()) {
            
            final IVertexBuilder builder = buffer.getBuffer(type);
            
            if (RenderTypeLookup.canRenderInLayer(state, type)) {
                
                ForgeHooksClient.setRenderLayer(type);
                dispatcher.getBlockModelRenderer().renderModel(world, model, state, pos, matrix, builder, false, RANDOM, state.getPositionRandom(pos), OverlayTexture.NO_OVERLAY, data);
            }
        }
        
        ForgeHooksClient.setRenderLayer(null);
    }
}