package fr.nathanmmo.elitelevelsync.hooks;
import org.bukkit.entity.Player;
public interface LevelHook {
    String getName();
    int getLevel(Player player);
    boolean isAvailable();
}
