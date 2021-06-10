package net.darkhax.botanypots.api.events;

import javax.annotation.Nullable;

import net.darkhax.botanypots.block.tileentity.TileEntityBotanyPot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Event;

public class BotanyPotEvent extends Event {
    
    private final TileEntityBotanyPot pot;
    
    public BotanyPotEvent(TileEntityBotanyPot pot) {
        
        this.pot = pot;
    }
    
    public TileEntityBotanyPot getBotanyPot () {
        
        return this.pot;
    }
    
    public static class Player extends BotanyPotEvent {
        
        @Nullable
        private final PlayerEntity player;
        
        public Player(TileEntityBotanyPot pot, @Nullable PlayerEntity player) {
            
            super(pot);
            this.player = player;
        }
        
        @Nullable
        public PlayerEntity getPlayer () {
            
            return this.player;
        }
    }
}