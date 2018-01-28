package net.chibidevteam.chibispongeplugin.command.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;

import net.chibidevteam.chibispongeplugin.ChibiSpongePlugin;
import net.chibidevteam.chibispongeplugin.PluginConfiguration;
import net.chibidevteam.chibispongeplugin.command.AbstractCommand;
import net.chibidevteam.chibispongeplugin.command.KeyValueArgumentList;
import net.chibidevteam.chibispongeplugin.exceptions.CriticalPluginErrorException;
import net.chibidevteam.chibispongeplugin.util.MessageUtils;

public abstract class AbstractSetCommand extends AbstractCommand {

    private static final String PARAMS = "params";

    protected String            configId;
    protected String            baseConfigPath;
    protected String            autoSaveKey;

    public AbstractSetCommand(String permission) {
        this.permission = permission;
        aliases = new String[] { "set" };
    }

    @Override
    protected void createArguments() throws CriticalPluginErrorException {
        argumentMap = new HashMap<>();
        mandatoryArgsCfgMap = getMandatoryArguments();
        optionalArgsCfgMap = getOptionalArguments();
        addArguments(mandatoryArgsCfgMap, false);

        Map<String, Class<?>> cfgMap = new HashMap<>();
        for (String key : optionalArgsCfgMap.keySet()) {
            cfgMap.put(key, KeyValueArgumentList.class);
        }
        addArguments(cfgMap, true);
    }

    @Override
    protected Object getArgConfig(String key) {
        return optionalArgsCfgMap.get(key);
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        ChibiSpongePlugin plugin = getPlugin();
        PluginConfiguration cfg;
        cfg = plugin.getConfig(configId);

        int count = 0;
        for (String key : optionalArgsCfgMap.keySet()) {
            Optional<Object> optn = args.getOne(key);
            if (optn.isPresent()) {
                cfg.set(baseConfigPath != null ? baseConfigPath + "." + key : key, optn.get());
            }
            ++count;
        }

        if (count == 0) {
            src.sendMessage(MessageUtils.getText("command.setCommand.error.noValueToSet", optionalArgsCfgMap.keySet()));
        } else if (autoSaveKey != null && cfg.getBoolean(baseConfigPath, autoSaveKey)) {
            cfg.save();
        }
        return CommandResult.success();
    }

    @Override
    protected String getHelp() {
        return MessageUtils.get("command.pluginConfig.set.help");
    }

}
