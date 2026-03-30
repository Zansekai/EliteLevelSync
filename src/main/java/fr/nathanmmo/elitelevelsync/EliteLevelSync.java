package fr.nathanmmo.elitelevelsync;
import fr.nathanmmo.elitelevelsync.hooks.*;
import fr.nathanmmo.elitelevelsync.listener.EquipListener;
import org.bukkit.command.*;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;
public class EliteLevelSync extends JavaPlugin implements CommandExecutor {
    private LevelHook activeHook;
    private final Map<String,LevelHook> hooks = new HashMap<>();
    @Override public void onEnable() {
        saveDefaultConfig(); registerHooks(); loadActiveHook();
        getServer().getPluginManager().registerEvents(new EquipListener(this),this);
        var c=getCommand("elsreload"); if(c!=null)c.setExecutor(this);
        getLogger().info("EliteLevelSync active! Source: "+(activeHook!=null?activeHook.getName():"AUCUNE"));
    }
    @Override public void onDisable() { getLogger().info("EliteLevelSync desactive."); }
    private void registerHooks() {
        hooks.clear();
        hooks.put("MMOCORE",new MMOCoreHook());
        hooks.put("MCMMO",new McMMOHook(getConfig().getString("mcmmo-level-type","POWER")));
        hooks.put("VALHALLA",new ValhallaHook(getConfig().getString("valhalla-level-type","LEVEL")));
    }
    private void loadActiveHook() {
        String src=getConfig().getString("level-source","MMOCORE").toUpperCase();
        activeHook=hooks.get(src);
        if(activeHook==null) getLogger().severe("Source inconnue: "+src);
        else if(!activeHook.isAvailable()) getLogger().warning(activeHook.getName()+" non charge.");
    }
    @Override public boolean onCommand(CommandSender s, Command c,String l,String[] a) {
        if(!c.getName().equalsIgnoreCase("elsreload"))return false;
        reloadConfig();registerHooks();loadActiveHook();
        s.sendMessage("\xa7a[EliteLevelSync] \xa77Config rechargee! Source: \xa7f"+(activeHook!=null?activeHook.getName():"\xa7cAUCUNE"));
        return true;
    }
    public LevelHook getActiveHook() { return activeHook; }
    public boolean isDebug() { return getConfig().getBoolean("debug",false); }
}
