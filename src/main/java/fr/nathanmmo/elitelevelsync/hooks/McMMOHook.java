package fr.nathanmmo.elitelevelsync.hooks;
import com.gmail.nossr50.datatypes.player.McMMOPlayer;
import com.gmail.nossr50.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
public class McMMOHook implements LevelHook {
    private final String levelType;
    public McMMOHook(String lt) { this.levelType=lt==null?"POWER":lt.toUpperCase(); }
    @Override public String getName() { return "mcMMO"; }
    @Override public int getLevel(Player player) {
        try { McMMOPlayer m=UserManager.getPlayer(player); if(m==null)return -1;
            if("SKILL_AVG".equals(levelType)){ int t=0,c=0;
                for(com.gmail.nossr50.datatypes.skills.PrimarySkillType s:com.gmail.nossr50.datatypes.skills.PrimarySkillType.values()){ int l=m.getSkillLevel(s);if(l>0){t+=l;c++;}} return c>0?t/c:0; }
            return m.getPowerLevel();
        }catch(Exception e){return -1;}
    }
    @Override public boolean isAvailable() { return Bukkit.getPluginManager().isPluginEnabled("mcMMO"); }
}
