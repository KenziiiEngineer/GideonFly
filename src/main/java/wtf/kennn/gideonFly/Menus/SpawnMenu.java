package wtf.kennn.gideonFly.Menus;

import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Utils.ChatUtil;
import wtf.kennn.gideonFly.Utils.SoundUtil;

import java.util.List;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class SpawnMenu implements Listener {

    private final GideonFly plugin;
    private final Player player;
    private final Inventory menu;

    public SpawnMenu(Player player) {
        this.plugin = GideonFly.getInstance();
        this.player = player;

        // 📖 Cargar título desde config.yml (Boosted)
        YamlDocument cfg = plugin.getYml().config();
        String title = cfg.getString("menus.spawn.title");
        if (title == null || title.isEmpty())
            title = "&b&lGideonFly &8| &fVIP Spawn";

        this.menu = Bukkit.createInventory(null, 27, colorize(title));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupItems();
    }

    /** 🧱 Carga los ítems del menú desde config.yml Boosted */
    private void setupItems() {
        YamlDocument cfg = plugin.getYml().config();

        menu.setItem(11, createItem(
                Material.EMERALD_BLOCK,
                getSafe(cfg, "menus.spawn.items.set.name", "&a✅ Set VIP Spawn"),
                cfg.getStringList("menus.spawn.items.set.lore")
        ));

        menu.setItem(15, createItem(
                Material.REDSTONE_BLOCK,
                getSafe(cfg, "menus.spawn.items.remove.name", "&c❌ Remove VIP Spawn"),
                cfg.getStringList("menus.spawn.items.remove.lore")
        ));

        menu.setItem(22, createItem(
                Material.ARROW,
                getSafe(cfg, "menus.spawn.items.back.name", "&e⬅ Back"),
                cfg.getStringList("menus.spawn.items.back.lore")
        ));
    }

    private ItemStack createItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize(name != null ? name : "&7Unnamed"));
        meta.setLore((lore == null || lore.isEmpty())
                ? List.of(colorize("&7No description"))
                : lore.stream().map(ChatUtil::colorize).toList());
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        player.openInventory(menu);
        play(player, SoundUtil.SoundType.OPEN_MENU);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(menu)) return;
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!p.getUniqueId().equals(player.getUniqueId())) return;
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;

        e.setCancelled(true);
        play(p, SoundUtil.SoundType.CLICK);

        switch (e.getCurrentItem().getType()) {
            case EMERALD_BLOCK -> saveVIPSpawn(p);
            case REDSTONE_BLOCK -> removeVIPSpawn(p);
            case ARROW -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> new SetupMenu(p).open(), 2L);
            }
        }
    }

    /** 💾 Guarda el VIP spawn en spawn.yml Boosted */
    private void saveVIPSpawn(Player p) {
        try {
            YamlDocument spawnDoc = plugin.getYml().spawn();
            var loc = p.getLocation();

            spawnDoc.set("world", loc.getWorld().getName());
            spawnDoc.set("x", loc.getX());
            spawnDoc.set("y", loc.getY());
            spawnDoc.set("z", loc.getZ());
            spawnDoc.set("yaw", loc.getYaw());
            spawnDoc.set("pitch", loc.getPitch());
            spawnDoc.save();
            spawnDoc.reload();

            YamlDocument cfg = plugin.getYml().config();
            String prefix = getSafe(cfg, "prefix", "&b[GIDEONFLY]&f ");
            String msg = getSafe(cfg, "messages.config-saved", "&aVIP Spawn saved successfully!");
            msg = msg.replace("%prefix%", prefix);

            send(p, colorize(msg));
            play(p, SoundUtil.SoundType.SUCCESS);

        } catch (Exception ex) {
            send(p, "&c⚠ Error saving spawn.yml!");
            ex.printStackTrace();
        }
    }

    /** ❌ Elimina el spawn VIP */
    private void removeVIPSpawn(Player p) {
        try {
            YamlDocument spawnDoc = plugin.getYml().spawn();

            for (String key : List.of("world", "x", "y", "z", "yaw", "pitch")) {
                spawnDoc.set(key, null);
            }
            spawnDoc.save();
            spawnDoc.reload();

            YamlDocument cfg = plugin.getYml().config();
            String prefix = getSafe(cfg, "prefix", "&b[GIDEONFLY]&f ");
            String msg = getSafe(cfg, "messages.config-removed", "&cVIP Spawn removed!");
            msg = msg.replace("%prefix%", prefix);

            send(p, colorize(msg));
            play(p, SoundUtil.SoundType.ERROR);

        } catch (Exception ex) {
            send(p, "&c⚠ Error saving spawn.yml!");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getInventory().equals(menu)) return;
        if (!e.getPlayer().getUniqueId().equals(player.getUniqueId())) return;
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!e.getPlayer().getUniqueId().equals(player.getUniqueId())) return;
        HandlerList.unregisterAll(this);
    }

    // =========================================================
    // 🧩 Helper para valores seguros
    // =========================================================
    private String getSafe(YamlDocument doc, String path, String fallback) {
        String s = doc.getString(path);
        return (s == null || s.isEmpty()) ? fallback : s;
    }
}
