package wtf.kennn.gideonFly.Managers;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.entity.Player;
import wtf.kennn.gideonFly.GideonFly;

import java.io.IOException;
import java.util.UUID;

public class PlayerDataManager {

    private final GideonFly plugin;
    private final YamlDocument dataFile;

    public PlayerDataManager(GideonFly plugin) {
        this.plugin = plugin;
        this.dataFile = plugin.getYml().pdata(); // ✅ Eliminado .raw() — pdata() ya devuelve YamlDocument
    }

    /**
     * 💾 Guarda el efecto seleccionado por el jugador
     *
     * @param player Jugador
     * @param effectType Tipo de efecto elegido
     */
    public void setEffect(Player player, String effectType) {
        UUID uuid = player.getUniqueId();
        try {
            dataFile.set(uuid.toString() + ".effect", effectType.toUpperCase());
            dataFile.save(); // 🔒 Guarda el archivo
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Error al guardar los datos del jugador " + player.getName());
            e.printStackTrace();
        }
    }

    /**
     * 📦 Obtiene el efecto actual del jugador
     *
     * @param player Jugador
     * @return Efecto guardado o "NINGUNO" si no hay
     */
    public String getEffect(Player player) {
        UUID uuid = player.getUniqueId();
        String effect = dataFile.getString(uuid.toString() + ".effect", "NINGUNO");
        return effect != null ? effect.toUpperCase() : "NINGUNO";
    }

    /**
     * 🧹 Limpia todos los datos del jugador
     *
     * @param player Jugador
     */
    public void clear(Player player) {
        UUID uuid = player.getUniqueId();
        try {
            dataFile.set(uuid.toString(), null);
            dataFile.save();
        } catch (IOException e) {
            plugin.getLogger().severe("⚠ Error al limpiar los datos de " + player.getName());
            e.printStackTrace();
        }
    }

    /**
     * ♻️ Recarga el archivo de datos desde el disco
     */
    public void reload() {
        try {
            dataFile.reload();
            plugin.getLogger().info("♻️ Datos de jugadores recargados correctamente.");
        } catch (IOException e) {
            plugin.getLogger().severe("⚠ Error al recargar los datos de jugadores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 💽 Guarda manualmente el archivo (por seguridad)
     */
    public void save() {
        try {
            dataFile.save();
        } catch (IOException e) {
            plugin.getLogger().severe("⚠ No se pudo guardar el archivo de datos.");
            e.printStackTrace();
        }
    }

    /**
     * ✅ Verifica si el jugador tiene un efecto guardado
     *
     * @param player Jugador
     * @return true si tiene un efecto asignado distinto de NINGUNO
     */
    public boolean hasEffect(Player player) {
        String effect = getEffect(player);
        return effect != null && !effect.equalsIgnoreCase("NINGUNO");
    }
}
