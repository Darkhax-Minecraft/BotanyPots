package net.darkhax.botanypots.network;

import net.darkhax.botanypots.BotanyPots;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.DistExecutor;

public class BreakEffectsMessage {
    
    private final BlockPos pos;
    private final int state;
    
    public BreakEffectsMessage(BlockPos pos, BlockState state) {
        
        this.pos = pos;
        this.state = Block.getStateId(state);
    }
    
    public BreakEffectsMessage(PacketBuffer buf) {
        
        this.pos = buf.readBlockPos();
        this.state = buf.readVarInt();
    }
    
    public void write (PacketBuffer buf) {
        
        buf.writeBlockPos(this.pos);
        buf.writeVarInt(this.state);
    }
    
    public void doBreakEffects () {
        
        if (BotanyPots.CLIENT_CONFIG.shouldDoBreakEffects()) {
            
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                
                Minecraft.getInstance().world.playEvent(null, Constants.WorldEvents.BREAK_BLOCK_EFFECTS, this.pos, this.state);
            });
        }
    }
}
