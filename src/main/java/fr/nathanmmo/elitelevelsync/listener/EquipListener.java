package fr.nathanmmo.elitelevelsync.listener;
import fr.nathanmmo.elitelevelsync.EliteLevelSync;
import fr.nathanmmo.elitelevelsync.hooks.LevelHook;
import fr.nathanmmo.elitelevelsync.util.EliteItemUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
public class EquipListener implements Listener {
    private final EliteLevelSync plugin;
    private static final int SLOT_HELMET=39,SLOT_CHESTPLATE=38,SLOT_LEGGINGS=37,SLOT_BOOTS=36,SLOT_OFFHAND=0;
    public EquipListener(EliteLevelSync plugin) { this.plugin = plugin; }

    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        ItemStack item = getItem(e);
        if (item != null && shouldBlock(p, item)) { e.setCancelled(true); notify(p, item); }
    }

    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        for (int slot : e.getRawSlots()) {
            if (isArmor(slot)) { ItemStack item = e.getNewItems().get(slot);
                if (item != null && shouldBlock(p, item)) { e.setCancelled(true); notify(p, item); return; } }
        }
    }

    private ItemStack getItem(InventoryClickEvent e) {
        if (isArmor(e.getRawSlot()) && e.getCursor()!=null && !e.getCursor().getType().isAir()) return e.getCursor();
        if (e.isShiftClick() && e.getCurrentItem()!=null && !e.getCurrentItem().getType().isAir() && isArmorType(e.getCurrentItem())) return e.getCurrentItem();
        return null;
    }

    private boolean isArmor(int s) { return s==SLOT_HELMET||s==SLOT_CHESTPLATE||s==SLOT_LEGGINGS||s==SLOT_BOOTS||s==SLOT_OFFHAND; }

    private boolean isArmorType(ItemStack it) {
        return switch (it.getType()) {
            case LEATHER_HELMET,CHAINMAIL_HELMET,IRON_HELMET,GOLDEN_HELMET,DIAMOND_HELMET,NETHERITE_HELMET,TURTLE_HELMET,
                 LEATHER_CHESTPLATE,CHAINMAIL_CHESTPLATE,IRON_CHESTPLATE,GOLDEN_CHESTPLATE,DIAMOND_CHESTPLATE,NETHERITE_CHESTPLATE,
                 LEATHER_LEGGINGS,CHAINMAIL_LEGGINGS,IRON_LEGGINGS,GOLDEN_LEGGINGS,DIAMOND_LEGGINGS,NETHERITE_LEGGINGS,
                 LEATHER_BOOTS,CHAINMAIL_BOOTS,IRON_BOOTS,GOLDEN_BOOTS,DIAMOND_BOOTS,NETHERITE_BOOTS,ELYTRA,CARVED_PUMPKII,PLAYER_HEAD -> true;
            default -> false;
        };
    }

    private boolean shouldBlock(Player p, ItemStack item) {
        if (p.hasPermission("elitelevelsync.bypass")) return false;
        int req = EliteItemUtil.getRequiredLevel(item);
        if (req <= 0) return false;
        LevelHook hook = plugin.getActiveHook();
        if (hook == null || !hook.isAvailable()) return false;
        int cur = hook.getLevel(p);
        if (cur < 0) return false;
        if (plugin.isDebug()) plugin.getLogger().info("[Debug] "+p.getName()+" niveau requis:"+req+" niveau actuel:"+cur);
        return cur < req;
    }

    private void notify(Player p, ItemStack item) {
        LevelHook hook = plugin.getActiveHook(); if (hook == null) return;
        int req = EliteItemUtil.getRequiredLevel(item);
        int cur = hook.getLevel(p);
        String name = item.getItemMeta()!=null&&item.getItemMeta().hasDisplayName()?item.getItemMeta().getDisplayName():item.getType().name();
        p.sendMessage(EliteItemUtil.format(plugin.getConfig().getString("message-insufficient-level","&c✖ Niveau insuffisant ! Niveau {required} requis (le vôtre : {current})"),req,cur,hook.getName(),name));
        if (plugin.getConfig().getBoolean("play-sound",true)) {
            try { Sound s=Sound.valueOf(plugin.getConfig().getString("sound","ENTITY_VILLAGER_NO")); p.playSound(p.getLocation(),s,(float)plugin.getConfig().getDouble("sound-volume",1.0),(float)plugin.getConfig().getDouble("sound-pitch",1.2)); } catch(IllegalArgumentException e) {}
        }
        if (plugin.getConfig().getBoolean("show-title",false)) {
            p.sendTitle(EliteItemUtil.format(plugin.getConfig().getString("title","&cNiveau insuffisant"),req,cur,hook.getName(),name),EliteItemUtil.format(plugin.getConfig().getString("subtitle","&7Requis : &c{required}"),req,cur,hook.getName(),name),plugin.getConfig().getInt("title-fade-in",10),plugin.getConfig().getInt("title-stay",40),plugin.getConfig().getInt("title-fade-out",10));
        }
    }
}
