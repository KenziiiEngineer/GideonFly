package wtf.kennn.gideonFly.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Utils.SoundUtil.SoundType;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

@CommandAlias("fly")
@Description("Activa o desactiva el modo vuelo.")
public class FlyCommand extends BaseCommand {

    private final GideonFly plugin;

    public FlyCommand() {
        this.plugin = GideonFly.getInstance();
    }

    @Default
    @Syntax("[jugador]")
    @CommandCompletion("@players")
    @CommandPermission("gideonfly.use")
    public void onFly(Player sender, @Optional String targetName) {

        // 📦 Lectura directa de BoostedYAML (solo config.yml)
        String prefix    = getCfg("prefix");
        String msgFlyOn  = getMsg("messages.fly-on", prefix);
        String msgFlyOff = getMsg("messages.fly-off", prefix);
        String msgNoPerm = getMsg("messages.no-permission", prefix);

        // 🎯 Si se especifica un jugador objetivo
        if (targetName != null) {
            if (!sender.hasPermission("gideonfly.admin")) {
                send(sender, msgNoPerm);
                play(sender, SoundType.ERROR);
                return;
            }

            Player target = Bukkit.getPlayerExact(targetName);
            if (target == null) {
                send(sender, prefix + "&cEl jugador no está conectado.");
                play(sender, SoundType.ERROR);
                return;
            }

            toggleFly(target, sender, msgFlyOn, msgFlyOff, prefix);
            return;
        }

        // ✈️ Toggle propio
        toggleFly(sender, null, msgFlyOn, msgFlyOff, prefix);
    }

    // =========================================================
    // 🔁 Método reutilizable para togglear modo vuelo
    // =========================================================
    private void toggleFly(Player target, Player executor, String msgOn, String msgOff, String prefix) {
        boolean newState = !target.getAllowFlight();
        target.setAllowFlight(newState);
        target.setFlying(newState);

        if (newState) {
            send(target, msgOn);
            play(target, SoundType.FLY_ON);
            log("&a" + target.getName() + " activó el modo vuelo.");
        } else {
            send(target, msgOff);
            play(target, SoundType.FLY_OFF);
            log("&c" + target.getName() + " desactivó el modo vuelo.");
        }

        if (executor != null && !executor.equals(target)) {
            send(executor, prefix + "&7Has cambiado el vuelo de &b" + target.getName() + "&7.");
            play(executor, SoundType.SUCCESS);
        }
    }

    // =========================================================
    // ⚙️ Helpers BoostedYAML (solo config.yml)
    // =========================================================

    /** Lee de config.yml (BoostedYAML) con fallback seguro. */
    private String getCfg(String path) {
        String s = plugin.getYml().config().getString(path);
        return s != null ? s : "";
    }

    /** Lee un mensaje desde config.yml y reemplaza %prefix%. */
    private String getMsg(String path, String prefix) {
        String s = plugin.getYml().config().getString(path);
        if (s == null) s = "&cMensaje faltante: " + path;
        if (prefix == null) prefix = "";
        return s.replace("%prefix%", prefix);
    }
}
