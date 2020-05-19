package net.darkhax.botanypots.block.tileentity;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.darkhax.botanypots.BotanyPots;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
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
    private static final BitSet BITS = new BitSet(3);
    private static final Direction[] SOIL_SIDES = new Direction[] { Direction.UP };
    private static final Direction[] CROP_SIDES = new Direction[] { Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };
    
    public TileEntityRendererBotanyPot(TileEntityRendererDispatcher dispatcher) {
        
        super(dispatcher);
    }
    
    @Override
    public void render (TileEntityBotanyPot tile, float partial, MatrixStack matrix, IRenderTypeBuffer buffer, int light, int overlay) {
        
        if (tile.getSoil() != null && BotanyPots.CLIENT_CONFIG.shouldRenderSoil()) {
            
            matrix.push();
            matrix.scale(0.625f, 0.384f, 0.625f);
            matrix.translate(0.3, 0.01, 0.3);
            this.renderBlock(tile.getSoil().getRenderState(), tile.getWorld(), tile.getPos(), matrix, buffer, SOIL_SIDES);
            matrix.pop();
        }
        
        if (tile.getCrop() != null && BotanyPots.CLIENT_CONFIG.shouldRenderCrop()) {
            
            matrix.push();
            matrix.translate(0.5, 0.40, 0.5);
            
            if (BotanyPots.CLIENT_CONFIG.shouldDoGrowthAnimation()) {
                
                final float growth = MathHelper.clamp((float) tile.getCurrentGrowthTicks() / tile.getTotalGrowthTicks() * (10 / 16f), 0, 1f);
                matrix.scale(growth, growth, growth);
            }
            
            matrix.translate(-0.5, 0, -0.5);
            
            final BlockState[] cropStates = tile.getCrop().getDisplayState();
            
            for (int i = 0; i < cropStates.length; i++) {
                
                matrix.translate(0, i, 0);
                this.renderBlock(cropStates[i], tile.getWorld(), tile.getPos(), matrix, buffer, CROP_SIDES);
                matrix.translate(0, -i, 0);
            }
            
            matrix.pop();
        }
    }
    
    private void renderBlock (BlockState state, World world, BlockPos pos, MatrixStack matrix, IRenderTypeBuffer buffer, Direction[] renderSides) {
        
        final BlockRendererDispatcher dispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
        final IBakedModel model = dispatcher.getModelForState(state);
        
        final RenderType type = RenderTypeLookup.getRenderType(state);
        
        if (type != null) {
            
            ForgeHooksClient.setRenderLayer(type);
            
            final IVertexBuilder builder = buffer.getBuffer(type);
            this.renderModel(dispatcher.getBlockModelRenderer(), world, model, state, pos, matrix, builder, renderSides);
            
            ForgeHooksClient.setRenderLayer(null);
        }
    }
    
    public void renderModel (BlockModelRenderer renderer, ILightReader world, IBakedModel model, BlockState state, BlockPos pos, MatrixStack matrix, IVertexBuilder buffer, Direction[] sides) {
        
        final IModelData modelData = model.getModelData(world, pos, state, EmptyModelData.INSTANCE);
        
        for (final Direction side : sides) {
            
            RANDOM.setSeed(0L);
            final List<BakedQuad> sidedQuads = model.getQuads(state, side, RANDOM, modelData);
            
            if (!sidedQuads.isEmpty()) {
                
                final int lightForSide = WorldRenderer.getPackedLightmapCoords(world, state, pos.offset(side));
                renderer.renderQuadsFlat(world, state, pos, lightForSide, OverlayTexture.NO_OVERLAY, false, matrix, buffer, sidedQuads, BITS);
            }
        }
        
        RANDOM.setSeed(0L);
        final List<BakedQuad> unsidedQuads = model.getQuads(state, null, RANDOM, modelData);
        
        if (!unsidedQuads.isEmpty()) {
            
            renderer.renderQuadsFlat(world, state, pos, -1, OverlayTexture.NO_OVERLAY, true, matrix, buffer, unsidedQuads, BITS);
        }
    }
}