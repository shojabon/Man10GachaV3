package com.shojabon.man10gachav3.ToolPackages;

import com.shojabon.man10gachav3.DataPackages.SBannerItemStack;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiFunction;
import java.util.function.Function;

public class BooleanSelectorAPI {
    Inventory inv;
    Listener listener = new Listener();
    JavaPlugin plugin;
    Player p;
    String title;
    ItemStack itemsToDisplay;
    boolean current;
    Function<InventoryClickEvent, String> backFunction;
    BiFunction<InventoryClickEvent, Boolean, String> biFunction;
    public BooleanSelectorAPI(String title, Player p, ItemStack itemToDisplay, boolean current, BiFunction<InventoryClickEvent, Boolean, String> biFunction, Function<InventoryClickEvent, String> backFunction){
        p.closeInventory();
        this.title = title;
        this.p = p;
        this.backFunction = backFunction;
        this.itemsToDisplay = itemToDisplay;
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        this.biFunction = biFunction;
        SInventory inv = new SInventory(5, title);
        inv.fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDamage( (short) 11).setDisplayname(" ").build());
        inv.setItem(31, new SItemStack(Material.STORAGE_MINECART).setDisplayname("§b§n§l変更を保存する").build());
        inv.setItem(13, itemsToDisplay);
        inv.setItem(44, new SItemStack(new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLUE, PatternType.CURLY_BORDER)).build()).setDisplayname("§c§l§n戻る").build());
        this.inv = inv.build();
        render(current);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        p.openInventory(this.inv);
    }

    private void render(boolean b){
        ItemStack trueGlass = new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname("§a§lTrue").setDamage((short) 5).build();
        ItemStack falseGlass = new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname("§c§lFalse").setDamage((short) 14).build();
        if(b){
            inv.setItem(29, new SItemStack(trueGlass).setGlowingEffect(true).build());
            inv.setItem(30, new SItemStack(trueGlass).setGlowingEffect(true).build());
            inv.setItem(32, falseGlass);
            inv.setItem(33, falseGlass);
        }else{
            inv.setItem(29, trueGlass);
            inv.setItem(30, trueGlass);
            inv.setItem(32, new SItemStack(falseGlass).setGlowingEffect(true).build());
            inv.setItem(33, new SItemStack(falseGlass).setGlowingEffect(true).build());
        }
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
            int s = e.getRawSlot();
            if(s == 44){
                backFunction.apply(e);
                return;
            }
            if(s == 31){
                biFunction.apply(e, current);
                return;
            }
            if(s == 29 || s == 30){
                current = true;
                p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                render(true);
            }else if(s == 33 || s == 32){
                current = false;
                p.playSound(p.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 1);
                render(false);
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close((Player) e.getPlayer());
            backFunction.apply(null);
        }

    }
}
