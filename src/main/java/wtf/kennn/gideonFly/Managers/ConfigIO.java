package wtf.kennn.gideonFly.Managers;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.kennn.gideonFly.GideonFly;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class ConfigIO {

    private final JavaPlugin plugin;
    private final String fileName;
    private final YamlDocument document;

    public ConfigIO(JavaPlugin plugin, String fileName) throws Exception {
        this.plugin = plugin;
        this.fileName = fileName;

        File file = new File(plugin.getDataFolder(), fileName);
        InputStream defaults = plugin.getResource(fileName);

        // 🧹 FIX: limpia BOM o caracteres invisibles al inicio del archivo
        try {
            if (file.exists()) {
                byte[] bytes = Files.readAllBytes(file.toPath());
                String content = new String(bytes, StandardCharsets.UTF_8);
                if (content.startsWith("\uFEFF")) {
                    plugin.getLogger().warning("⚠ Removed invisible BOM character from " + fileName);
                    Files.writeString(file.toPath(), content.replace("\uFEFF", ""), StandardCharsets.UTF_8);
                }
            }
        } catch (Exception ex) {
            plugin.getLogger().warning("⚠ Could not clean BOM from " + fileName + ": " + ex.getMessage());
        }

        // ✅ Crea el archivo si no existe
        if (!file.exists() && defaults != null) {
            plugin.saveResource(fileName, false);
        }

        // ✅ Carga segura de BoostedYAML (sin romper por comentarios)
        this.document = YamlDocument.create(
                file,
                GeneralSettings.DEFAULT,
                LoaderSettings.builder()
                        .setAutoUpdate(false)
                        .build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.DEFAULT
        );

        document.update();
        document.save();
    }

    // ======================================================
    // 📘 MÉTODOS DE LECTURA / ESCRITURA
    // ======================================================

    public String str(String path, String fallback) {
        String value = document.getString(path);
        return (value == null || value.isBlank()) ? fallback : value;
    }

    public List<String> list(String path, List<String> fallback) {
        List<String> list = document.getStringList(path);
        return (list == null || list.isEmpty()) ? fallback : list;
    }

    public boolean bool(String path, boolean fallback) {
        try {
            return document.getBoolean(path);
        } catch (Exception e) {
            return fallback;
        }
    }

    public int integer(String path, int fallback) {
        try {
            return document.getInt(path);
        } catch (Exception e) {
            return fallback;
        }
    }

    public void set(String path, Object value) throws Exception {
        document.set(path, value);
        document.save();
    }

    public void reload() throws Exception {
        document.reload();
        document.update();
        document.save();
    }

    public YamlDocument raw() {
        return document;
    }
}
