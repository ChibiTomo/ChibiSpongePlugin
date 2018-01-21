package net.chibidevteam.chibispongeplugin.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.text.Text;

public class MessageUtils {
    private static final String        UNKNOWN_KEY_WRAPPER = "???";

    private static Map<String, Text>   txtCache            = new HashMap<>();
    private static Map<String, String> cache               = new HashMap<>();

    private static boolean             loaded;

    private static ResourceBundle      defaultRb;
    private static ResourceBundle      rb;

    private MessageUtils() {
    }

    public static void reload() {
        defaultRb = ResourceBundle.getBundle("chibiplugin_messages", Locale.getDefault());
        rb = ResourceBundle.getBundle("messages", Locale.getDefault());
        loaded = true;
    }

    public static String format(String str, Object... objects) {
        String result = str;

        Pattern pat;
        Matcher matcher;
        Object obj;
        for (int i = 0; i < objects.length; ++i) {
            pat = Pattern.compile("\\{" + i + "\\}");
            matcher = pat.matcher(result);

            obj = objects[i];
            result = matcher.replaceAll(obj == null ? (String) obj : obj.toString());
        }
        return result;
    }

    public static String get(String key, Object... objects) {
        if (!loaded) {
            reload();
        }

        String text = cache.get(key);
        if (text != null) {
            return text;
        }

        text = getNotFoundText(key, objects);
        if (rb != null && rb.containsKey(key)) {
            text = rb.getString(key);
        } else if (defaultRb.containsKey(key)) {
            text = defaultRb.getString(key);
        }

        text = format(text, objects);
        cache.put(key, text);
        return text;
    }

    private static String getNotFoundText(String key, Object... objects) {
        String text = UNKNOWN_KEY_WRAPPER + key + UNKNOWN_KEY_WRAPPER;
        String[] array = new String[objects.length];
        if (array.length > 0) {
            text += ": ";
            for (int i = 0; i < objects.length; ++i) {
                text += "{" + i + "}";
            }
        }

        return text + StringUtils.join(array, ", ");
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
