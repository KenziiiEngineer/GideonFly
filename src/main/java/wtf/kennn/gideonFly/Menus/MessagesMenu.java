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

public class MessagesMenu implements Listener {

    private final GideonFly plugin;
    private final Player player;
    private final Inventory menu;

    public MessagesMenu(Player player) {
        this.plugin = GideonFly.getInstance();
        this.player = player;

        // 📖 Cargar título desde BoostedYAML
        var cfg = plugin.getYml().config();
        String title = cfg.getString("menus.join.title", "&b&lGideonFly &8| &fJoin Messages");
        this.menu = Bukkit.createInventory(null, 27, colorize(title));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupItems();
    }

    /** 🧱 Carga los ítems del menú desde config.yml Boosted */
    private void setupItems() {
        var cfg = plugin.getYml().config();

        menu.setItem(11, createItem(
                Material.LIME_DYE,
                getSafe(cfg, "menus.join.items.enable.name", "&a✅ Enable Join Messages"),
                cfg.getStringList("menus.join.items.enable.lore")
        ));

        menu.setItem(15, createItem(
                Material.RED_DYE,
                getSafe(cfg, "menus.join.items.disable.name", "&c❌ Disable Join Messages"),
                cfg.getStringList("menus.join.items.disable.lore")
        ));

        menu.setItem(22, createItem(
                Material.ARROW,
                getSafe(cfg, "menus.join.items.back.name", "&e⬅ Back"),
                cfg.getStringList("menus.join.items.back.lore")
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
            case LIME_DYE -> setJoinMessages(p, true);
            case RED_DYE -> setJoinMessages(p, false);
            case ARROW -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> new SetupMenu(p).open(), 2L);
            }
        }
    }

    /**
     * 💾 Cambia ambos: join-messages y vip-broadcast en config.yml
     */
    private void setJoinMessages(Player p, boolean enabled) {
        try {
            YamlDocument cfg = plugin.getYml().config();

            // 🔄 Actualiza los dos valores
            cfg.set("join-messages", enabled);
            cfg.set("vip-broadcast", enabled);
            cfg.save();
            cfg.reload();

            // 🔁 Recarga los listeners principales
            Bukkit.getScheduler().runTask(plugin, plugin::reinitCoreListeners);

            // 📢 Mensaje al jugador
            String prefix = getSafe(cfg, "prefix", "&b[GIDEONFLY]&f ");
            String msg = enabled
                    ? getSafe(cfg, "messages.join-enabled", "%prefix% &aJoin messages have been ENABLED (VIP & Normal)!")
                    : getSafe(cfg, "messages.join-disabled", "%prefix% &cJoin messages have been DISABLED for everyone!");

            send(p, colorize(msg.replace("%prefix%", prefix)));
            play(p, enabled ? SoundUtil.SoundType.SUCCESS : SoundUtil.SoundType.ERROR);

        } catch (Exception ex) {
            send(p, "&c⚠ Error saving config.yml!");
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
