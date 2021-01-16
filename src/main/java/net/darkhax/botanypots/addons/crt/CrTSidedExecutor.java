package net.darkhax.botanypots.addons.crt;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.blamejared.crafttweaker.CraftTweaker;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class CrTSidedExecutor {
    
    @Nullable
    public static <T> T callOnSide (LogicalSide side, Supplier<Callable<T>> toRun) {
        
        return side.isClient() ? callForSide(toRun, null) : callForSide(null, toRun);
    }
    
    @Nullable
    public static <T> T callForSide (@Nullable Supplier<Callable<T>> client, @Nullable Supplier<Callable<T>> server) {
        
        try {
            
            if (getLogicalSide().isClient() && client != null) {
                
                return client.get().call();
            }
            
            else if (getLogicalSide().isServer() && server != null) {
                
                return server.get().call();
            }
        }
        
        catch (final Exception e) {
            
            throw new RuntimeException(e);
        }
        
        return null;
    }
    
    public static void runOnSide (LogicalSide side, Supplier<Runnable> toRun) {
        
        if (side.isClient()) {
            
            runForSide(toRun, null);
        }
        
        else if (side.isServer()) {
            
            runForSide(null, toRun);
        }
    }
    
    public static void runForSide (@Nullable Supplier<Runnable> client, @Nullable Supplier<Runnable> server) {
        
        try {
            
            if (getLogicalSide().isClient() && client != null) {
                
                client.get().run();
            }
            
            else if (getLogicalSide().isServer() && server != null) {
                
                server.get().run();
            }
        }
        
        catch (final Exception e) {
            
            throw new RuntimeException(e);
        }
    }
    
    public static void runOnClient (Runnable toRun) {
        
        runOnSide(LogicalSide.CLIENT, () -> toRun);
    }
    
    public static void runOnServer (Runnable toRun) {
        
        runOnSide(LogicalSide.SERVER, () -> toRun);
    }
    
    public static LogicalSide getLogicalSide () {
        
        if (CraftTweaker.serverOverride || FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            
            return LogicalSide.SERVER;
        }
        
        return EffectiveSide.get();
    }
}