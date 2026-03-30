package fr.nathanmmo.elitelevelsync.hooks;
import me.athlaeos.valhallammo.playerstats.profiles.implementations.PowerProfile;
import me.athlaeos.valhallammo.players.CharacterProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
public class ValhallaHook implements LevelHook {
    private final String levelType;
    public ValhallaHook(String lt) { this.levelType=lt==null?"LEVEL":lt.toUpperCase(); }
    @Override public String getName() { return "ValhallaMMO"; }
    @Override public int getLevel(Player player) {
        try { PowerProfile p=CharacterProfile.getOrCache(player,PowerProfile.class); if(p==null)return -1;
            return "TOTAL_EXP".equals(levelType)?(int)p.getTotalExp():p.getLevelFromExp(p.getTotalExp());
        }catch(Exception e){return -1;}
    }
    @Override public boolean isAvailable() { return Bukkit.getPluginManager().isPluginEnabled("ValhallaMMO"); }
}
