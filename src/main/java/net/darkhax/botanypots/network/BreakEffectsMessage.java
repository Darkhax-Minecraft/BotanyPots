package net.darkhax.botanypots.network;

import java.util.function.Supplier;

import net.darkhax.botanypots.BotanyPots;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

public class BreakEffectsMessage {
    
    private final BlockPos pos;
    private final int state;
    
    public BreakEffectsMessage(BlockPos pos, BlockState state) {
        
        this(pos, Block.getStateId(state));
    }
    
    private BreakEffectsMessage(BlockPos pos, int state) {
        
        this.pos = pos;
        this.state = state;
    }
    
    public static void encode (BreakEffectsMessage message, PacketBuffer buf) {
        
        buf.writeBlockPos(message.pos);
        buf.writeInt(message.state);
    }
    
    public static BreakEffectsMessage decode (PacketBuffer buf) {
        
        final BlockPos pos = buf.readBlockPos();
        final int state = buf.readInt();
        return new BreakEffectsMessage(pos, state);
    }
    
    public static void handle (BreakEffectsMessage message, Supplier<NetworkEvent.Context> ctx) {
        
        if (BotanyPots.CLIENT_CONFIG.shouldDoBreakEffects()) {
            
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                
                Minecraft.getInstance().world.playEvent(null, Constants.WorldEvents.BREAK_BLOCK_EFFECTS, message.pos, message.state);
            });
        }
    }
}
