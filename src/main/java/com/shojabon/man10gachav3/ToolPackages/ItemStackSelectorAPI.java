package com.shojabon.man10gachav3.ToolPackages;

import com.shojabon.man10gachav3.DataPackages.SBannerItemStack;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ItemStackSelectorAPI {
    Inventory inv;
    Listener listener = new Listener();
    JavaPlugin plugin;
    Player p;
    boolean movingMenu = false;
    BiFunction<InventoryClickEvent, ItemStack, String> okFunction;
    Function<InventoryClickEvent, String> cancelFunction;
    public ItemStackSelectorAPI(String title, Player p, ItemStack currentItem, int amount, BiFunction<InventoryClickEvent, ItemStack, String> okFunction, Function<InventoryClickEvent, String> cancelFunction){
        p.closeInventory();
        this.p = p;
        this.okFunction = okFunction;
        this.cancelFunction = cancelFunction;
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        inv = new SInventory(3, title).fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDamage(11).build()).
                setItem(new int[]{11, 12}, new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname("§a§l決定").setDamage(5).build()).
                setItem(new int[]{14, 15}, new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname("§c§lキャンセル").setDamage(14).build()).setItem(13, new SItemStack(currentItem).setAmount(amount).build()).
        setItem(26, new SItemStack(new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLUE, PatternType.CURLY_BORDER)).build()).setDisplayname("§c§l§n戻る").build()).build();
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
            if(r >= 27){
                if(p.getInventory().getItem(e.getSlot()) != null) {
                    inv.setItem(13, new SItemStack(p.getInventory().getItem(e.getSlot()).clone()).build());
                }
                return;
            }
            if(r == 26){
                movingMenu = true;
                cancelFunction.apply(e);
                return;
            }
            if(r == 11 || r == 12){
                movingMenu = true;
                String res = okFunction.apply(e, inv.getItem(13));
                if(res == null){
                    e.getWhoClicked().closeInventory();
                }
                return;
            }
            if(r == 14 || r == 15){
                movingMenu = true;
                cancelFunction.apply(e);
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
