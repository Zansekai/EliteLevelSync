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
        saveDefaultConfig(); registerHooks(); loadHook();
        getServer().getPluginManager().registerEvents(new EquipListener(this),this);
        var cmd = getCommand("elsreload"); if(cmd!=null) cmd.setExecutor(this);
        getLogger().info("EliteLevelSync activ\u00e9 ! Source : "+(activeHook!=null?activeHook.getName():"AUCUNE"));
    }
    @Override public void onDisable() { getLogger().info("EliteLevelSync d\u00e9sactiv\u00e9."); }
    private void registerHooks() {
        hooks.clear();
        hooks.put("MMOCORE", new MMOCoreHook());
        hooks.put("MCMMO",   new McMMOHook(getConfig().getString("mcmmo-level-type","POWER")));
        hooks.put("VALHALLA",new ValhallaHook(getConfig().getString("valhalla-level-type","LEVEL")));
    }
    private void loadHook() {
        String src = getConfig().getString("level-source","MMOCORE").toUpperCase();
        activeHook = hooks.get(src);
        if(activeHook==null) { getLogger().severe("Source inconnue: '"+src+"'. Valeurs: MMOCORE,MCMMO,VALHALLA"); return; }
        if(!activeHook.isAvailable()) getLogger().warning("Plugin '"+activeHook.getName()+"' non charg\u00e9 !");
    }
    @Override public boolean onCommand(CommandSender s,Command c,String l,String[] a) {
        if(!c.getName().equalsIgnoreCase("elsreload")) return false;
        reloadConfig(); registerHooks(); loadHook();
        s.sendMessage("\u00a7a[EliteLevelSync] \u00a77Recharg\u00e9 ! Source: \u00a7f"+(activeHook!=null?activeHook.getName():"\u00a7cAUCUNE"));
        return true;
    }
    public LevelHook getActiveHook() { return activeHook; }
    public boolean isDebug() { return getConfig().getBoolean("debug",false); }
}