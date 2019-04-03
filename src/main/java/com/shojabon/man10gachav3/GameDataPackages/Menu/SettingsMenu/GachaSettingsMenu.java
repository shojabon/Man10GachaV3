package com.shojabon.man10gachav3.GameDataPackages.Menu.SettingsMenu;

import com.shojabon.man10gachav3.DataPackages.GachaSettings;
import com.shojabon.man10gachav3.DataPackages.SBannerItemStack;
import com.shojabon.man10gachav3.GamePackages.Man10GachaAPI;
import com.shojabon.man10gachav3.ToolPackages.SInventory;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Function;

public class GachaSettingsMenu {
    Inventory inv;
    Listener listener;
    JavaPlugin plugin;
    String gacha;
    Player p;
    Man10GachaAPI api;

    private void reopenMenu(){
        new BukkitRunnable(){

            @Override
            public void run() {
                new GachaSettingsMenu(gacha, p);
            }
        }.runTaskLater(plugin, 1);
    }


    public GachaSettingsMenu(String gacha, Player p){
        p.closeInventory();
        this.gacha = gacha;
        this.p = p;
        this.api = new Man10GachaAPI();
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        this.generalSettingsMenu = new GachaGeneralSettingsMenu(gacha, p);
        inv = new SInventory(5, "§b§l" + gacha + "：設定メニュー").fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDamage(11).setDisplayname(" ").build()).
        setItem(new int[]{12, 13, 14, 21,23,30,31,32}, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(14).setDisplayname(" ").build()).
        setItem(11, new SItemStack(Material.EMERALD).setDisplayname("§a§l§n価格設定").build()).
        setItem(15, new SItemStack(Material.SIGN).setDisplayname("§6§l§n看板設定").build()).
        setItem(22, new SItemStack(Material.CHEST).setDisplayname("§8§l§n§k00§7§l§nコンテナ設定§8§l§n§k00").setGlowingEffect(true).build()).
        setItem(29, new SItemStack(Material.NETHER_STAR).setDisplayname("§f§l§n一般設定").build()).
        setItem(33,new SItemStack(Material.DISPENSER).setDisplayname("§7§l§n統計データ").build()).
        setItem(44, new SItemStack(new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLUE, PatternType.CURLY_BORDER)).build()).setDisplayname("§c§l§n戻る").build()).build();
        listener = new Listener(p);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        p.openInventory(inv);
    }

    private void close(Player p){
        HandlerList.unregisterAll(listener);
        p.closeInventory();
    }

    GachaGeneralSettingsMenu generalSettingsMenu;

    class Listener implements org.bukkit.event.Listener
    {
        Player p;
        Listener(Player p){
            this.p = p;
        }

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getWhoClicked().getUniqueId() != p.getUniqueId()) return;
            e.setCancelled(true);
            new Thread(() -> {
                if(e.getRawSlot() == 44) new GachaSettingsSelectionMenu(p);
                if(e.getRawSlot() == 29) generalSettingsMenu.createMenu(0,0);
                if(e.getRawSlot() == 11){
                    new GachaPaymentSettingsMenu(gacha, p, event -> {
                        api.updateGacha(api.getGacha(gacha));
                        reopenMenu();
                        return null;
                    });
                }
                if(e.getRawSlot() == 22) {
                    new GachaContainerSettingsMenu(gacha, p, event -> {
                        api.updateGacha(api.getGacha(gacha));
                        reopenMenu();
                        return null;
                    });
                }
            }).start();
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close((Player) e.getPlayer());
        }


    }

}
