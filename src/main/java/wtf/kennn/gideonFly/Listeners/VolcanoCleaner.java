package wtf.kennn.gideonFly.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.persistence.PersistentDataType;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Utils.KeyUtil;

public class VolcanoCleaner {

    /**
     * 🧹 Limpia todos los FallingBlocks creados por el efecto "VOLCAN"
     * que tengan la tag personalizada gf_volcano.
     */
    public static void cleanAll(GideonFly plugin) {
        int removed = 0;

        for (World world : Bukkit.getWorlds()) {
            for (FallingBlock fb : world.getEntitiesByClass(FallingBlock.class)) {
                if (!fb.isValid() || fb.isDead()) continue;

                if (fb.getPersistentDataContainer().has(KeyUtil.GF_VOLCANO, PersistentDataType.BYTE)) {
                    fb.remove();
                    removed++;
                }
            }
        }

        if (removed > 0)
            plugin.getLogger().info("🧹 VolcanoCleaner removed " + removed + " falling blocks.");
    }
}
