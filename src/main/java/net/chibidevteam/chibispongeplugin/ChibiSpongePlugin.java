package net.chibidevteam.chibispongeplugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameLoadCompleteEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;

import com.google.inject.Inject;

import net.chibidevteam.chibispongeplugin.command.AbstractCommandRegister;
import net.chibidevteam.chibispongeplugin.exceptions.ChibiPluginException;
import net.chibidevteam.chibispongeplugin.exceptions.ConfigurationNotFoundException;
import net.chibidevteam.chibispongeplugin.exceptions.IOConfigurationException;
import net.chibidevteam.chibispongeplugin.util.MessageUtils;

/**
 * This is an abstract class that make easy the creation of Plugins for Sponge
 *
 * @author ChibiTomo
 *
 */
public abstract class ChibiSpongePlugin {
    /**************************************************************************************************/
    // Constants

    private static final String              DEFAULT_CONF_FILE = "config.conf";
    public static final String               MAIN_CONFIG_ID    = "CHIBI_SPONGE_PLUGIN_MAIN_CONF";

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
    protected String getDefaultConfigFile() {
        return DEFAULT_CONF_FILE;
    }

    /**
     * This method is called after adding the main config file. It is used to call
     * more 'addConfiguration'
     *
     * @throws IOConfigurationException
     */
    protected void addConfigurations() throws IOConfigurationException {
    }

    /**
     *
     * return the object implementing {@link AbstractCommandRegister} for this
     * plugin
     */
    protected Optional<AbstractCommandRegister> getCommandRegister() {
        return Optional.empty();
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
        try {
            setupConfig();
        } catch (IOConfigurationException e1) {
            error(MessageUtils.get("config.error.critical"), e1);
        }
    }

    @Listener
    public void init(GameInitializationEvent e) throws ChibiPluginException {
        Optional<AbstractCommandRegister> optCommandRegister = getCommandRegister();
        if (optCommandRegister.isPresent()) {
            AbstractCommandRegister commandRegister = optCommandRegister.get();
            commandRegister.setPlugin(this);
            commandRegister.register();
        }
    }

    @Listener
    public void postInit(GamePostInitializationEvent e) throws ChibiPluginException {
    }

    @Listener
    public void loadComplete(GameLoadCompleteEvent e) throws ChibiPluginException {
    }

    /**
     * @throws IOConfigurationException
     ************************************************************************************************/
    // Setup

    private void setupConfig() throws IOConfigurationException {
        boolean useSharedConfig = useSharedConfig();
        configDir = useSharedConfig ? sharedConfigDir : privateConfigDir;
        configFile = useSharedConfig ? sharedConfigFile : privateConfigFile;

        loadConfigs();
        saveAllConfigs();
    }

    /**
     * @throws IOConfigurationException
     ************************************************************************************************/
    // Config

    private void loadConfigs() throws IOConfigurationException {
        addConfiguration(MAIN_CONFIG_ID, configFile, getDefaultConfigFile());
        addConfigurations();
    }

    protected void addExternalConfiguration(String name, String path) throws IOConfigurationException {
        addConfiguration(name, path, null);
    }

    protected void addExternalConfiguration(String name, String path, String jarUrl) throws IOConfigurationException {
        addConfiguration(name, Paths.get(path), jarUrl);
    }

    protected void addConfiguration(String name, String path) throws IOConfigurationException {
        addConfiguration(name, path, null);
    }

    protected void addConfiguration(String name, String path, String jarUrl) throws IOConfigurationException {
        addConfiguration(name, Paths.get(path), jarUrl);
    }

    protected void addConfiguration(String name, Path path, String jarUrl) throws IOConfigurationException {
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

    protected void addExternalConfiguration(String name, Path path, String jarUrl) throws IOConfigurationException {
        addConf(name, path, jarUrl);
    }

    private void addConf(String name, Path path, String jarUrl) throws IOConfigurationException {
        String ju = jarUrl;
        while (ju.startsWith(File.separator) || ju.startsWith("/")) {
            ju = ju.substring(1);
        }
        PluginConfiguration pc = new PluginConfiguration(this, name, path, ju);
        configs.put(name, pc);
    }

    /**
     * Save all the registered configurations
     *
     * @throws IOConfigurationException
     */
    public void saveAllConfigs() throws IOConfigurationException {
        for (PluginConfiguration pc : configs.values()) {
            pc.save();
        }
    }

    /**
     * Persist the main configuration
     *
     * @throws IOConfigurationException
     */
    public void saveMainConfig() throws IOConfigurationException {
        saveConfig(MAIN_CONFIG_ID);
    }

    /**
     * Persist the given config
     *
     * @param name
     *            , the name of the configuration to save
     * @throws IOConfigurationException
     */
    public void saveConfig(String name) throws IOConfigurationException {
        try {
            getConfig(name).save();
        } catch (ConfigurationNotFoundException e) {
            throw new IOConfigurationException(MessageUtils.get("config.error.cannotSave", name), e);
        }
    }

    public PluginConfiguration getConfig(String name) throws ConfigurationNotFoundException {
        PluginConfiguration pc = configs.get(name);
        if (pc == null) {
            throw new ConfigurationNotFoundException(MessageUtils.get("config.error.notRegistered"));
        }
        return pc;
    }

    public boolean usePermissions() throws ConfigurationNotFoundException {
        return getConfig(MAIN_CONFIG_ID).getBoolean("plugin", "usePermissions");
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
