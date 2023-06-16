package net.darkhax.botanypots.events;

import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

public class BotanyPotEvent extends Event {

    private final Level level;
    private final BlockPos pos;
    private final BlockEntityBotanyPot pot;

    public BotanyPotEvent(Level level, BlockPos pos, BlockEntityBotanyPot pot) {

        this.level = level;
        this.pos = pos;
        this.pot = pot;
    }

    public Level getLevel() {

        return this.level;
    }

    public BlockPos getPos() {

        return this.pos;
    }

    public BlockEntityBotanyPot getPot() {

        return this.pot;
    }
}