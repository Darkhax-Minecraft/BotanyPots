package net.darkhax.botanypots.block.tileentity;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public class TileEntityRendererBotanyPot extends TileEntityRenderer<TileEntityBotanyPot> {
    
    @Override
    public void render (TileEntityBotanyPot tile, double x, double y, double z, float partialTicks, int destroyStage) {
        
        if (tile.getSoil() != null) {
            GlStateManager.disableLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translated(x, y, z);
            GlStateManager.scaled(10 / 16d, 10 / 26d, 10 / 16d);
            GlStateManager.translated(0.3d, 0.01, 0.3d);
            renderBlockModel(tile.getWorld(), tile.getPos(), tile.getSoil().getRenderState(), true);
            GlStateManager.popMatrix();
            
            if (tile.getCrop() != null) {
                
                final float growth = MathHelper.clamp((tile.getCurrentGrowthTicks() + partialTicks) / tile.getTotalGrowthTicks() * (10 / 16f), 0f, 1f);
                GlStateManager.pushMatrix();
                GlStateManager.translated(x, y, z);
                GlStateManager.translated(0.5, 0.40, 0.5);
                GlStateManager.scaled(growth, growth, growth);
                GlStateManager.translated(-0.5, 0, -0.5);
                renderBlockModel(tile.getWorld(), tile.getPos(), tile.getCrop().getDisplayState(), true);
                GlStateManager.popMatrix();
            }
            GlStateManager.enableLighting();
        }
    }
    
    public static void renderBlockModel (World world, BlockPos pos, BlockState state, boolean translateToOrigin) {
        
        buff().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        if (translateToOrigin) {
            buff().setTranslation(-pos.getX(), -pos.getY(), -pos.getZ());
        }
        final BlockRendererDispatcher blockrendererdispatcher = mc().getBlockRendererDispatcher();
        final BlockModelShapes modelShapes = blockrendererdispatcher.getBlockModelShapes();
        final IBakedModel ibakedmodel = modelShapes.getModel(state);
        bind(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        for (final BlockRenderLayer layer : BlockRenderLayer.values()) {
            if (state.getBlock().canRenderInLayer(state, layer)) {
                ForgeHooksClient.setRenderLayer(layer);
                blockrendererdispatcher.getBlockModelRenderer().renderModel(world, ibakedmodel, state, pos, buff(), false, new Random(), 0, EmptyModelData.INSTANCE);
            }
        }
        ForgeHooksClient.setRenderLayer(null);
        if (translateToOrigin) {
            buff().setTranslation(0, 0, 0);
        }
        Tessellator.getInstance().draw();
    }
    
    public static void bind (ResourceLocation texture) {
        
        mc().getTextureManager().bindTexture(texture);
    }
    
    public static BufferBuilder buff () {
        
        return tess().getBuffer();
    }
    
    public static Tessellator tess () {
        
        return Tessellator.getInstance();
    }
    
    public static Minecraft mc () {
        
        return Minecraft.getInstance();
    }
    
    public static double clamp (double value, double min, double max) {
        
        return Math.max(min, Math.min(value, max));
    }
    
    public static double smoothStep (double start, double end, double amount) {
        
        amount = clamp(amount, 0, 1);
        amount = clamp((amount - start) / (end - start), 0, 1);
        return amount * amount * (3 - 2 * amount);
    }
}