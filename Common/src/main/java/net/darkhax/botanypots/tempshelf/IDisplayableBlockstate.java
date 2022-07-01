package net.darkhax.botanypots.tempshelf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface IDisplayableBlockstate {

    void render(PoseStack stack, Level level, BlockPos pos, VertexConsumer buffer, int light, int overlay, int frame, Direction... visibleFaces);
}