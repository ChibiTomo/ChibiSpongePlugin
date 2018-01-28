package net.chibidevteam.chibispongeplugin.command;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.spec.CommandSpec.Builder;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import net.chibidevteam.chibispongeplugin.ChibiSpongePlugin;
import net.chibidevteam.chibispongeplugin.exceptions.CriticalPluginErrorException;
import net.chibidevteam.chibispongeplugin.util.MessageUtils;

public abstract class AbstractCommand implements CommandExecutor {

    protected String[]                    aliases;
    protected Text                        description;
    protected Text                        extendedDescription;
    protected String                      permission;
    protected InputTokenizer              inputTokenizer;

    protected AbstractCommand[]           children;

    private static boolean                usesPermissions;

    private ChibiSpongePlugin             plugin;
    protected Map<String, CommandElement> argumentMap;
    protected Map<String, Class<?>>       optionalArgsCfgMap;
    protected Map<String, Class<?>>       mandatoryArgsCfgMap;

    protected abstract String getHelp();

    protected CommandSpec build() throws CriticalPluginErrorException {
        validate();

        Builder builder = CommandSpec.builder();

        if (description != null) {
            builder.description(description);
        }
        if (extendedDescription != null) {
            builder.extendedDescription(extendedDescription);
        }

        if (description == null && extendedDescription == null) {
            description = Text.of(getHelp());
            builder.description(description);
        }

        if (usesPermissions && permission != null) {
            builder.permission(permission);
        }
        if (inputTokenizer != null) {
            builder.inputTokenizer(inputTokenizer);
        }

        createArguments();
        if (!argumentMap.isEmpty()) {
            builder.arguments(argumentMap.values().toArray(new CommandElement[] {}));
        } else {
            builder.arguments(new CommandElement[] { GenericArguments.none() });
        }
        if (children != null) {
            CommandSpec spec;
            for (AbstractCommand cmd : children) {
                spec = cmd.build();
                builder.child(spec, cmd.getAliases());
            }
        }

        return builder.executor(this).build();
    }

    protected void createArguments() throws CriticalPluginErrorException {
        argumentMap = new HashMap<>();
        mandatoryArgsCfgMap = getMandatoryArguments();
        optionalArgsCfgMap = getOptionalArguments();
        addArguments(mandatoryArgsCfgMap, false);
        addArguments(optionalArgsCfgMap, true);
    }

    @SuppressWarnings("unchecked")
    protected final void addArguments(Map<String, Class<?>> map, boolean optional) throws CriticalPluginErrorException {

        for (Entry<String, Class<?>> entry : map.entrySet()) {
            String key = entry.getKey();
            Text txt = Text.of(key);
            Class<?> clazz = entry.getValue();
            CommandElement arg;

            if (Boolean.class.isAssignableFrom(clazz)) {
                arg = GenericArguments.bool(txt);
            } else if (String.class.isAssignableFrom(clazz)) {
                arg = GenericArguments.string(txt);
            } else if (KeyValueArgumentList.class.isAssignableFrom(clazz)) {
                // arg = new KeyValueArgumentList(txt, (Map<String, Class<?>>)
                // getArgConfig(key));
                arg = new KeyValueArgumentList(txt, (Class<?>) getArgConfig(key));
            } else {
                throw new CriticalPluginErrorException(
                        MessageUtils.get("command.error.unknownArgumentType", clazz, key, aliases[0]));
            }

            if (optional) {
                arg = GenericArguments.optional(arg);
            }

            if (argumentMap.containsKey(key)) {
                plugin.warn("command.warning.overridingArgument", key, argumentMap.get(key), clazz, aliases[0]);
            }

            argumentMap.put(key, arg);
        }
    }

    protected Map<String, Class<?>> getMandatoryArguments() {
        return new HashMap<>();
    }

    protected Map<String, Class<?>> getOptionalArguments() {
        return new HashMap<>();
    }

    protected Object getArgConfig(String key) {
        return null;
    }

    protected void printHelp(CommandSource src, CommandContext ctx) {
        src.sendMessage(Text.of(TextColors.YELLOW, getHelp()));
    }

    private void validate() {
        if (aliases == null || aliases.length < 1) {
            throw new RuntimeException();
        }
    }

    public String[] getAliases() {
        return aliases;
    }

    public AbstractCommand[] getChildren() {
        return children;
    }

    public static boolean isUsesPermissions() {
        return usesPermissions;
    }

    public static void setUsesPermissions(boolean usesPermissions) {
        AbstractCommand.usesPermissions = usesPermissions;
    }

    public ChibiSpongePlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(ChibiSpongePlugin plugin) {
        this.plugin = plugin;

        if (children != null) {
            for (AbstractCommand child : children) {
                child.setPlugin(plugin);
            }
        }
    }

    public Map<String, Class<?>> getOptionalArgsCfgMap() {
        return optionalArgsCfgMap;
    }

    public Map<String, Class<?>> getMandatoryArgsCfgMap() {
        return mandatoryArgsCfgMap;
    }
}
