package wtf.kennn.gideonFly.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import wtf.kennn.gideonFly.Apis.LuckPermsHook;
import wtf.kennn.gideonFly.GideonFly;

import static wtf.kennn.gideonFly.Utils.ChatUtil.colorize;

public class LeaveListener implements Listener {

    private final GideonFly plugin;

    public LeaveListener(GideonFly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // ❌ Elimina el mensaje por defecto de Minecraft
        e.quitMessage(null);

        // 📦 Archivo BoostedYAML
        var cfg = plugin.getYml().config(); // ✅ No es cfg(), es config()

        // 🚫 Si está desactivado en la config, salir
        // BoostedYAML no acepta default value en getBoolean, así que lo manejamos manualmente
        Boolean leaveEnabled = cfg.getBoolean("leave-messages.enabled");
        if (leaveEnabled == null) leaveEnabled = true; // valor por defecto
        if (!leaveEnabled) return;

        var player = e.getPlayer();
        String lpPrefix = LuckPermsHook.getPrefix(player);
        String playerName = player.getName();

        // 💬 Mensaje personalizado desde config.yml
        String msg = cfg.getString("leave-messages.message");
        if (msg == null || msg.isEmpty())
            msg = "&c✈ %luckperms_prefix%%player_name% ha salido del servidor!";

        // 🧠 Reemplazos seguros
        msg = msg
                .replace("%player_name%", playerName != null ? playerName : "")
                .replace("%luckperms_prefix%", lpPrefix != null ? lpPrefix : "");

        // 🎨 Enviar mensaje coloreado
        Bukkit.broadcastMessage(colorize(msg));
    }
}
