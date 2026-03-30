package fr.nathanmmo.elitelevelsync.util;
import com.magmaguy.elitemobs.items.EliteItemManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
public class EliteItemUtil {
    private EliteItemUtil() {}
    public static int getRequiredLevel(ItemStack item) {
        if (item==null||!item.hasItemMeta()) return -1;
        try {
            if (!EliteItemManager.isEliteMobsItem(item)) return -1;
            return EliteItemManager.getItemLevel(item);
        } catch (Exception e) { return parseLore(item); }
    }
    private static int parseLore(ItemStack item) {
        ItemMeta m = item.getItemMeta(); if (m==null||!m.hasLore()) return -1;
        List<String> lore = m.getLore(); if (lore==null) return -1;
        for (String l : lore) {
            String c = l.replaceAll("§[0-9a-fk-or]","").trim();
            if (c.startsWith("Item Level:")||c.startsWith("Niveau de l'objet")) {
                try { String[] p=c.split(":"); if(p.length>=2) return Integer.parseInt(p[1].trim()); }
                catch(NumberFormatException i){}
            }
        }
        return -1;
    }
    public static String format(String t,int req,int cur,String src,String item) {
        return t.replace("{required}",String.valueOf(req)).replace("{current}",String.valueOf(cur)).replace("{source}",src).replace("{item}",item!=null?item:"Objet inconnu").replace("&","§");
    }
}
