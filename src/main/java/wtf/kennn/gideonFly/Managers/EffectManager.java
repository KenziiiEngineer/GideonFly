package wtf.kennn.gideonFly.Managers;

import org.bukkit.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Listeners.VolcanoCleaner;
import wtf.kennn.gideonFly.Utils.KeyUtil;

import java.util.Random;

public class EffectManager {

    private static final Random RANDOM = new Random();

    /**
     * 🔥 Reproduce el efecto según el tipo guardado del jugador.
     */
    public static void playEffect(Player player, String effectType) {
        if (effectType == null || effectType.equalsIgnoreCase("NINGUNO")) return;

        effectType = effectType.toUpperCase();
        Location loc = player.getLocation();
        World world = loc.getWorld();

        switch (effectType) {
            case "RAYO" -> lightningEffect(world, loc);
            case "FUEGO" -> fireworkEffect(world, loc);
            case "VOLCAN" -> spawnWoolVolcano(player);
            default -> Bukkit.getLogger().warning("[GIDEONFLY] Unknown effect type: " + effectType);
        }
    }

    /**
     * ⚡ RAYO (solo efecto visual, sin daño)
     */
    private static void lightningEffect(World world, Location loc) {
        world.strikeLightningEffect(loc);
        world.spawnParticle(Particle.ELECTRIC_SPARK, loc, 25, 0.5, 1, 0.5, 0.1);
        world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.7f, 1.4f);
    }

    /**
     * 🎆 FUEGO ARTIFICIAL aleatorio
     */
    private static void fireworkEffect(World world, Location loc) {
        Firework fw = world.spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();

        Color[] colors = {Color.RED, Color.AQUA, Color.LIME, Color.FUCHSIA, Color.YELLOW, Color.ORANGE};
        meta.addEffect(FireworkEffect.builder()
                .withColor(colors[RANDOM.nextInt(colors.length)])
                .withFade(Color.WHITE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build());
        meta.setPower(0);
        fw.setFireworkMeta(meta);

        Bukkit.getScheduler().runTaskLater(GideonFly.getInstance(), fw::detonate, 10L);
    }

    /**
     * 🌋 VOLCÁN DE LANA — efecto tipo CubeCraft
     */
    public static void spawnWoolVolcano(Player player) {
        World world = player.getWorld();
        GideonFly plugin = GideonFly.getInstance();

        NamespacedKey TAG = KeyUtil.GF_VOLCANO;

        Material[] colors = {
                Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
                Material.LIME_WOOL, Material.LIGHT_BLUE_WOOL,
                Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.PINK_WOOL, Material.WHITE_WOOL
        };

        final int durationTicks = 60; // duración del efecto: 3 segundos
        final int interval = 3;       // cada 3 ticks (≈0.15s)

        new BukkitRunnable() {
            int elapsed = 0;

            @Override
            public void run() {
                if (!player.isOnline() || elapsed >= durationTicks) {
                    Location end = player.getEyeLocation().clone().add(0, 0.5, 0);
                    world.playSound(end, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1.3f);
                    world.spawnParticle(Particle.CLOUD, end, 50, 1, 0.5, 1, 0.05);
                    cancel();
                    return;
                }

                // 📍 Desde la cabeza (no tapa la vista del jugador)
                Location base = player.getEyeLocation().clone().add(0, 0.4, 0);
                int count = 10 + RANDOM.nextInt(5);

                for (int i = 0; i < count; i++) {
                    Material wool = colors[RANDOM.nextInt(colors.length)];
                    Location spawn = base.clone().add(
                            (RANDOM.nextDouble() - 0.5) * 0.8,
                            RANDOM.nextDouble() * 0.6,
                            (RANDOM.nextDouble() - 0.5) * 0.8
                    );

                    FallingBlock fb = world.spawnFallingBlock(spawn, wool.createBlockData());
                    fb.setVelocity(new Vector(
                            (RANDOM.nextDouble() - 0.5) * 0.9,
                            RANDOM.nextDouble() * 1.6 + 1.0,
                            (RANDOM.nextDouble() - 0.5) * 0.9
                    ));
                    fb.setDropItem(false);
                    fb.setHurtEntities(false);

                    fb.getPersistentDataContainer().set(TAG, PersistentDataType.BYTE, (byte) 1);

                    // Partículas y sonido local
                    world.spawnParticle(Particle.LAVA, spawn, 8, 0.3, 0.4, 0.3, 0.02);
                    world.spawnParticle(Particle.CRIT, spawn, 6, 0.4, 0.4, 0.4, 0.02);
                    world.playSound(spawn, Sound.BLOCK_LAVA_POP, 0.8f, 1.2f + RANDOM.nextFloat() * 0.3f);

                    // Limpieza individual del bloque tras 20s
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (fb.isValid() && !fb.isDead()) fb.remove();
                    }, 20L * 20);
                }

                // Sonido global del efecto
                world.playSound(player.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 0.7f, 1.5f);
                elapsed += interval;
            }
        }.runTaskTimer(plugin, 0L, interval);

        // ✅ Limpieza extra global por si queda algo sin borrar
        Bukkit.getScheduler().runTaskLater(plugin, () -> VolcanoCleaner.cleanAll(plugin), 20L * 25);
    }
}
