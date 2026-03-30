package fr.nathanmmo.elitelevelsync.hooks;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
public class MMOCoreHook implements LevelHook {
    @Override public String getName() { return "MMOCore"; }
    @Override public int getLevel(Player player) {
        try { return PlayerData.get(player).getLevel(); } catch(Exception e) { return -1; }
    }
    @Override public boolean isAvailable() { return Bukkit.getPluginManager().isPluginEnabled("MMOCore"); }
}
