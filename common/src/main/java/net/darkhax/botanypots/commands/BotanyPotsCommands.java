package net.darkhax.botanypots.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.darkhax.bookshelf.api.commands.ICommandBuilder;
import net.darkhax.botanypots.Constants;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class BotanyPotsCommands implements ICommandBuilder {
    @Override
    public void build(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {

        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(Constants.MOD_ID);

        CommandDump.build(root);

        dispatcher.register(root);
    }

    public static Component modMessage(Component component) {

        return Component.translatable("commands.botanypots.mod_message", component);
    }
}