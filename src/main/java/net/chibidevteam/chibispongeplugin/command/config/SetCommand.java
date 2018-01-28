package net.chibidevteam.chibispongeplugin.command.config;

import java.util.HashMap;
import java.util.Map;

import net.chibidevteam.chibispongeplugin.ChibiSpongePlugin;

public class SetCommand extends AbstractSetCommand {
    private static final String USES_PERMISIONS = "usePermissions";
    private static final String AUTOSAVE        = "autoSave";

    public SetCommand(String permission) {
        super(permission);
        this.autoSaveKey = AUTOSAVE;
        this.baseConfigPath = "plugin";
        this.configId = ChibiSpongePlugin.MAIN_CONFIG_ID;
    }

    @Override
    protected Map<String, Class<?>> getOptionalArguments() {
        Map<String, Class<?>> result = new HashMap<>();

        result.put(AUTOSAVE, Boolean.class);
        result.put(USES_PERMISIONS, Boolean.class);

        return result;
    }

}
