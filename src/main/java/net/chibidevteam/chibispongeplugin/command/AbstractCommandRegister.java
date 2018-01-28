package net.chibidevteam.chibispongeplugin.command;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;

import net.chibidevteam.chibispongeplugin.ChibiSpongePlugin;
import net.chibidevteam.chibispongeplugin.exceptions.ConfigurationNotFoundException;
import net.chibidevteam.chibispongeplugin.exceptions.CriticalPluginErrorException;
import net.chibidevteam.chibispongeplugin.util.MessageUtils;

public abstract class AbstractCommandRegister {
    private Set<AbstractCommand> commands = new HashSet<>();

    private ChibiSpongePlugin    plugin;

    public final void setPlugin(ChibiSpongePlugin plugin) throws CriticalPluginErrorException {
        this.plugin = plugin;
        try {
            AbstractCommand.setUsesPermissions(plugin.usePermissions());
        } catch (ConfigurationNotFoundException e) {
            throw new CriticalPluginErrorException(e);
        }
    }

    protected final void addCommand(Class<? extends AbstractCommand> clazz) {
        try {
            addCommand(clazz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            plugin.error(MessageUtils.get("commandRegister.error.cannotAdd", clazz), e);
        }
    }

    protected final void addCommand(AbstractCommand cmd) {
        commands.add(cmd);
    }

    protected abstract void setCommands();

    public final void register() throws CriticalPluginErrorException {
        setCommands();

        CommandSpec spec;
        for (AbstractCommand cmd : commands) {
            cmd.setPlugin(plugin);
            spec = cmd.build();
            Sponge.getCommandManager().register(plugin, spec, cmd.getAliases());
            plugin.info(MessageUtils.get("commandRegister.registered", cmd.getAliases()[0]));
        }
    }

}
