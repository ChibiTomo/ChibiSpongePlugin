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

    public PluginConfiguration(ChibiSpongePlugin plugin, String name, String uri, String jarUrl) {
        this(plugin, name, Paths.get(uri), jarUrl);
    }

    public PluginConfiguration(ChibiSpongePlugin plugin, String name, Path path, String jarUrl) {
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

    public void reload() {
        rootNode = load(loader, path.toString());
    }

    public void save() {
        try {
            loader.save(rootNode);
        } catch (IOException e) {
            plugin.error(MessageUtils.get("config.error.cannotSave", path), e);
        }
    }

    public String getName() {
        return name;
    }

    private void reloadDefault() {
        if (StringUtils.isBlank(jarUrl)) {
            plugin.info(MessageUtils.get("config.noDefault", name));
            return;
        }
        Optional<Asset> optional = Sponge.getAssetManager().getAsset(plugin, jarUrl);
        if (optional.isPresent()) {
            URL url = optional.get().getUrl();
            ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder().setURL(url)
                    .build();
            rootNode = load(loader, jarUrl);
        } else {
            plugin.warn(MessageUtils.get("config.defaultNotFound", jarUrl));
        }
    }

    private ConfigurationNode load(ConfigurationLoader<CommentedConfigurationNode> loader, String path) {
        try {
            return loader.load();
        } catch (IOException e) {
            plugin.error(MessageUtils.get("config.error.cannotLoad", path), e);
            return null;
        }
    }
}
