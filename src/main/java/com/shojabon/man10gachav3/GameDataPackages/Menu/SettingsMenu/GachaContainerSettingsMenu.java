package com.shojabon.man10gachav3.GameDataPackages.Menu.SettingsMenu;

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
import org.bukkit.plugin.java.JavaPlugin;

public class GachaContainerSettingsMenu {
    Inventory inv;
    Listener listener = new Listener();
    JavaPlugin plugin;
    String gacha;
    Player p;
    int currentPage = 0;
    SInventory sInventory;
    int[] slots = new int[]{10,14,19,23,28,32,37,41};

    GachaGame game;

    public GachaContainerSettingsMenu(String gacha, Player p){
        p.closeInventory();
        this.gacha = gacha;
        game = Man10GachaAPI.gachaGameMap.get(gacha);
        this.p = p;
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        SInventory inve = new SInventory(6, "§b§l" + gacha + "§7§lコンテナ設定");
        inve.fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDamage(11).setDisplayname(" ").build());
        inve.setItem(slots, new SItemStack(Material.BARRIER).setDisplayname("§c§lなし").build());
        inve.setItem(new int[]{11,15,20,24,29,33,38,42}, new SItemStack(Material.ANVIL).setDisplayname("§7§l設定").build());
        inve.setItem(new int[]{12,16,21,25,30,34,39,43}, new SItemStack(Material.TNT).setDisplayname("§4§l消去する").build());
        inve.setItem(new int[]{18,27}, new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname("戻る").setDamage(14).build());
        inve.setItem(new int[]{26,35}, new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname("次へ").setDamage(14).build());
        sInventory = inve;
        inv = inve.build();
        render(currentPage);
        p.openInventory(inv);
    }

    private void close(Player p){
        HandlerList.unregisterAll(listener);
        p.closeInventory();
    }

    private void render(int menu){
        int len = slots.length;
        for(int i =0; i < slots.length; i++){
            inv.setItem(slots[i], new SItemStack(Material.BARRIER).setDisplayname("§c§lなし").build());
        }
        if(game.getItemIndex().size() <= len) {
            len = game.getItemIndex().size();
        }
        for(int i =0; i < len; i++){
            if(game.getItemIndex().get(menu * 8 + i) != null){
                inv.setItem(slots[i], new SItemStack(game.getItemIndex().get(menu * 8 + i).item).setAmount(game.getItemIndex().get(menu * 8 + i).amount).build());
            }else{
            }
        }
    }

    class Listener implements org.bukkit.event.Listener
    {

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getWhoClicked().getUniqueId() != p.getUniqueId()) return;
            e.setCancelled(true);
            if(e.getRawSlot() == 26 || e.getRawSlot() == 35){

                currentPage += 1;
                render(currentPage);

                return;
            }
            if(e.getRawSlot() == 18 || e.getRawSlot() == 27){
                currentPage -= 1;
                if(currentPage < 0) currentPage = 0;
                render(currentPage);
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close((Player) e.getPlayer());
        }

    }
}
