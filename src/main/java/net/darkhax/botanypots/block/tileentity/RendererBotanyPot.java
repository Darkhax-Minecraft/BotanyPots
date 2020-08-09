package net.darkhax.botanypots.block.tileentity;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.darkhax.bookshelf.util.RenderUtils;
import net.darkhax.botanypots.BotanyPots;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

@OnlyIn(Dist.CLIENT)
public class RendererBotanyPot extends TileEntityRenderer<TileEntityBotanyPot> {
    
    private static final Random RANDOM = new Random();
    private static final Direction[] SOIL_SIDES = new Direction[] { Direction.UP };
    private static final Direction[] CROP_SIDES = new Direction[] { Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    
    private final boolean isOptifinePresent;
    
    private static boolean detectOptifine () {
        
        try {
            
            final Class<?> clazz = Class.forName("net.optifine.Config");
            return clazz != null;
        }
        
        catch (final Exception e) {
            
            return false;
        }
    }
    
    public RendererBotanyPot(TileEntityRendererDispatcher dispatcher) {
        
        super(dispatcher);
        this.isOptifinePresent = detectOptifine();
        
        if (this.isOptifinePresent) {
            
            BotanyPots.LOGGER.warn("Detected Optifine. Botany pots will be rendered in compatibility mode.");
        }
    }
    
    @Override
    public void render (TileEntityBotanyPot tile, float partial, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        
        if (tile.getSoil() != null && BotanyPots.CLIENT_CONFIG.shouldRenderSoil()) {
            
            matrix.push();
            matrix.scale(0.625f, 0.384f, 0.625f);
            matrix.translate(0.3, 0.01, 0.3);
            
            if (!this.isOptifinePresent) {
                
                this.renderBlock(tile.getSoil().getRenderState(), tile.getWorld(), tile.getPos(), matrix, buffer, SOIL_SIDES);
            }
            
            else {
                
                this.renderBlock(tile.getSoil().getRenderState(), tile.getWorld(), tile.getPos(), matrix, buffer);
            }
            
            matrix.pop();
        }
        
        if (tile.getCrop() != null && BotanyPots.CLIENT_CONFIG.shouldRenderCrop()) {
            
            matrix.push();
            matrix.translate(0.5, 0.40, 0.5);
            
            if (BotanyPots.CLIENT_CONFIG.shouldDoGrowthAnimation()) {
                
                final float progressScale = 0.25f + (float) tile.getCurrentGrowthTicks() / tile.getTotalGrowthTicks() * 0.75f;
                final float growth = MathHelper.clamp(progressScale * 0.625f, 0, 1f);
                matrix.scale(growth, growth, growth);
            }
            
            matrix.translate(-0.5, 0, -0.5);
            
            final BlockState[] cropStates = tile.getCrop().getDisplayState();
            
            for (int i = 0; i < cropStates.length; i++) {
                
                matrix.translate(0, i, 0);
                
                if (!this.isOptifinePresent) {
                    
                    this.renderBlock(cropStates[i], tile.getWorld(), tile.getPos(), matrix, buffer, CROP_SIDES);
                }
                
                else {
                    
                    this.renderBlock(cropStates[i], tile.getWorld(), tile.getPos(), matrix, buffer);
                }
                
                matrix.translate(0, -i, 0);
            }
            
            matrix.pop();
        }
    }
    
    private void renderBlock (BlockState state, World world, BlockPos pos, MatrixStack matrix, IRenderTypeBuffer buffer, Direction[] renderSides) {
        
        final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        final IBakedModel model = dispatcher.getModelForState(state);
        
        final RenderType type = RenderUtils.findRenderType(state);
        
        if (type != null) {
            
            ForgeHooksClient.setRenderLayer(type);
            
            final IVertexBuilder builder = buffer.getBuffer(type);
            RenderUtils.renderModel(dispatcher.getBlockModelRenderer(), world, model, state, pos, matrix, builder, renderSides);
            
            ForgeHooksClient.setRenderLayer(null);
        }
    }
    
    /**
     * This only exists for optifine compat mode.
     */
    private void renderBlock (BlockState state, World world, BlockPos pos, MatrixStack matrix, IRenderTypeBuffer buffer) {
        
        final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        final IBakedModel model = dispatcher.getModelForState(state);
        final boolean useAO = Minecraft.isAmbientOcclusionEnabled() && state.getLightValue(world, pos) == 0 && model.isAmbientOcclusion();
        
        final RenderType type = RenderUtils.findRenderType(state);
        
        if (type != null) {
            
            ForgeHooksClient.setRenderLayer(type);
            
            final IVertexBuilder builder = buffer.getBuffer(type);
            this.renderModel(dispatcher.getBlockModelRenderer(), useAO, world, model, state, pos, matrix, builder, false, OverlayTexture.NO_OVERLAY);
            
            ForgeHooksClient.setRenderLayer(null);
        }
    }
    
    /**
     * This only exists for optifine compat mode.
     */
    private boolean renderModel (BlockModelRenderer renderer, boolean useAO, IBlockDisplayReader world, IBakedModel model, BlockState state, BlockPos pos, MatrixStack matrix, IVertexBuilder buffer, boolean checkSides, int overlay) {
        
        try {
            
            final IModelData modelData = model.getModelData(world, pos, state, EmptyModelData.INSTANCE);
            return useAO ? renderer.renderModelSmooth(world, model, state, pos, matrix, buffer, checkSides, RANDOM, 0L, overlay, modelData) : renderer.renderModelFlat(world, model, state, pos, matrix, buffer, checkSides, RANDOM, 0L, overlay, modelData);
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