package com.shojabon.man10gachav3.DataPackages;

import com.shojabon.man10gachav3.ToolPackages.SInventory;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiFunction;
import java.util.function.Function;

public class LocationSelectorAPI {
    Inventory inv;
    Listener listener = new Listener();
    JavaPlugin plugin;
    String gacha;
    Player p;
    BiFunction<InventoryClickEvent, Location, String> okFunction;
    Function<InventoryClickEvent, String> cancelFunction;
    GachaTeleport currentLoc;
    boolean movingMenu = false;
    public LocationSelectorAPI(String gacha, GachaTeleport currentLocation, Player p, BiFunction<InventoryClickEvent, Location, String> okFunction, Function<InventoryClickEvent, String> cancelFunction){
        p.closeInventory();
        this.gacha = gacha;
        this.p = p;
        this.currentLoc = currentLocation;
        this.okFunction = okFunction;
        this.cancelFunction = cancelFunction;
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        SInventory invv = new SInventory(6, gacha);
        invv.fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDamage(11).setDisplayname(" ").build());
        invv.setItem(21, new SItemStack(Material.BARRIER).setDisplayname("§c§l§nロケーションをクリアする").build());
        if(!currentLocation.useable()){
            invv.setItem(23, new SItemStack(Material.COMPASS).setDisplayname("§b§l現在設定")
                    .addLore("§b§lWorld:なし")
                    .addLore("§b§lX:なし")
                    .addLore("§b§lY:なし")
                    .addLore("§b§lZ:なし")
                    .addLore("§b§lPitch:なし")
                    .addLore("§b§lYaw:なし")
                    .addLore("§d§l§nクリックして現在ロケーションを保存")
                    .build());
        }else{
            invv.setItem(23, new SItemStack(Material.COMPASS).setDisplayname("§b§l現在設定")
                    .addLore("§b§lWorld:" + currentLocation.location.getWorld().getName())
                    .addLore("§b§lX:" + currentLocation.location.getX())
                    .addLore("§b§lY:" + currentLocation.location.getY())
                    .addLore("§b§lZ:" + currentLocation.location.getZ())
                    .addLore("§b§lPitch:" + currentLocation.location.getPitch())
                    .addLore("§b§lYaw:" + currentLocation.location.getYaw())
                    .addLore("§d§l§nクリックして現在ロケーションを保存")
                    .build());
        }
        invv.setItem(new int[]{48,49,50}, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(14).setDisplayname("§c§l§n決定").build());
        invv.setItem(53, new SItemStack(new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLUE, PatternType.CURLY_BORDER)).build()).setDisplayname("§c§l§n戻る").build());
        inv = invv.build();
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
            e.setCancelled(true);
            int r = e.getRawSlot();
            if(r == 23){
                currentLoc = new GachaTeleport(p.getLocation());
                inv.setItem(23, new SItemStack(Material.COMPASS).setDisplayname("§b§l現在設定")
                        .addLore("§b§lWorld:" + p.getLocation().getWorld().getName())
                        .addLore("§b§lX:" + p.getLocation().getX())
                        .addLore("§b§lY:" + p.getLocation().getY())
                        .addLore("§b§lZ:" + p.getLocation().getZ())
                        .addLore("§b§lPitch:" + p.getLocation().getPitch())
                        .addLore("§b§lYaw:" + p.getLocation().getYaw())
                        .addLore("§d§l§nクリックして現在ロケーションを保存")
                        .build());
            }
            if(r == 21){
                currentLoc = new GachaTeleport();
                inv.setItem(23, new SItemStack(Material.COMPASS).setDisplayname("§b§l現在設定")
                        .addLore("§b§lWorld:なし")
                        .addLore("§b§lX:なし")
                        .addLore("§b§lY:なし")
                        .addLore("§b§lZ:なし")
                        .addLore("§b§lPitch:なし")
                        .addLore("§b§lYaw:なし")
                        .addLore("§d§l§nクリックして現在ロケーションを保存")
                        .build());
            }
            if(r == 48 || r == 49 || r == 50) {
                movingMenu = true;
                if(!currentLoc.useable()){
                    String func = okFunction.apply(e, null);
                    if (func == null) {
                        p.closeInventory();
                        return;
                    }
                }else{
                    movingMenu = true;
                    String func = okFunction.apply(e, currentLoc.location);
                    if (func == null) {
                        p.closeInventory();
                        return;
                    }
                }
                e.setCancelled(true);
            }
            if(r == 53){
                movingMenu = true;
                cancelFunction.apply(e);
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close((Player) e.getPlayer());
            if(!movingMenu){
                cancelFunction.apply(null);
            }
        }

    }
}
