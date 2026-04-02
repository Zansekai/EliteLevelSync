package fr.zansekai.elitelevelsync.managers;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import fr.zansekai.elitelevelsync.EliteLevelSync;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class ConfigManager {

    private final EliteLevelSync plugin;

    private String syncMode;
    private double scaleFactor;
    private int maxEmLevel;
    private boolean syncOnJoin;
    private boolean syncOnLevelUp;
    private int periodicSyncInterval;
    private List<PrimarySkillType> averageSkills;
    private Map<PrimarySkillType, String> skillMapping;

    // Messages
    private String msgSyncSuccess;
    private String msgReloadSuccess;
    private String msgNoPermission;
    private String msgPlayerNotFound;

    public ConfigManager(EliteLevelSync plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        syncMode = cfg.getString("sync-mode", "direct").toLowerCase();
        scaleFactor = cfg.getDouble("scale-factor", 1.0);
        maxEmLevel = cfg.getInt("max-em-level", 100);
        syncOnJoin = cfg.getBoolean("sync-on-join", true);
        syncOnLevelUp = cfg.getBoolean("sync-on-levelup", true);
        periodicSyncInterval = cfg.getInt("periodic-sync-interval", 300);

        // Skills pour la moyenne
        averageSkills = new ArrayList<>();
        List<String> avgList = cfg.getStringList("average-skills");
        for (String s : avgList) {
            try {
                averageSkills.add(PrimarySkillType.valueOf(s.toUpperCase()));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Skill mcMMO inconnu dans average-skills: " + s);
            }
        }

        // Mapping direct
        skillMapping = new LinkedHashMap<>();
        if (cfg.isConfigurationSection("skill-mapping")) {
            for (String key : cfg.getConfigurationSection("skill-mapping").getKeys(false)) {
                String value = cfg.getString("skill-mapping." + key);
                if (value == null || value.isBlank()) continue;
                try {
                    PrimarySkillType mcmmoSkill = PrimarySkillType.valueOf(key.toUpperCase());
                    skillMapping.put(mcmmoSkill, value.toUpperCase());
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Skill mcMMO inconnu dans skill-mapping: " + key);
                }
            }
        }

        // Messages
        msgSyncSuccess = color(cfg.getString("messages.sync-success", "&aSkills synchronisÃ©s."));
        msgReloadSuccess = color(cfg.getString("messages.reload-success", "&aConfig rechargÃ©e."));
        msgNoPermission = color(cfg.getString("messages.no-permission", "&cPermission refusÃ©e."));
        msgPlayerNotFound = color(cfg.getString("messages.player-not-found", "&cJoueur introuvable."));
    }

    private String color(String s) {
        return s == null ? "" : s.replace("&", "Â§");
    }

    // --- Getters ---

    public String getSyncMode() { return syncMode; }
    public double getScaleFactor() { return scaleFactor; }
    public int getMaxEmLevel() { return maxEmLevel; }
    public boolean isSyncOnJoin() { return syncOnJoin; }
    public boolean isSyncOnLevelUp() { return syncOnLevelUp; }
    public int getPeriodicSyncInterval() { return periodicSyncInterval; }
    public List<PrimarySkillType> getAverageSkills() { return averageSkills; }
    public Map<PrimarySkillType, String> getSkillMapping() { return skillMapping; }
    public String getMsgSyncSuccess() { return msgSyncSuccess; }
    public String getMsgReloadSuccess() { return msgReloadSuccess; }
    public String getMsgNoPermission() { return msgNoPermission; }
    public String getMsgPlayerNotFound() { return msgPlayerNotFound; }
}
