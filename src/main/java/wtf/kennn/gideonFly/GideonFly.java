package wtf.kennn.gideonFly;

import co.aikar.commands.PaperCommandManager;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.kennn.gideonFly.Apis.LuckPermsHook;
import wtf.kennn.gideonFly.Commands.FlyCommand;
import wtf.kennn.gideonFly.Commands.JoinEffectCommand;
import wtf.kennn.gideonFly.Commands.SetupCommand;
import wtf.kennn.gideonFly.Listeners.*;
import wtf.kennn.gideonFly.Managers.YMLManager;
import wtf.kennn.gideonFly.Managers.PlayerDataManager;
import wtf.kennn.gideonFly.Utils.KeyUtil;

import static wtf.kennn.gideonFly.Utils.ChatUtil.log;

public final class GideonFly extends JavaPlugin {

    private static GideonFly instance;
    private PlayerDataManager playerDataManager;
    private PaperCommandManager commandManager;
    private LuckPerms luckPermsAPI;
    private YMLManager yml;

    @Override
    public void onEnable() {
        instance = this;

        KeyUtil.init(this);
        LuckPermsHook.init(this);

        try {
            yml = new YMLManager(this);
        } catch (Exception e) {
            getLogger().severe("❌ Error loading YAML files: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        playerDataManager = new PlayerDataManager(this);

        try {
            commandManager = new PaperCommandManager(this);
        } catch (Exception e) {
            getLogger().severe("❌ Failed to initialize ACF: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        commandManager.registerCommand(new FlyCommand());
        commandManager.registerCommand(new SetupCommand());
        commandManager.registerCommand(new JoinEffectCommand());

        registerListeners();


        log("       &b&lGIDEONFLY");
        log("");
        log("&fPlugin by &bGideon Studio");
        log("&fAuthor: &bKenn");
        log("&fStatus: &aEnabled");
        log("&fVersion: &a" + getDescription().getVersion());
        log("");
    }

    @Override
    public void onDisable() {

        log("       &b&lGIDEONFLY");
        log("");
        log("&fPlugin by &bGideon Studio");
        log("&fAuthor: &bKenn");
        log("&fStatus: &aDisabled");
        log("&fVersion: &a" + getDescription().getVersion());
        log("");
    }

    private void registerListeners() {
        var pm = getServer().getPluginManager();
        pm.registerEvents(new JoinListener(this), this);
        pm.registerEvents(new PrefixChatListener(), this);
        pm.registerEvents(new LeaveListener(this), this);
        pm.registerEvents(new VolcanoCleanupListener(this), this);
    }

    public void reinitCoreListeners() {
        org.bukkit.event.HandlerList.unregisterAll(this);
        registerListeners();
        log("&eRe-registered all listeners after config reload!");
    }

    public static GideonFly getInstance() { return instance; }
    public YMLManager getYml() { return yml; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public PaperCommandManager getCommandManager() { return commandManager; }
    public LuckPerms getLuckPermsAPI() { return luckPermsAPI; }
}
