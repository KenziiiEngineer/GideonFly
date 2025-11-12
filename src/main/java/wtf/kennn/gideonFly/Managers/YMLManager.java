package wtf.kennn.gideonFly.Managers;

import dev.dejvokep.boostedyaml.YamlDocument;
import wtf.kennn.gideonFly.GideonFly;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class YMLManager {

    private final YamlDocument config;
    private final YamlDocument spawn;
    private final YamlDocument pdata;

    public YMLManager(GideonFly plugin) {
        try {
            this.config = YamlDocument.create(
                    new File(plugin.getDataFolder(), "config.yml"),
                    Objects.requireNonNull(plugin.getResource("config.yml"))
            );
            this.spawn = YamlDocument.create(
                    new File(plugin.getDataFolder(), "spawn.yml"),
                    Objects.requireNonNull(plugin.getResource("spawn.yml"))
            );
            this.pdata = YamlDocument.create(
                    new File(plugin.getDataFolder(), "playerdata.yml")
            );
        } catch (IOException e) {
            throw new RuntimeException("No se pudieron cargar los YAMLs", e);
        }
    }

    public YamlDocument config()   { return config; }
    public YamlDocument spawn()    { return spawn; }
    public YamlDocument pdata()    { return pdata; }
}
