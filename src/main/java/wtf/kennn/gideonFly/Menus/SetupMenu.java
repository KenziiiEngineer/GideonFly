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

public class SetupMenu implements Listener {

    private final GideonFly plugin;
    private final Player player;
    private Inventory menu;

    public SetupMenu(Player player) {
        this.plugin = GideonFly.getInstance();
        this.player = player;
        buildMenu(); // se genera con los valores actuales del config Boosted
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /** 🔧 Construye el inventario con los valores actuales del config Boosted */
    private void buildMenu() {
        var cfg = plugin.getYml().config(); // ✅ reemplaza .cfg() por .config()

        String title = cfg.getString("menus.setup.title");
        if (title == null || title.isEmpty())
            title = "&b&lGideonFly &8| &fSetup Menu";
        menu = Bukkit.createInventory(null, 27, colorize(title));

        // 🚫 Sin permiso
        if (!player.hasPermission("gideonfly.admin")) {
            menu.setItem(13, createItem(Material.BARRIER, "&c🚫 No permission",
                    List.of("&7You don’t have permission to open this menu.")));
            return;
        }

        // ✅ Ítems del menú desde Boosted config.yml
        menu.setItem(11, createItem(
                Material.COMPASS,
                getSafe(cfg, "menus.setup.items.spawn.name", "&b🧭 Set VIP Spawn"),
                cfg.getStringList("menus.setup.items.spawn.lore")
        ));

        menu.setItem(13, createItem(
                Material.PAPER,
                getSafe(cfg, "menus.setup.items.join.name", "&a💬 Join Messages"),
                cfg.getStringList("menus.setup.items.join.lore")
        ));

        menu.setItem(15, createItem(
                Material.NAME_TAG,
                getSafe(cfg, "menus.setup.items.prefix.name", "&d🏷️ Change Prefix"),
                cfg.getStringList("menus.setup.items.prefix.lore")
        ));

        menu.setItem(22, createItem(
                Material.REDSTONE,
                getSafe(cfg, "menus.setup.items.config.name", "&e💾 Save / Reload Config"),
                cfg.getStringList("menus.setup.items.config.lore")
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
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(menu)) return;
        if (!(event.getWhoClicked() instanceof Player p)) return;
        if (!p.getUniqueId().equals(player.getUniqueId())) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) return;

        event.setCancelled(true);
        play(p, SoundUtil.SoundType.CLICK);

        // 🔒 Bloqueo por permiso
        if (!p.hasPermission("gideonfly.admin")) {
            send(p, "&cYou don’t have permission to use this menu.");
            play(p, SoundUtil.SoundType.ERROR);
            return;
        }

        // 🧭 Acciones del menú
        switch (event.getCurrentItem().getType()) {
            case COMPASS -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> new SpawnMenu(p).open(), 2L);
            }
            case PAPER -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> new MessagesMenu(p).open(), 2L);
            }
            case NAME_TAG -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin,
                        () -> wtf.kennn.gideonFly.Listeners.PrefixChatListener.addWaiting(p), 2L);
            }
            case REDSTONE -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> new SaveMenu(p).open(), 2L);
            }
        }
    }

    /** 🔁 Permite reconstruir el menú tras un reload */
    public void refresh() {
        buildMenu();
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
