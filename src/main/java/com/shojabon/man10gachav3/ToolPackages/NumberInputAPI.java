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
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by sho on 2018/07/12.
 */
public class NumberInputAPI {

    private Plugin plugin;
    private Player player;
    private int numberOfChars;
    private BiFunction<InventoryClickEvent, Integer, String> function;
    private String title;
    private Listener listener;
    private GachaBannerDictionary bdl = new GachaBannerDictionary();
    private Function<InventoryClickEvent, String> backFunction;
    public NumberInputAPI(String title, Player player, int numberOfChars, BiFunction<InventoryClickEvent, Integer, String> function, Function<InventoryClickEvent, String> backFunction){
        player.closeInventory();
        this.plugin = Bukkit.getPluginManager().getPlugin("Man10GachaV2");
        this.player = player;
        this.backFunction = backFunction;
        this.function = function;
        this.title = title;
        if(numberOfChars >= 9){
            numberOfChars = 9;
        }
        if(numberOfChars <= 1){
            numberOfChars = 1;
        }
        this.listener = new Listener(player.getUniqueId(), numberOfChars);
        this.numberOfChars = numberOfChars;
        SInventory inv = new SInventory(6, title);
                inv.fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDamage(11).setDisplayname(" ").build()).setItem(new int[]{41, 42,50,51}, new SItemStack(Material.EMERALD_BLOCK).setDisplayname("§a§l§nConfirm").build()).
                setItem(new int[]{43, 44, 52, 53}, new SItemStack(Material.REDSTONE_BLOCK).setDisplayname("§c§l§nCancel").build());
        inv.setItem(48, new SItemStack(Material.TNT).setDisplayname("§c§lClear").build());
        inv.setItem(new int[]{0,1,2,3,4,5,6,7,8}, new ItemStack(Material.AIR));
        int redTilesToPlace = 9 - numberOfChars;
        for(int i = 0;i < redTilesToPlace;i++){
            inv.setItem(i, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(14).setDisplayname(" ").build());
        }
        inv.setItem(new int[]{12, 15}, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(11).setGlowingEffect(true).setDisplayname("§a§l,").build());
        inv.setItem(46, bdl.getItem(0));
        inv.setItem(37, bdl.getItem(1));
        inv.setItem(38, bdl.getItem(2));
        inv.setItem(39, bdl.getItem(3));
        inv.setItem(28, bdl.getItem(4));
        inv.setItem(29, bdl.getItem(5));
        inv.setItem(30, bdl.getItem(6));
        inv.setItem(19, bdl.getItem(7));
        inv.setItem(20, bdl.getItem(8));
        inv.setItem(21, bdl.getItem(9));
        Bukkit.getServer().getPluginManager().registerEvents(this.listener, this.plugin);
        player.openInventory(inv.build());
    }

    public void closeInventory(){
        HandlerList.unregisterAll(this.listener);
        this.player.closeInventory();
    }

    class Listener implements org.bukkit.event.Listener{
        private UUID uuid;
        private String inputVal = "";
        private int maxChars;

        public Listener(UUID playerUUID, int maxChars){
            this.uuid = playerUUID;
            this.maxChars = maxChars;
        }

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(this.uuid != e.getWhoClicked().getUniqueId()){
                return;
            }
            int s = e.getSlot();
            e.setCancelled(true);
            if(s == 48) inputVal = "";
            if(s == 43 || s == 44 || s == 52 || s == 53){
                closeInventory();
                backFunction.apply(e);
                return;
            }
            if(s == 41 || s == 42 || s == 50 || s == 51){
                if(inputVal.equals("")){
                    String res = function.apply(e, 0);
                    if(res == null){
                        closeInventory();
                    }
                    return;
                }else{
                    String res = function.apply(e, Integer.valueOf(inputVal));
                    if(res == null){
                        closeInventory();
                    }
                    return;
                }
            }
            if(inputVal.length() == maxChars){
                return;
            }
            if(s == 46){
                if(inputVal.length() == 0){
                    return;
                }
                inputVal += "0";
            }
            if(s == 37) inputVal += "1";
            if(s == 38) inputVal += "2";
            if(s == 39) inputVal += "3";
            if(s == 28) inputVal += "4";
            if(s == 29) inputVal += "5";
            if(s == 30) inputVal += "6";
            if(s == 19) inputVal += "7";
            if(s == 20) inputVal += "8";
            if(s == 21) inputVal += "9";
            renderDisplay(e.getInventory());
        }

        @EventHandler
        public void onCLose(InventoryCloseEvent e){
            if(this.uuid != e.getPlayer().getUniqueId()){
                return;
            }
            closeInventory();
            backFunction.apply(null);
        }

        void renderDisplay(Inventory inv){
            int[] ints = new int[inputVal.length()];
            for(int i = 0;i < ints.length;i++){
                ints[i] = Integer.parseInt(String.valueOf(inputVal.charAt(i)));
            }
            int startFrom = 9 - ints.length;
            int startFromClear = 9 - maxChars;
            for(int i = 0;i < maxChars;i++){
                inv.setItem(startFromClear + i, new ItemStack(Material.AIR));
            }
            for(int i = 0;i < ints.length;i++){
               inv.setItem(startFrom + i, bdl.getItem(ints[i]));
            }
        }
    }

}

