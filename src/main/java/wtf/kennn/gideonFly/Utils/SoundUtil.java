package wtf.kennn.gideonFly.Utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    public enum SoundType {
        FLY_ON,
        FLY_OFF,
        SUCCESS,
        ERROR,
        CLICK,
        OPEN_MENU,
    }

    public static void play(Player player, SoundType type) {
        if (player == null || !player.isOnline()) return;

        try {
            switch (type) {
                case OPEN_MENU -> player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1.1f);
                case FLY_ON -> player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
                case FLY_OFF -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 1f);
                case SUCCESS -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                case ERROR -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
                case CLICK -> player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            }
        } catch (Exception e) {

        }
    }
}
