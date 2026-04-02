package fr.zansekai.elitelevelsync.listeners;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import fr.zansekai.elitelevelsync.EliteLevelSync;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class McMMOLevelUpListener implements Listener {

    private final EliteLevelSync plugin;

    public McMMOLevelUpListener(EliteLevelSync plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
        if (!plugin.getConfigManager().isSyncOnLevelUp()) return;
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () ->
            plugin.getSyncManager().syncSingleSkill(player, event.getSkill()));
    }
}
