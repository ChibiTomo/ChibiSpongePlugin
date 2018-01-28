package net.chibidevteam.chibispongeplugin.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

public class KeyValueArgumentList extends CommandElement {

    private static final String PARSE_ERROR = "This is a parse error! Fuck you if you succed to assign it!!!";

    private Class<?>            clazz;

    protected KeyValueArgumentList(Text key, Class<?> clazz) {
        super(key);
        this.clazz = clazz;
    }

    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
        List<String> skipped = new ArrayList<>();

        Object result = PARSE_ERROR;
        String arg;
        while (args.hasNext() && PARSE_ERROR.equals(result)) {
            arg = args.next();
            result = parse(arg);
            if (PARSE_ERROR.equals(result)) {
                skipped.add(arg);
            }
        }

        for (int i = skipped.size() - 1; i >= 0; --i) {
            args.insertArg(skipped.get(i));
        }

        if (PARSE_ERROR.equals(result)) {
            throw args.createError(Text.of("command.keyVal.noArg"));
        }

        return result;
    }

    private Object parse(String arg) throws ArgumentParseException {
        String[] parts = arg.split("=");

        if (parts.length != 2) {
            return PARSE_ERROR;
        }
        String key = parts[0];
        String value = parts[1];

        if (!getKey().toPlain().equals(key) || clazz == null) {
            return PARSE_ERROR;
        }

        if (Boolean.class.isAssignableFrom(clazz)) {
            return Boolean.parseBoolean(value);
        } else if (List.class.isAssignableFrom(clazz)) {
            return Arrays.asList(value.split(","));
        } else if (Set.class.isAssignableFrom(clazz)) {
            return new HashSet<>(Arrays.asList(value.split(",")));
        }

        return value;
    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
        return Collections.emptyList();
    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of(getKey(), "=<" + clazz.getSimpleName() + ">");
    }

}
