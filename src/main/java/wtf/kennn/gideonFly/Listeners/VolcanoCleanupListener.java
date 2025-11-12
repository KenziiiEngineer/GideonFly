package wtf.kennn.gideonFly.Listeners;

import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.persistence.PersistentDataType;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Utils.KeyUtil;

public class VolcanoCleanupListener implements Listener {

    private final GideonFly plugin;

    public VolcanoCleanupListener(GideonFly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFallingBlockLand(EntityChangeBlockEvent e) {
        Entity ent = e.getEntity();
        if (!(ent instanceof FallingBlock fb)) return;

        // 🔍 Usa la key centralizada de KeyUtil
        if (!fb.getPersistentDataContainer().has(KeyUtil.GF_VOLCANO, PersistentDataType.BYTE)) return;

        // ❌ Evita que el FallingBlock se convierta en bloque
        e.setCancelled(true);

        // 💨 Efecto visual bonito al desaparecer
        fb.getWorld().spawnParticle(
                Particle.CLOUD,
                fb.getLocation().add(0, 0.3, 0),
                10, 0.25, 0.25, 0.25, 0.01
        );

        fb.remove();
    }
}
