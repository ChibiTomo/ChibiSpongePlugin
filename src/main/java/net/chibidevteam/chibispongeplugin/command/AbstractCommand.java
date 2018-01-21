package net.chibidevteam.chibispongeplugin.command;

import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.parsing.InputTokenizer;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.command.spec.CommandSpec.Builder;
import org.spongepowered.api.text.Text;

public abstract class AbstractCommand implements CommandExecutor {

    protected String[]          aliases;
    protected CommandElement[]  arguments;
    protected Text              description;
    protected Text              extendedDescription;
    protected String            permission;
    protected InputTokenizer    inputTokenizer;

    protected AbstractCommand[] children;

    protected CommandSpec build() {
        validate();

        Builder builder = CommandSpec.builder();

        if (description != null) {
            builder.description(description);
        }
        if (extendedDescription != null) {
            builder.extendedDescription(extendedDescription);
        }
        if (permission != null) {
            builder.permission(permission);
        }
        if (inputTokenizer != null) {
            builder.inputTokenizer(inputTokenizer);
        }
        if (arguments != null) {
            builder.arguments(arguments);
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
}
