package com.shojabon.man10gachav3.DataPackages;

import com.shojabon.man10gachav3.GamePackages.GachaGame;
import com.shojabon.man10gachav3.GamePackages.Man10GachaAPI;
import com.shojabon.man10gachav3.Man10GachaV3;
import com.shojabon.man10gachav3.ToolPackages.SInventory;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class GachaPlayGame {
    Inventory inv;
    Listener listener = new Listener();
    JavaPlugin plugin;
    Player p;
    GachaGame game;

    GachaItemStack[] itemStacks = new GachaItemStack[9];
    public GachaPlayGame(Player p, GachaGame game){
        p.closeInventory();
        this.p = p;
        this.game = game;
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        Bukkit.getPluginManager().registerEvents(listener, plugin);


        SInventory invee = new SInventory(3, game.getSettings().title);
        invee.fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDamage(11).setDisplayname(" ").build());
        invee.setItem(new int[]{4,22}, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(14).setDisplayname("§c§l||").build());
        invee.setLine(1, new ItemStack(Material.AIR));
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        inv = invee.build();
        p.openInventory(inv);
        play();
    }

    int[] slots = new int[]{9,10, 11,12,13,14,15,16,17};

    private void play(){
        Man10GachaAPI.inGamePlayerMap.put(p.getUniqueId(), game.getSettings().name);
        Runnable r = () -> {
            long speed =  5;
            int stage = 0;
            for(int i =0; i < 10000; i++){




                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int randSize = game.getStorage().size()-1;
                if(randSize <= 0) randSize = 1;
                int index = new Random().nextInt(randSize);
                ItemStack nextItem = new SItemStack(game.getStorage().get(index).item).setAmount(game.getStorage().get(index).amount).build();
                rollItems();
                inv.setItem(17, nextItem);
                itemStacks[8] = game.getStorage().get(index);
                if(i >= 150){
                    speed += 1;
                }
                if(speed >= 75){
                    speed += 10;
                }
                if(speed >= 200){
                    speed += 150;
                }
                if(speed >= 900){
                    speed = 900;
                    if(stage == 3){
                        speed = 1000;
                    }
                    stage ++;
                }
                if(speed >= 1000){
                    break;
                }

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            winItem(itemStacks[4]);

        };
        Thread t = new Thread(r);
        t.start();
    }


    private void winItem(GachaItemStack item){
        p.getInventory().addItem(new SItemStack(item.item).setAmount(item.amount).build());
        item.playerSound.playSoundToPlayer(p);
        item.broadcastSound.playSoundToServerExeptPlayer(p);
        if(item.playerMessage != null){
            for(String message : item.playerMessage){
                p.sendMessage(message);
            }
        }
        if(item.broadcastMessage != null){
            for(Player player : Bukkit.getOnlinePlayers()){
                for(String message : item.broadcastMessage){
                    player.sendMessage(message);
                }
            }
        }





        Man10GachaAPI.inGamePlayerMap.remove(p.getUniqueId());
    }

    private void rollItems(){
        for(int i =0; i < slots.length-1; i++){
            inv.setItem(slots[i], inv.getItem(slots[i+1]));
            itemStacks[i] = itemStacks[i+1];
        }
        new GachaSound(Sound.BLOCK_DISPENSER_DISPENSE, 1, 1).playSoundToPlayer(p);
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
            e.setCancelled(true);
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close((Player) e.getPlayer());
        }

    }
}
