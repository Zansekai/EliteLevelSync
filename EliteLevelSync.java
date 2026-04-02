package fr.zansekai.elitelevelsync;

import fr.zansekai.elitelevelsync.commands.ElsCommand;
import fr.zansekai.elitelevelsync.listeners.McMMOLevelUpListener;
import fr.zansekai.elitelevelsync.listeners.PlayerJoinListener;
import fr.zansekai.elitelevelsync.managers.ConfigManager;
import fr.zansekai.elitelevelsync.managers.SyncManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EliteLevelSync extends JavaPlugin {

    private static EliteLevelSync instance;
    private ConfigManager configManager;
    private SyncManager syncManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configManager = new ConfigManager(this);
        syncManager = new SyncManager(this);

        // Listeners
        Bukkit.getPluginManager().registerEvents(new McMMOLevelUpListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Commande
        getCommand("elitelevelsync").setExecutor(new ElsCommand(this));

        // Sync pÃ©riodique
        int interval = configManager.getPeriodicSyncInterval();
        if (interval > 0) {
            long ticks = interval * 20L;
            Bukkit.getScheduler().runTaskTimer(this, () ->
                Bukkit.getOnlinePlayers().forEach(syncManager::syncPlayer), ticks, ticks);
        }

        getLogger().info("EliteLevelSync activÃ©. Mode: " + configManager.getSyncMode());
    }

    @Override
    public void onDisable() {
        getLogger().info("EliteLevelSync dÃ©sactivÃ©.");
    }

    public static EliteLevelSync getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public SyncManager getSyncManager() {
        return syncManager;
    }
}
