package wtf.kennn.gideonFly.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.entity.Player;
import wtf.kennn.gideonFly.Menus.SetupMenu;
import wtf.kennn.gideonFly.Utils.SoundUtil;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;
import static wtf.kennn.gideonFly.Utils.ChatUtil.send;

@CommandAlias("setup")
@CommandPermission("gideonfly.admin")
public class SetupCommand extends BaseCommand {

    @Default
    public void onSetup(Player player) {
        new SetupMenu(player).open();
        play(player, SoundUtil.SoundType.OPEN_MENU);
    }
}
