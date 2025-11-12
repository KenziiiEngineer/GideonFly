package wtf.kennn.gideonFly.Menus;

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

public class SaveMenu implements Listener {

    private final GideonFly plugin;
    private final Player player;
    private final Inventory menu;

    public SaveMenu(Player player) {
        this.plugin = GideonFly.getInstance();
        this.player = player;

        // 📖 Título del menú desde config.yml (Boosted)
        var cfg = plugin.getYml().config();
        String title = cfg.getString("menus.config.title");
        if (title == null || title.isEmpty())
            title = "&b&lGideonFly &8| &fConfig Manager";

        this.menu = Bukkit.createInventory(null, 27, colorize(title));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupItems();
    }

    /** 🧱 Carga los ítems del menú desde config.yml (BoostedYAML) */
    private void setupItems() {
        var cfg = plugin.getYml().config();

        menu.setItem(11, createItem(
                Material.EMERALD_BLOCK,
                getSafe(cfg, "menus.config.items.save.name", "&a💾 Save Configuration"),
                cfg.getStringList("menus.config.items.save.lore")
        ));

        menu.setItem(15, createItem(
                Material.BOOK,
                getSafe(cfg, "menus.config.items.reload.name", "&b🔄 Reload Configuration"),
                cfg.getStringList("menus.config.items.reload.lore")
        ));

        menu.setItem(22, createItem(
                Material.ARROW,
                getSafe(cfg, "menus.config.items.back.name", "&e⬅ Back"),
                cfg.getStringList("menus.config.items.back.lore")
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

            // 💾 SAVE CONFIGS
            case EMERALD_BLOCK -> {
                try {
                    plugin.getYml().config().save();
                    plugin.getYml().spawn().save();
                    plugin.getYml().pdata().save();

                    var cfg = plugin.getYml().config();
                    String prefix = getSafe(cfg, "prefix", "&b[GIDEONFLY]&f ");
                    String msg = getSafe(cfg, "messages.config-saved",
                            "&aAll configurations saved successfully!");
                    msg = msg.replace("%prefix%", prefix);

                    send(p, colorize(msg));
                    play(p, SoundUtil.SoundType.SUCCESS);
                    p.closeInventory();

                } catch (Exception ex) {
                    send(p, "&c⚠ Error saving configurations!");
                    ex.printStackTrace();
                }
            }

            // 🔄 RELOAD CONFIGS
            case BOOK -> {
                try {
                    plugin.getYml().config().reload();
                    plugin.getYml().spawn().reload();
                    plugin.getYml().pdata().reload();

                    plugin.reinitCoreListeners();

                    var cfg = plugin.getYml().config();
                    String prefix = getSafe(cfg, "prefix", "&b[GIDEONFLY]&f ");
                    String msg = getSafe(cfg, "messages.config-reloaded",
                            "&eAll configurations reloaded successfully!");
                    msg = msg.replace("%prefix%", prefix);

                    send(p, colorize(msg));
                    play(p, SoundUtil.SoundType.SUCCESS);

                    // Reabrir menú principal después de recargar
                    Bukkit.getScheduler().runTaskLater(plugin, () -> new SetupMenu(p).open(), 5L);

                } catch (Exception ex) {
                    send(p, "&c⚠ Error reloading configurations!");
                    ex.printStackTrace();
                }
            }

            // ⬅ BACK
            case ARROW -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> new SetupMenu(p).open(), 2L);
            }
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
    private String getSafe(dev.dejvokep.boostedyaml.YamlDocument doc, String path, String fallback) {
        String s = doc.getString(path);
        return (s == null || s.isEmpty()) ? fallback : s;
    }
}
