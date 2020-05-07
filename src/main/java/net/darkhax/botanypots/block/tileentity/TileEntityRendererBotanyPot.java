package net.darkhax.botanypots.block.tileentity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ILightReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
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
                
                final float growth = MathHelper.clamp((float) tile.getCurrentGrowthTicks() / tile.getTotalGrowthTicks() * (10 / 16f), 0, 1f);
                matrix.push();
                matrix.translate(0.5, 0.40, 0.5);
                matrix.scale(growth, growth, growth);
                matrix.translate(-0.5, 0, -0.5);
                
                final BlockState[] cropStates = tile.getCrop().getDisplayState();
                
                for (int i = 0; i < cropStates.length; i++) {
                    
                    matrix.translate(0, i, 0);
                    this.renderBlock(cropStates[i], tile.getWorld(), tile.getPos(), matrix, buffer);
                    matrix.translate(0, -i, 0);
                }
                
                matrix.pop();
            }
        }
    }
    
    private void renderBlock (BlockState state, World world, BlockPos pos, MatrixStack matrix, IRenderTypeBuffer buffer) {
        
        final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        final IBakedModel model = dispatcher.getModelForState(state);
        final boolean useAO = Minecraft.isAmbientOcclusionEnabled() && state.getLightValue(world, pos) == 0 && model.isAmbientOcclusion();
        
        final RenderType type = RenderTypeLookup.getRenderType(state);
        
        if (type != null) {
            
            ForgeHooksClient.setRenderLayer(type);
            
            final IVertexBuilder builder = buffer.getBuffer(type);
            this.renderModel(dispatcher.getBlockModelRenderer(), useAO, world, model, state, pos, matrix, builder, false, OverlayTexture.NO_OVERLAY);
            
            ForgeHooksClient.setRenderLayer(null);
        }
    }
    
    public boolean renderModel (BlockModelRenderer renderer, boolean useAO, ILightReader world, IBakedModel model, BlockState state, BlockPos pos, MatrixStack matrix, IVertexBuilder buffer, boolean checkSides, int combinedOverlayIn) {
        
        try {
            
            final IModelData modelData = model.getModelData(world, pos, state, EmptyModelData.INSTANCE);
            return useAO ? renderer.renderModelSmooth(world, model, state, pos, matrix, buffer, checkSides, RANDOM, 0L, combinedOverlayIn, modelData) : renderer.renderModelFlat(world, model, state, pos, matrix, buffer, checkSides, RANDOM, 0L, combinedOverlayIn, modelData);
        }
        
        catch (final Throwable throwable) {
            
            final CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
            final CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state);
            crashreportcategory.addDetail("Using AO", useAO);
            throw new ReportedException(crashreport);
        }
    }
}