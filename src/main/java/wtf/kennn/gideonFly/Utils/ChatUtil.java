package wtf.kennn.gideonFly.Utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import wtf.kennn.gideonFly.GideonFly;
import dev.dejvokep.boostedyaml.YamlDocument;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ChatUtil {

    // 🎨 Soporte para colores hexadecimales tipo #FFFFFF
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    /**
     * ✨ Convierte texto con & y #RRGGBB a formato coloreado válido.
     * También reemplaza dinámicamente %prefix% desde config.yml.
     */
    public static String colorize(String message) {
        if (message == null) return "";

        // Obtiene prefix desde BoostedYAML config.yml
        String prefix = "";
        try {
            YamlDocument cfg = GideonFly.getInstance().getYml().config();
            prefix = cfg.getString("prefix");
        } catch (Exception ignored) {}
        if (prefix == null) prefix = "&b[GIDEONFLY]&f ";

        message = message.replace("%prefix%", prefix);

        // Hex color (#RRGGBB)
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String color = matcher.group();
            message = message.replace(color, ChatColor.of(color).toString());
        }

        // & color codes
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * 💬 Envía mensaje coloreado a un jugador o consola.
     */
    public static void send(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(colorize(message));
    }

    /**
     * 📢 Envía un broadcast global a todos los jugadores.
     */
    public static void broadcast(String message) {
        if (message == null || message.isEmpty()) return;
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(colorize(message)));
    }

    /**
     * 🧾 Envía un mensaje formateado a la consola con soporte de & y #HEX.
     */
    public static void log(String message) {
        if (message == null || message.isEmpty()) return;
        Bukkit.getConsoleSender().sendMessage(colorize(message));
    }

    // Alias simple
    public static String color(String message) {
        return colorize(message);
    }

    /**
     * 🎨 Colorea todas las líneas de una lista (útil para lore).
     */
    public static List<String> color(List<String> lines) {
        if (lines == null || lines.isEmpty()) return List.of();
        return lines.stream().map(ChatUtil::colorize).collect(Collectors.toList());
    }

    /**
     * 📖 Obtiene texto de config.yml con color y fallback seguro.
     */
    public static String getMenuText(String path) {
        String text = null;
        try {
            text = GideonFly.getInstance().getYml().config().getString(path);
        } catch (Exception ignored) {}
        if (text == null || text.isEmpty())
            text = "&c[MISSING: " + path + "]";
        return colorize(text);
    }

    /**
     * 🧩 Obtiene lista (ej. lore) desde config.yml con color y fallback.
     */
    public static List<String> getMenuList(String path) {
        List<String> list = null;
        try {
            list = GideonFly.getInstance().getYml().config().getStringList(path);
        } catch (Exception ignored) {}
        if (list == null || list.isEmpty())
            return List.of(colorize("&c(MISSING LIST: " + path + ")"));
        return list.stream().map(ChatUtil::colorize).collect(Collectors.toList());
    }
}
