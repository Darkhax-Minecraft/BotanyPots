package net.darkhax.botanypots.common.impl.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.darkhax.bookshelf.common.api.commands.PermissionLevel;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class BotanyPotsCommands {

    public static void build(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(BotanyPotsMod.MOD_ID);
        root.requires(PermissionLevel.OWNER);
        DebugCommands.build(root);
        dispatcher.register(root);
    }

    public static Component modMessage(Component component) {
        return Component.translatable("commands.botanypots.mod_message", component);
    }
}