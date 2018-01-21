package net.chibidevteam.chibispongeplugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;

import com.google.inject.Inject;

import net.chibidevteam.chibispongeplugin.command.AbstractCommandRegister;

/**
 * This is an abstract class that make easy the creation of Plugins for Sponge
 *
 * @author ChibiTomo
 *
 */
public abstract class ChibiSpongePlugin {
    /**************************************************************************************************/
    // Constants

    private static final String              DEFAULT_CONF_FILE = "default.conf";
    private static final String              MAIN_CONFIG_ID    = "CHIBI_SPONGE_PLUGIN_MAIN_CONF";

    /**************************************************************************************************/
    // Injects

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path                             privateConfigDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path                             privateConfigFile;

    @Inject
    @ConfigDir(sharedRoot = true)
    private Path                             sharedConfigDir;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path                             sharedConfigFile;

    private Map<String, PluginConfiguration> configs           = new HashMap<>();

    @Inject
    protected Logger                         logger;

    @Inject
    private Game                             game;

    /**************************************************************************************************/
    // Attibutes
    private Path                             configDir;
    private Path                             configFile;

    /**************************************************************************************************/
    // To override

    /**
     *
     * @return true if the Plugin use the shared config dir
     */
    protected boolean useSharedConfig() {
        return false;
    }

    /**
     *
     * @return the path to the main config file
     */
    protected String getDefaultConfAssetPath() {
        return DEFAULT_CONF_FILE;
    }

    /**
     * This method is called after adding the main config file. It is used to call
     * more 'addConfiguration'
     */
    protected void addConfigurations() {
    }

    /**
     *
     * return the object implementing {@link AbstractCommandRegister} for this
     * plugin
     */
    protected AbstractCommandRegister getCommandRegister() {
        return null;
    }

    /**************************************************************************************************/
    // Events

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

    /**************************************************************************************************/
    // Logging

    public void info(String format, Object... objects) {
        logger.info(format, objects);
    }

    public void warn(String format, Object... objects) {
        logger.warn(format, objects);
    }

    public void error(String msg, Throwable e) {
        logger.error(msg, e);
    }

    /**************************************************************************************************/
    // Listeners

    @Listener
    public void preInit(GamePreInitializationEvent e) {
        setupConfig();
    }

    @Listener
    public void init(GameInitializationEvent e) {
        AbstractCommandRegister commandRegister = getCommandRegister();
        if (commandRegister != null) {
            commandRegister.setPlugin(this);
            commandRegister.register();
        }
    }

    /**************************************************************************************************/
    // Setup

    private void setupConfig() {
        boolean useSharedConfig = useSharedConfig();
        configDir = useSharedConfig ? sharedConfigDir : privateConfigDir;
        configFile = useSharedConfig ? sharedConfigFile : privateConfigFile;

        loadConfigs();
        saveAllConfigs();
    }

    /**************************************************************************************************/
    // Config

    private void loadConfigs() {
        addConfiguration(MAIN_CONFIG_ID, configFile, getDefaultConfAssetPath());
        addConfigurations();
    }

    protected void addExternalConfiguration(String name, String path) {
        addConfiguration(name, path, null);
    }

    protected void addExternalConfiguration(String name, String path, String jarUrl) {
        addConfiguration(name, Paths.get(path), jarUrl);
    }

    protected void addConfiguration(String name, String path) {
        addConfiguration(name, path, null);
    }

    protected void addConfiguration(String name, String path, String jarUrl) {
        addConfiguration(name, Paths.get(path), jarUrl);
    }

    protected void addConfiguration(String name, Path path, String jarUrl) {
        Path p = path;
        if (!p.startsWith(configDir)) {
            String sep = "";
            if (!p.startsWith(File.separator) || !p.startsWith("/")) {
                sep = File.separator;
            }
            p = Paths.get(configDir + sep + path);
        }
        addConf(name, p, jarUrl);
    }

    protected void addExternalConfiguration(String name, Path path, String jarUrl) {
        addConf(name, path, jarUrl);
    }

    private void addConf(String name, Path path, String jarUrl) {
        String ju = jarUrl;
        while (ju.startsWith(File.separator) || ju.startsWith("/")) {
            ju = ju.substring(1);
        }
        PluginConfiguration pc = new PluginConfiguration(this, name, path, ju);
        configs.put(name, pc);
    }

    /**
     * Save all the registered configurations
     */
    public void saveAllConfigs() {
        for (PluginConfiguration pc : configs.values()) {
            pc.save();
        }
    }

    /**
     * Persist the main configuration
     */
    public void saveMainConfig() {
        saveConfig(MAIN_CONFIG_ID);
    }

    /**
     * Persist the given config
     *
     * @param name
     *            , the name of the configuration to save
     */
    public void saveConfig(String name) {
        PluginConfiguration pc = configs.get(name);
        if (pc != null) {
            pc.save();
        }
    }

    /**************************************************************************************************/
    // Getters

    /**
     *
     * @return The Sponge Game instance
     */
    public Game getGame() {
        return game;
    }

    /**
     *
     * @return the plugin logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     *
     * @return configuration directory of this Plugin
     */
    public Path getConfigDir() {
        return configDir;
    }
}
