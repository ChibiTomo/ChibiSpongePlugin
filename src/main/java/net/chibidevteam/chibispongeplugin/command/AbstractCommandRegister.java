package net.chibidevteam.chibispongeplugin.command;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;

import net.chibidevteam.chibispongeplugin.ChibiSpongePlugin;
import net.chibidevteam.chibispongeplugin.util.MessageUtils;

public abstract class AbstractCommandRegister {
    private Set<AbstractCommand> commands = new HashSet<>();

    private ChibiSpongePlugin    plugin;

    protected void addCommand(Class<? extends AbstractCommand> clazz) {
        try {
            addCommand(clazz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            plugin.error(MessageUtils.get("commandRegister.error.cannotAdd", clazz), e);
        }
    }

    protected void addCommand(AbstractCommand cmd) {
        commands.add(cmd);
    }

    protected abstract void setCommands();

    public void register() {
        setCommands();

        CommandSpec spec;
        for (AbstractCommand cmd : commands) {
            spec = cmd.build();
            Sponge.getCommandManager().register(plugin, spec, cmd.getAliases());
            plugin.info(MessageUtils.get("commandRegister.registered", cmd.getAliases()[0]));
        }
    }

    public void setPlugin(ChibiSpongePlugin plugin) {
        this.plugin = plugin;
    }

}
