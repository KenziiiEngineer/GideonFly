
package wtf.kennn.gideonFly.Utils;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class KeyUtil {
    public static NamespacedKey GF_VOLCANO;

    private KeyUtil() {}
    public static void init(JavaPlugin plugin) {
        GF_VOLCANO = new NamespacedKey(plugin, "gf_volcano");
    }
}
