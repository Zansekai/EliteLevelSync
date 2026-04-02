package fr.zansekai.elitelevelsync.managers;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import com.magmaguy.elitemobs.playerdata.database.PlayerData;
import com.magmaguy.elitemobs.skills.SkillType;
import com.magmaguy.elitemobs.skills.SkillXPCalculator;
import fr.zansekai.elitelevelsync.EliteLevelSync;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class SyncManager {

    private final EliteLevelSync plugin;

    public SyncManager(EliteLevelSync plugin) {
        this.plugin = plugin;
    }

    public void syncPlayer(Player player) {
        ConfigManager cfg = plugin.getConfigManager();
        String mode = cfg.getSyncMode();
        switch (mode) {
            case "direct" -> syncDirect(player, cfg);
            case "average" -> syncAverage(player, cfg);
            case "both" -> { syncDirect(player, cfg); syncAverageForUnmapped(player, cfg); }
            default -> plugin.getLogger().warning("Mode de sync inconnu: " + mode);
        }
    }

    private void syncDirect(Player player, ConfigManager cfg) {
        for (Map.Entry<PrimarySkillType, String> entry : cfg.getSkillMapping().entrySet()) {
            SkillType emSkill = parseEmSkill(entry.getValue());
            if (emSkill == null) continue;
            setEmSkillLevel(player, emSkill, getMcMMOLevel(player, entry.getKey()), cfg);
        }
    }

    private void syncAverage(Player player, ConfigManager cfg) {
        int avg = computeAverage(player, cfg.getAverageSkills());
        for (SkillType s : SkillType.values()) setEmSkillLevel(player, s, avg, cfg);
    }

    private void syncAverageForUnmapped(Player player, ConfigManager cfg) {
        int avg = computeAverage(player, cfg.getAverageSkills());
        java.util.Set<String> mapped = new java.util.HashSet<>(cfg.getSkillMapping().values());
        for (SkillType s : SkillType.values()) if (!mapped.contains(s.name())) setEmSkillLevel(player, s, avg, cfg);
    }

    private int computeAverage(Player player, List<PrimarySkillType> skills) {
        if (skills.isEmpty()) return 1;
        long sum = 0; int count = 0;
        for (PrimarySkillType skill : skills) { sum += getMcMMOLevel(player, skill); count++; }
        return count == 0 ? 1 : (int)(sum / count);
    }

    private int getMcMMOLevel(Player player, PrimarySkillType skill) {
        try { return ExperienceAPI.getLevel(player, skill.toString()); }
        catch (Exception e) { plugin.getLogger().warning("Cannot read mcMMO skill " + skill + " for " + player.getName()); return 1; }
    }

    private void setEmSkillLevel(Player player, SkillType emSkill, int rawLevel, ConfigManager cfg) {
        int scaled = Math.max(1, Math.min((int)Math.floor(rawLevel * cfg.getScaleFactor()), cfg.getMaxEmLevel()));
        try { PlayerData.setSkillXP(player.getUniqueId(), emSkill, SkillXPCalculator.totalXPForLevel(scaled)); }
        catch (Exception e) { plugin.getLogger().warning("Cannot set EM skill " + emSkill + " for " + player.getName()); }
    }

    public void syncSingleSkill(Player player, PrimarySkillType mcmmoSkill) {
        ConfigManager cfg = plugin.getConfigManager();
        if (cfg.getSyncMode().equals("average") || cfg.getSyncMode().equals("both")) { syncPlayer(player); return; }
        String emName = cfg.getSkillMapping().get(mcmmoSkill);
        if (emName == null) return;
        SkillType emSkill = parseEmSkill(emName);
        if (emSkill == null) return;
        setEmSkillLevel(player, emSkill, getMcMMOLevel(player, mcmmoSkill), cfg);
    }

    private SkillType parseEmSkill(String name) {
        try { return SkillType.valueOf(name.toUpperCase()); }
        catch (IllegalArgumentException e) { plugin.getLogger().warning("Unknown EM skill: " + name); return null; }
    }
}
