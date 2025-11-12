package wtf.kennn.gideonFly.Apis;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import wtf.kennn.gideonFly.GideonFly;

import static wtf.kennn.gideonFly.Utils.ChatUtil.log;

public class LuckPermsHook {

    private static LuckPerms api;


    public static void init(GideonFly plugin) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
                try {
                    RegisteredServiceProvider<LuckPerms> provider =
                            plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);

                    if (provider != null) {
                        api = provider.getProvider();
                        log("&aGideonFly detected: LuckPerms API hooked successfully!");
                        return;
                    }
                } catch (Exception e) {
                    log("&cFailed to hook into LuckPerms: " + e.getMessage());
                }
            }

            api = null;
            log("&eLuckPerms not detected or API unavailable.");
        }, 20L);
    }

    // 🔹 Obtener el prefijo del jugador (seguro)
    public static String getPrefix(Player player) {
        if (api == null) return "";
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return "";

        var meta = user.getCachedData().getMetaData();
        String prefix = meta.getPrefix();
        return prefix != null ? prefix : "";
    }


    public static boolean isHooked() {
        return api != null;
    }


    public static LuckPerms getAPI() {
        return api;
    }
}
