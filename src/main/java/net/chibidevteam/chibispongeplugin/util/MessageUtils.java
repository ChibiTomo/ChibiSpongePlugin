package net.chibidevteam.chibispongeplugin.util;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.text.Text;

public class MessageUtils {
    private static final String      UNKNOWN_KEY_WRAPPER = "???";

    private static Map<String, Text> txtCache               = new HashMap<>();

    private MessageUtils() {
    }

    public static String get(String key) {
        return UNKNOWN_KEY_WRAPPER + key + UNKNOWN_KEY_WRAPPER;
    }

    public static Text getText(String key) {
        Text txt = txtCache.get(key);
        if (txt == null) {
            txt = Text.of(get(key));
            txtCache.put(key, txt);
        }
        return txt;
    }
}
