package fr.nathanmmo.elitelevelsync.listener;
import fr.nathanmmo.elitelevelsync.EliteLevelSync;
import fr.nathanmmo.elitelevelsync.hooks.LevelHook;
import fr.nathanmmo.elitelevelsync.util.EliteItemUtil;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;
public class EquipListener implements Listener {
    private final EliteLevelSync plugin;
    private static final int HELMET=39,CHEST=38,LEGS=37,BOOTS=36,OFF=40;
    public EquipListener(EliteLevelSync p) { this.plugin=p; }
    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)
    public void onClick(InventoryClickEvent e) {
        if(!(e.getWhoClicked() instanceof Player p))return;
        ItemStack item=getItem(e); if(item==null)return;
        if(block(p,item)){e.setCancelled(true);notify(p,item);}
    }
    @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)
    public void onDrag(InventoryDragEvent e) {
        if(!(e.getWhoClicked() instanceof Player p))return;
        for(int s:e.getRawSlots()){ if(armor(s)){ ItemStack i=e.getNewItems().get(s); if(i!=null&&block(p,i)){e.setCancelled(true);notify(p,J);return;} } }
    }
    private ItemStack getItem(InventoryClickEvent e) {
        if(armor(e.getRawSlot())){ ItemStack c=e.getCursor(); if(c!=null&&!c.getType().isAir())return c; }
        if(e.isShiftClick()){ ItemStack c=e.getCurrentItem(); if(c!=null&&!c.getType().isAir()&&wouldArmor(c))return c; }
        return null;
    }
    private boolean armor(int s){return s==HELMET||s==CHEST||s==LEGS||s==BOOTS||s==OFF;}
    private boolean wouldArmor(ItemStack i){
        return switch(i.getType()){
            case LEATHER_HELMET,CHAINMAIL_HELMET,IRON_HELMET,GOLDEN_HELMET,DIAMOND_HELMET,NETHERITE_HELMET,TURTLE_HELMET,
             LEATHER_CHESTPLATE,CHAINMAIL_CHESTPLATE,IRON_CHESTPLATE,GOLDEN_CHESTPLATE,DIAMOND_CHESTPLATE,NETHERITE_CHESTPLATE,
             LEATHER_LEGGINGS,CHAINMAIL_LEGGINGS,IRON_LEGGINGS,GOLDEN_LEGGINGS,DIAMOND_LEGGINGS,NETHERITE_LEGGINGS,
             LEATHER_BOOTS,CHAINMAIL_BOOTS,IRON_BOOTS,GOLDEN_BOOTS,DIAMOND_BOOTS,NETHERITE_BOOTS,ELYTRA,CARVED_PUMPKIN,PLAYER_HEAD->true;
            default->false;
        };
    }
    private boolean block(Player p,ItemStack item){
        if(p.hasPermission("elitelevelsync.bypass"))return false;
        int req=EliteItemUtil.getRequiredLevel(item); if(req<=0)return false;
        LevelHook h=plugin.getActiveHook(); if(h==null||!h.isAvailable())return false;
        int cur=h.getLevel(p); if(cur<0)return false;
        if(plugin.isDebug())plugin.getLogger().info(String.format("[%s] req=%d cur=%d",p.getName(),req,cur));
        return cur<req;
    }
    private void notify(Player p,ItemStack item){
        LevelHook h=plugin.getActiveHook(); if(h==null)return;
        int req=EliteItemUtil.getRequiredLevel(item),cur=h.getLevel(p);
        String n=item.getItemMeta()!=null&&item.getItemMeta().hasDisplayName()?item.getItemMeta().getDisplayName():item.getType().name();
        p.sendMessage(EliteItemUtil.format(plugin.getConfig().getString("message-insufficient-level","&c✖ &7Niveau insuffisant"),req,cur,h.getName(),n));
        if(plugin.getConfig().getBoolean("play-sound",true)){
            try{Sound snd=Sound.valueOf(plugin.getConfig().getString("sound","ENTITY_VILLAGER_NO"));
            p.playSound(p.getLocation(),snd,(float)plugin.getConfig().getDouble("sound-volume",1.0),(float)plugin.getConfig().getDouble("sound-pitch",1.2));}catch(IllegalArgumentException ign){}
        }
    }
}
