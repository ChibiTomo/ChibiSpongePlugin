package net.chibidevteam.chibispongeplugin.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

public abstract class AbstractNamespaceCommand extends AbstractCommand {

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {
        printHelp(src, ctx);
        return CommandResult.empty();
    }

}
