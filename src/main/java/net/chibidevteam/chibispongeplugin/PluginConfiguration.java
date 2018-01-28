package net.chibidevteam.chibispongeplugin;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;

import net.chibidevteam.chibispongeplugin.exceptions.IOConfigurationException;
import net.chibidevteam.chibispongeplugin.util.MessageUtils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

/**
 * This class represents a plugin configuration. A plugin can have multiple of
 * it.
 *
 * @author ChibiTomo
 *
 */
public class PluginConfiguration {
    private ChibiSpongePlugin                       plugin;
    private String                                  name;
    private Path                                    path;
    private String                                  jarUrl;

    ConfigurationNode                               rootNode;
    ConfigurationLoader<CommentedConfigurationNode> loader;

    public PluginConfiguration(ChibiSpongePlugin plugin, String name, String uri, String jarUrl)
            throws IOConfigurationException {
        this(plugin, name, Paths.get(uri), jarUrl);
    }

    public PluginConfiguration(ChibiSpongePlugin plugin, String name, Path path, String jarUrl)
            throws IOConfigurationException {
        this.plugin = plugin;
        this.name = name;
        this.path = path;
        this.jarUrl = jarUrl;

        loader = HoconConfigurationLoader.builder().setPath(path).build();
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            reloadDefault();
        } else {
            reload();
        }
    }

    public void reload() throws IOConfigurationException {
        rootNode = load(loader, path.toString());
    }

    public void save() throws IOConfigurationException {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            String msg = MessageUtils.get("config.error.cannotSaveTo", name, path);
            plugin.error(msg, e);
            throw new IOConfigurationException(msg, e);
        }
    }

    public String getName() {
        return name;
    }

    public void set(String key, Object value) {
        Object[] keys = key.split("\\.");
        getNode(keys).setValue(value);
    }

    public String getString(Object... keys) {
        return getNode(keys).getString();
    }

    public boolean getBoolean(Object... keys) {
        return getNode(keys).getBoolean();
    }

    private ConfigurationNode getNode(Object... keys) {
        return rootNode.getNode(keys);
    }

    private void reloadDefault() throws IOConfigurationException {
        if (!StringUtils.isBlank(jarUrl)) {
            Optional<Asset> optional = Sponge.getAssetManager().getAsset(plugin, jarUrl);
            if (optional.isPresent()) {
                URL url = optional.get().getUrl();
                ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setURL(url)
                        .build();
                rootNode = load(loader, jarUrl);
            } else {
                plugin.warn(MessageUtils.get("config.error.defaultNotFound", jarUrl));
            }
        } else {
            plugin.info(MessageUtils.get("config.error.noDefault", name));
        }
    }

    private ConfigurationNode load(ConfigurationLoader<CommentedConfigurationNode> loader, String path)
            throws IOConfigurationException {
        try {
            return loader.load();
        } catch (IOException e) {
            String msg = MessageUtils.get("config.error.cannotLoadFrom", name, path);
            plugin.error(msg, e);
            throw new IOConfigurationException(msg, e);
        }
    }
}
