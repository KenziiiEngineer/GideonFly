package wtf.kennn.gideonFly.Managers;

import org.bukkit.plugin.java.JavaPlugin;

public class BYamlManager {

    private final ConfigIO config;
    private final ConfigIO spawn;
    private final ConfigIO playerData;

    public BYamlManager(JavaPlugin plugin) throws Exception {
        plugin.getDataFolder().mkdirs();

        this.config = new ConfigIO(plugin, "config.yml");
        this.spawn = new ConfigIO(plugin, "spawn.yml");
        this.playerData = new ConfigIO(plugin, "playerdata.yml");
    }

    // ======================================================
    // 📂 GETTERS
    // ======================================================

    public ConfigIO cfg() { return config; }     // Config general
    public ConfigIO spwn() { return spawn; }     // Spawn VIP
    public ConfigIO pdat() { return playerData; } // Datos de jugadores

    public void reloadAll() throws Exception {
        config.reload();
        spawn.reload();
        playerData.reload();
    }
}
