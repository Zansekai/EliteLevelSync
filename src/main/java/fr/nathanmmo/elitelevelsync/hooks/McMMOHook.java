package fr.nathanmmo.elitelevelsync.hooks;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
public class McMMOHook implements LevelHook {
    private final String levelType;
    public McMMOHook(String levelType) { this.levelType = levelType == null ? "POWER" : levelType.toUpperCase(); }
    @Override public String getName() { return "mcMMO"; }
    @Override public int getLevel(Player player) {
        try {
            McMMOPlayer mcP = UserManager.getPlayer(player);
            if (mcP == null) return -1;
            if ("SKILL_AVG".equals(levelType)) {
                int t = 0, c = 0;
                for (var s : com.gmail.nossr50.datatypes.skills.PrimarySkillType.values()) {
                    int l = mcP.getSkillLevel(s); if (l > 0) { t += l; c++; }
                }
                return c > 0 ? t / c : 0;
            } else { return mcP.getPowerLevel(); }
        } catch (Exception e) { return -1; }
    }
    @Override public boolean isAvailable() { return Bukkit.getPluginManager().isPluginEnabled("mcMMO"); }
}
