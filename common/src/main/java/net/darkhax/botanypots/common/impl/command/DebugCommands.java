package net.darkhax.botanypots.common.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class DebugCommands {

    public static void build(LiteralArgumentBuilder<CommandSourceStack> parent) {
        final LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("debug");
        MissingCommand.build(cmd);
        PlaceCrops.build(cmd);
        parent.then(cmd);
    }
}