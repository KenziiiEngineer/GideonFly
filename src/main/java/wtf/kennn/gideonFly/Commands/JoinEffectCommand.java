package wtf.kennn.gideonFly.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.entity.Player;
import wtf.kennn.gideonFly.Menus.JoinEffectsMenu;
import wtf.kennn.gideonFly.Utils.SoundUtil;

import static wtf.kennn.gideonFly.Utils.SoundUtil.play;
import static wtf.kennn.gideonFly.Utils.ChatUtil.send;

@CommandAlias("joineffects")
@Description("Open the join effects selection menu.")
@CommandPermission("gideonfly.effects")
public class JoinEffectCommand extends BaseCommand {

    @Default
    public void onJoinEffects(Player player) {
        play(player, SoundUtil.SoundType.CLICK);

        new JoinEffectsMenu(player).open();
    }
}
