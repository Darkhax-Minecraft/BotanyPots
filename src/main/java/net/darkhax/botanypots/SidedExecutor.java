/**
 * This class was created by <Darkhax>. It is distributed as part of Bookshelf. You can find
 * the original source here: https://github.com/Darkhax-Minecraft/Bookshelf
 *
 * Bookshelf is Open Source and distributed under the GNU Lesser General Public License version
 * 2.1.
 */
package net.darkhax.botanypots;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;

public final class SidedExecutor {
    
    @Nullable
    public static <T> T callOnSide (LogicalSide side, Supplier<Callable<T>> toRun) {
        
        return side.isClient() ? callForSide(toRun, null) : callForSide(null, toRun);
    }
    
    @Nullable
    public static <T> T callForSide (@Nullable Supplier<Callable<T>> client, @Nullable Supplier<Callable<T>> server) {
        
        try {
            
            if (EffectiveSide.get().isClient() && client != null) {
                
                return client.get().call();
            }
            
            else if (server != null){
                
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
        
        else {
            
            runForSide(null, toRun);
        }
    }
    
    public static void runForSide (@Nullable Supplier<Runnable> client, @Nullable Supplier<Runnable> server) {
        
        try {
            
            if (EffectiveSide.get().isClient() && client != null) {
                
                client.get().run();
            }
            
            else if (server != null){
                
                server.get().run();
            }
        }
        
        catch (final Exception e) {
            
            throw new RuntimeException(e);
        }
    }
}