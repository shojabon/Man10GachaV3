package com.shojabon.man10gachav3.ToolPackages;


import com.shojabon.man10gachav3.DataPackages.GachaBannerDictionary;
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

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MultiItemStackSelectorAPI {
    Inventory inv;
    Listener listener = new Listener();
    JavaPlugin plugin;
    boolean menuMove = false;
    Player p;
    BiFunction<InventoryClickEvent, ArrayList<ItemStack>, String> okFunction;
    Function<InventoryClickEvent, String> cancelFucntion;
    GachaBannerDictionary dict = new GachaBannerDictionary();
    public MultiItemStackSelectorAPI(Player p, ArrayList<ItemStack> items, BiFunction<InventoryClickEvent, ArrayList<ItemStack>, String> okFunction, Function<InventoryClickEvent, String> cancelFucntion){
        p.closeInventory();
        this.p = p;
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        this.okFunction = okFunction;
        this.cancelFucntion = cancelFucntion;
        SInventory inventory = new SInventory(6, "§b§l保存するアイテムを入力してください");
        inventory.setItem(new int[]{45,46,47,48,49,50,51,52,53}, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(11).setDisplayname(" ").build());
        inventory.setItem(new int[]{48,49,50}, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(14).setDisplayname("§c§l決定する").build());
        inventory.setItem(53, dict.getSymbol("back"));
        inv = inventory.build();
        if(items != null){
            int amount = 45;
            if(items.size() >= 45) amount = 45;
            if(items.size() <= 45) amount = items.size();
            for(int i = 0;i < amount;i++){
                if(items.get(i) != null){
                    inv.setItem(i, items.get(i));
                }
            }
        }
        p.openInventory(inv);
    }

    private void close(Player p){
        HandlerList.unregisterAll(listener);
        p.closeInventory();
    }

    class Listener implements org.bukkit.event.Listener
    {

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getWhoClicked().getUniqueId() != p.getUniqueId()) return;
            int r = e.getRawSlot();
            if(r >= 45 && r <=53) e.setCancelled(true);
            if(r == 53){
                menuMove = true;
                String res = cancelFucntion.apply(e);
                if(res == null){
                    p.closeInventory();
                    return;
                }
            }
            if(r == 48 || r == 49 || r == 50){
                ArrayList<ItemStack> items = new ArrayList<>();
                for(int i = 0;i < 45;i++){
                    if(inv.getItem(i) != null){
                        items.add(inv.getItem(i));
                    }
                }
                menuMove = true;
                String res = okFunction.apply(e, items);
                if(res == null){
                    p.closeInventory();
                    return;
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close((Player) e.getPlayer());
            if(!menuMove) cancelFucntion.apply(null);
        }

    }
}
