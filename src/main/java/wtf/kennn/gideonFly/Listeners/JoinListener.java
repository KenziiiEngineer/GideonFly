package wtf.kennn.gideonFly.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import wtf.kennn.gideonFly.Apis.LuckPermsHook;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Managers.EffectManager;
import wtf.kennn.gideonFly.Utils.SoundUtil;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class JoinListener implements Listener {

    private final GideonFly plugin;

    public JoinListener(GideonFly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.joinMessage(null);

        var cfg = plugin.getYml().config();
        var spwn = plugin.getYml().spawn();

        String lpPrefix = LuckPermsHook.getPrefix(p);
        String prefix = cfg.getString("prefix", "&b[GIDEONFLY]&f ");

        // ===============================
        // 🟡 SISTEMA VIP
        // ===============================
        if (p.hasPermission("gideonfly.vip")) {

            // ✅ Solo manda broadcast si está activado
            if (cfg.getBoolean("vip-broadcast", true)) {
                String vipMsg = safeReplace(
                        cfg.getString("join-vip-broadcast",
                                "&6⭐ %luckperms_prefix%%player_name% &ehas arrived from the sky!"),
                        p.getName(), lpPrefix, prefix
                );
                Bukkit.broadcastMessage(colorize(vipMsg));
            }

            // ✈️ Activar vuelo automático si está habilitado
            if (cfg.getBoolean("vip-auto-fly", true)) {
                p.setAllowFlight(true);
                p.setFlying(true);
            }

            // 💬 Mensaje personal
            String entryMsg = cfg.getString("messages.vip-entry",
                    "%prefix% &bWelcome VIP! Your flight is automatically enabled.");
            send(p, colorize(entryMsg.replace("%prefix%", prefix)));
            play(p, SoundUtil.SoundType.SUCCESS);

            // 📍 Teleportar al spawn
            if (spwn != null && spwn.contains("world")) {
                var world = Bukkit.getWorld(spwn.getString("world"));
                if (world != null) {
                    double x = spwn.getDouble("x", p.getLocation().getX());
                    double y = spwn.getDouble("y", p.getLocation().getY());
                    double z = spwn.getDouble("z", p.getLocation().getZ());

                    float yaw = spwn.contains("yaw") ? ((Number) spwn.get("yaw")).floatValue() : p.getLocation().getYaw();
                    float pitch = spwn.contains("pitch") ? ((Number) spwn.get("pitch")).floatValue() : p.getLocation().getPitch();

                    p.teleport(new Location(world, x, y, z, yaw, pitch));
                } else {
                    send(p, colorize(prefix + "&c⚠ The configured VIP spawn world does not exist."));
                }
            } else {
                send(p, colorize(prefix + "&c⚠ No VIP spawn configured."));
            }

            return; // 🛑 Evita que entre al broadcast global normal
        }

        // ===============================
        // 👤 JUGADORES NORMALES
        // ===============================
        if (cfg.getBoolean("join-messages", true)) {
            String msg = safeReplace(
                    cfg.getString("join-broadcast",
                            "&a✈ &e%luckperms_prefix%%player_name% &ahas joined the server!"),
                    p.getName(), lpPrefix, prefix
            );
            Bukkit.broadcastMessage(colorize(msg));
        }

        // ===============================
        // ✨ EFECTOS DE ENTRADA
        // ===============================
        String effectType = plugin.getPlayerDataManager().getEffect(p);
        if (!effectType.equalsIgnoreCase("NINGUNO")) {
            Bukkit.getScheduler().runTaskLater(plugin,
                    () -> EffectManager.playEffect(p, effectType),
                    20L);
        }
    }

    // ===============================
    // 🧩 Reemplazo seguro
    // ===============================
    private String safeReplace(String msg, String playerName, String luckPermsPrefix, String prefix) {
        if (msg == null || msg.isBlank())
            msg = "&7%luckperms_prefix%%player_name% joined the server!";
        return msg
                .replace("%player_name%", playerName != null ? playerName : "")
                .replace("%luckperms_prefix%", luckPermsPrefix != null ? luckPermsPrefix : "")
                .replace("%prefix%", prefix != null ? prefix : "");
    }
}
