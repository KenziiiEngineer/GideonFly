package wtf.kennn.gideonFly.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Utils.SoundUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class PrefixChatListener implements Listener {

    private static final Set<Player> waitingPrefix = new HashSet<>();

    /**
     * 🔧 Agrega al jugador a la lista de espera para definir el prefijo.
     */
    public static void addWaiting(Player player) {
        waitingPrefix.add(player);
        var cfg = GideonFly.getInstance().getYml().config(); // ✅ usamos config() directamente

        String msg = cfg.getString("menus.prefix.message");
        if (msg == null || msg.isEmpty()) {
            msg = "&e✏ Escribe tu nuevo prefijo en el chat o 'cancel' para cancelar.";
        }

        send(player, colorize(msg));
        play(player, SoundUtil.SoundType.CLICK);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!waitingPrefix.contains(player)) return;

        event.setCancelled(true);
        String message = event.getMessage().trim();

        // 🛑 Cancelar
        if (message.equalsIgnoreCase("cancel")) {
            waitingPrefix.remove(player);
            sendSafe(player, "menus.prefix.cancel", "&cConfiguración del prefijo cancelada.");
            play(player, SoundUtil.SoundType.ERROR);
            return;
        }

        // ⚙ Guardar en el hilo principal
        Bukkit.getScheduler().runTask(GideonFly.getInstance(), () -> {
            try {
                GideonFly plugin = GideonFly.getInstance();

                // 🧠 Guarda el nuevo prefijo en config.yml
                var cfg = plugin.getYml().config();
                cfg.set("prefix", message);
                cfg.save();
                cfg.reload();

                // ♻️ Actualiza los listeners / sistemas dependientes
                plugin.reinitCoreListeners();

                waitingPrefix.remove(player);

                String success = cfg.getString("menus.prefix.success");
                if (success == null || success.isEmpty()) {
                    success = "&a✅ Prefijo actualizado correctamente! Nuevo prefijo: &f%prefix%";
                }

                send(player, colorize(success.replace("%prefix%", message)));
                play(player, SoundUtil.SoundType.SUCCESS);

            } catch (IOException e) {
                e.printStackTrace();
                send(player, "&c⚠ Error al guardar el nuevo prefijo!");
                play(player, SoundUtil.SoundType.ERROR);
            }
        });
    }

    /**
     * 🧩 Envía texto seguro con fallback.
     */
    private static void sendSafe(Player p, String path, String fallback) {
        var cfg = GideonFly.getInstance().getYml().config();
        String text = cfg.getString(path);
        if (text == null || text.isEmpty()) text = fallback;
        send(p, colorize(text));
    }
}
