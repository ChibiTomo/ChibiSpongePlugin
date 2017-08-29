package net.chibidevteam.chibispongeplugin;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;

import com.google.inject.Inject;

import net.chibidevteam.chibispongeplugin.commands.AbstractCommandRegister;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public abstract class ChibiSpongePlugin {

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path                                            configDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path                                            defaultConfigDir;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path                                            sharedConfigDir;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path                                            sharedDefaultConfigDir;

    // Give us a configuration to work from
    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configLoader;

    // These are all injected on plugin load for users to work from
    @Inject
    protected Logger                                        logger;

    @Inject
    private Game                                            game;

    @Inject(optional = true)
    private AbstractCommandRegister                         commandRegister;

    protected void addEventListener(Object listener) {
        Sponge.getEventManager().registerListeners(this, listener);
    }

    protected void removeEventListener(Object listener) {
        Sponge.getEventManager().unregisterListeners(listener);
    }

    protected void removeAllEventListener() {
        Sponge.getEventManager().unregisterPluginListeners(this);
    }

    protected void fireEvent(Event e) {
        Sponge.getEventManager().post(e);
    }

    /*
     * LISTENERS
     */

    @Listener
    public void init(GameInitializationEvent e) {
        if (commandRegister != null) {
            commandRegister.setPlugin(this);
            commandRegister.register();
        }
    }

    /*
     * LOGGING
     */

    public void error(String msg, Throwable e) {
        logger.error(msg, e);
    }

    /*
     * GETTERS
     */

    public Path getConfigDir() {
        return configDir;
    }

    public Path getDefaultConfigDir() {
        return defaultConfigDir;
    }

    public Path getSharedConfigDir() {
        return sharedConfigDir;
    }

    public Path getSharedDefaultConfigDir() {
        return sharedDefaultConfigDir;
    }

    public ConfigurationLoader<CommentedConfigurationNode> getConfigLoader() {
        return configLoader;
    }

    public Game getGame() {
        return game;
    }

    public Logger getLogger() {
        return logger;
    }
}
