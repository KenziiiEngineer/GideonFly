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
import wtf.kennn.gideonFly.Managers.EffectManager;
import wtf.kennn.gideonFly.Utils.ChatUtil;
import wtf.kennn.gideonFly.Utils.SoundUtil;

import java.util.List;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class JoinEffectsMenu implements Listener {

    private final GideonFly plugin;
    private final Player player;
    private final Inventory menu;

    public JoinEffectsMenu(Player player) {
        this.plugin = GideonFly.getInstance();
        this.player = player;

        var cfg = plugin.getYml().config();
        String title = cfg.getString("menus.effects.title");
        if (title == null || title.isEmpty())
            title = "&b&lGideonFly &8| &fJoin Effects";

        this.menu = Bukkit.createInventory(null, 27, colorize(title));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupItems();
    }

    /** 🧱 Carga los ítems del menú desde config.yml Boosted */
    private void setupItems() {
        var cfg = plugin.getYml().config();

        // 🔒 Sin permiso → advertencia
        if (!player.hasPermission("gideonfly.joineffects")) {
            menu.setItem(13, createItem(Material.BARRIER,
                    "&c🚫 No permission",
                    List.of("&7You don’t have permission to use join effects.")));
            return;
        }

        // ✅ Cada ítem se lee desde Boosted config.yml
        menu.setItem(10, createItem(Material.LIGHTNING_ROD,
                getSafe(cfg, "menus.effects.rayo.name", "&e⚡ Friendly Lightning"),
                cfg.getStringList("menus.effects.rayo.lore")));

        menu.setItem(12, createItem(Material.FIREWORK_ROCKET,
                getSafe(cfg, "menus.effects.fuego.name", "&d🎇 Firework"),
                cfg.getStringList("menus.effects.fuego.lore")));

        menu.setItem(14, createItem(Material.RED_WOOL,
                getSafe(cfg, "menus.effects.volcan.name", "&c🌋 Wool Volcano"),
                cfg.getStringList("menus.effects.volcan.lore")));

        menu.setItem(16, createItem(Material.BARRIER,
                getSafe(cfg, "menus.effects.ninguno.name", "&8❌ No Effect"),
                cfg.getStringList("menus.effects.ninguno.lore")));

        menu.setItem(22, createItem(Material.ARROW,
                getSafe(cfg, "menus.effects.back.name", "&e⬅ Close"),
                cfg.getStringList("menus.effects.back.lore")));
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

        if (!p.hasPermission("gideonfly.joineffects")) {
            send(p, "&cYou don’t have permission to use this menu.");
            play(p, SoundUtil.SoundType.ERROR);
            return;
        }

        String effectType = switch (e.getCurrentItem().getType()) {
            case LIGHTNING_ROD -> "RAYO";
            case FIREWORK_ROCKET -> "FUEGO";
            case RED_WOOL -> "VOLCAN";
            case BARRIER -> "NINGUNO";
            default -> null;
        };

        if (effectType == null) {
            if (e.getCurrentItem().getType() == Material.ARROW) {
                p.closeInventory();
                HandlerList.unregisterAll(this);
                play(p, SoundUtil.SoundType.CLICK);
            }
            return;
        }

        // ✅ Guardar efecto con PlayerDataManager (usa BoostedYAML)
        plugin.getPlayerDataManager().setEffect(p, effectType);

        var cfg = plugin.getYml().config();
        String prefix = getSafe(cfg, "prefix", "&b[GIDEONFLY]&f ");
        send(p, colorize(prefix + "&aSelected join effect: &f" + effectType));
        play(p, SoundUtil.SoundType.SUCCESS);

        // 🎇 Prueba visual inmediata
        Bukkit.getScheduler().runTask(plugin, () -> EffectManager.playEffect(p, effectType));

        p.closeInventory();
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
    // 🧩 Helper seguro
    // =========================================================
    private String getSafe(dev.dejvokep.boostedyaml.YamlDocument doc, String path, String fallback) {
        String s = doc.getString(path);
        return (s == null || s.isEmpty()) ? fallback : s;
    }
}
