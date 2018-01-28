package net.chibidevteam.chibispongeplugin.exceptions;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.text.Text;

public class ChibiPluginException extends CommandException {
    private static final long serialVersionUID = 2859067183496450861L;

    public ChibiPluginException(String arg0) {
        super(Text.of(arg0));
    }

    public ChibiPluginException(Throwable arg0) {
        super(Text.of(arg0));
    }

    public ChibiPluginException(String arg0, Throwable arg1) {
        super(Text.of(arg0), arg1);
    }

}
