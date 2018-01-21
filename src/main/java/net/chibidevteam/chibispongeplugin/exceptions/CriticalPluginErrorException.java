package net.chibidevteam.chibispongeplugin.exceptions;

public class CriticalPluginErrorException extends ChibiPluginException {
    private static final long serialVersionUID = 3081664368232794695L;

    public CriticalPluginErrorException(String arg0) {
        super(arg0);
    }

    public CriticalPluginErrorException(Throwable arg0) {
        super(arg0);
    }

    public CriticalPluginErrorException(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

}
