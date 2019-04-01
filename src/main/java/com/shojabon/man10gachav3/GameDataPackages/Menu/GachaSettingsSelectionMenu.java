package com.shojabon.man10gachav3.GameDataPackages.Menu;

import com.shojabon.man10gachav3.GamePackages.GachaGame;
import com.shojabon.man10gachav3.GamePackages.Man10GachaAPI;
import com.shojabon.man10gachav3.ToolPackages.SInventory;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class GachaSettingsSelectionMenu {
    JavaPlugin plugin;
    Player p;
    Listener listener;
    Inventory inv;
    int page = 0;
    public GachaSettingsSelectionMenu(Player p){
        this.p = p;
        p.closeInventory();
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        listener = new Listener(p);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        inv = createInventory();
        p.openInventory(inv);
        rednerSelection();
    }

    private Inventory createInventory(){
        return new SInventory(6, "§b§l設定するガチャを選択してください").setItem(new int[]{53,52}, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(14).setDisplayname("§c§l次").build()).setItem(new int[]{46,45}, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(14).setDisplayname("§c§l戻る").build()).setItem(new int[]{51,50,48,47}, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(3).setDisplayname(" ").build()).setItem(49, new SItemStack(Material.NETHER_STAR).setDisplayname("§8§l設定を開く").build()).build();
    }

    private void close(Player p){
        HandlerList.unregisterAll(listener);
        p.closeInventory();
    }

    private void clearSelection(){
        for(int i = 0;i < 45;i++){
            inv.setItem(i, new ItemStack(Material.AIR));
        }
    }

    private void rednerSelection(){
        int to = Man10GachaAPI.gachaGameMap.size();
        if(to - 45 * page > 45) {
            to = 45;
        }else{
            to = to - 45 * page;
        }
        for(int i = 0;i < to;i++){
            GachaGame game = Man10GachaAPI.gachaGameMap.get(Man10GachaAPI.gachaGameMap.keySet().toArray()[i + 45 * page]);
            inv.setItem(i, new SItemStack(game.getSettings().icon.clone()).setDisplayname(game.getSettings().name).build());
        }
    }

    class Listener implements org.bukkit.event.Listener {
        Player p;
        Listener(Player p){
            this.p = p;
        }
        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getWhoClicked().getUniqueId() != p.getUniqueId()) return;
            e.setCancelled(true);
            if(e.getRawSlot() >= 54) return;
            if(e.getSlot() == -999 || e.getInventory().getItem(e.getSlot()) == null || e.getSlot() == 47 || e.getSlot() == 48 || e.getSlot() == 50 || e.getSlot() == 51 || e.getSlot() == 49) return;
            if(e.getSlot() == 52 || e.getSlot() == 53){
                double maxPage = Man10GachaAPI.gachaGameMap.keySet().size()/45;
                if(maxPage - (int) maxPage > 0) maxPage += 1;
                if(page + 1 > maxPage){
                    return;
                }
                page += 1;
                clearSelection();
                rednerSelection();
                return;
            }
            if(e.getSlot() == 45 || e.getSlot() == 46){
                if(page - 1 < 0){
                    return;
                }
                page -= 1;
                clearSelection();
                rednerSelection();
                return;
            }
            new GachaSettingsMenu(e.getInventory().getItem(e.getRawSlot()).getItemMeta().getDisplayName(), (Player) e.getWhoClicked());
        }
        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close((Player) e.getPlayer());
        }
    }
}
