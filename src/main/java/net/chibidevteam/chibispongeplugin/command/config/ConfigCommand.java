package net.chibidevteam.chibispongeplugin.command.config;

import net.chibidevteam.chibispongeplugin.command.AbstractCommand;
import net.chibidevteam.chibispongeplugin.command.AbstractNamespaceCommand;
import net.chibidevteam.chibispongeplugin.util.MessageUtils;

public class ConfigCommand extends AbstractNamespaceCommand {

    public ConfigCommand() {
        this(null);
    }

    public ConfigCommand(String permission) {
        aliases = new String[] { "plugin-config", "config", "plgcfg" };
        this.permission = permission;
        children = new AbstractCommand[] { new SetCommand(permission) };
    }

    @Override
    protected String getHelp() {
        return MessageUtils.get("command.pluginConfig.help");
    }

}
