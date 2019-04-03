package com.shojabon.man10gachav3.ToolPackages;

import com.shojabon.man10gachav3.DataPackages.CategorizedMenuCategory;
import com.shojabon.man10gachav3.DataPackages.SBannerItemStack;
import com.shojabon.man10gachav3.ToolPackages.SInventory;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class CategorizedMenuAPI {
    Inventory inv;
    Listener listener = new Listener();
    JavaPlugin plugin;
    Player p;
    int start = 0;

    int currentCategory = 0;
    int currentSelectionPage = 0;

    List<CategorizedMenuCategory> category;
    Function<InventoryClickEvent, String> clickBackFunction;
    BiFunction<InventoryClickEvent, CategorizedMenuLocation, String> clickFunction;
    HashMap<Integer, Integer> categorySlotToCategoryId = new HashMap<>();
    HashMap<Integer, Integer> selectionSlotToSelectionId = new HashMap<>();
    public CategorizedMenuAPI(String title, Player p, List<CategorizedMenuCategory> category, BiFunction<InventoryClickEvent, CategorizedMenuLocation, String> clickFunction, Function<InventoryClickEvent, String> clickBackFunction, int startCategory, int startPage){
        p.closeInventory();
        this.category = category;
        this.clickBackFunction = clickBackFunction;
        this.clickFunction = clickFunction;
        this.p = p;
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        currentCategory = startCategory;
        currentSelectionPage = startPage;
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        inv = new SInventory(6, title).setItem(53, new SItemStack(new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLUE, PatternType.CURLY_BORDER)).build()).setDisplayname("§c§l§n戻る").build()).
                setItem(new int[]{9,10,11,12,13,14,15,16,17,45,46,47,48,49,50,51,52}, new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDamage(11).build())
                .setItem(new int[]{18,27,36,26,35,36,44},
                        new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDamage(14).build()).setItem(0, new SItemStack(new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLUE, PatternType.CURLY_BORDER)).build()).setDisplayname("§d§l左").build()).setItem(8, new SItemStack(new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_RIGHT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLUE, PatternType.CURLY_BORDER)).build()).setDisplayname("§d§l右").build()).build();
        renderFirstInventory();
        p.openInventory(inv);
    }

    private void close(Player p){
        HandlerList.unregisterAll(listener);
        p.closeInventory();
    }

    private void renderCategory(){
        new BukkitRunnable(){

            @Override
            public void run() {
                for(int i = 0;i < 7;i++){
                    inv.setItem(i + 1, new ItemStack(Material.AIR));
                }
                int slots = 7;
                if(category.size() <= slots){
                    slots = category.size();
                }
                for(int i = 0;i < slots;i++){
                    if(currentCategory == start + i){
                        inv.setItem(i + 1, new SItemStack(category.get(i + start).getIcon()).setGlowingEffect(true).build());
                    }else{
                        inv.setItem(i + 1, new SItemStack(category.get(i + start).getIcon()).setGlowingEffect(false).build());
                    }
                    categorySlotToCategoryId.put(i + 1, i + start);
                }
            }
        }.runTaskLater(plugin, 0);
    }

    private void renderFirstInventory(){
        renderCategory();
        renderSelection();
    }

    int[] selectionMenu = {19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};

    private void renderSelection(){
        for(int i = 0;i < 21;i++){
            inv.setItem(selectionMenu[i], new ItemStack(Material.AIR));
        }
        int slot = 21;
        if(this.category.get(currentCategory).getContent().size() - currentSelectionPage * 21 <= slot){
            slot = this.category.get(currentCategory).getContent().size() - currentSelectionPage * 21;
        }
        for(int i = 0;i < slot;i++){
            inv.setItem(selectionMenu[i], this.category.get(currentCategory).getContent().get(i + 21 * currentSelectionPage));
            selectionSlotToSelectionId.put(selectionMenu[i], i + 21 * currentSelectionPage);
        }
    }


    class Listener implements org.bukkit.event.Listener
    {
        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getWhoClicked().getUniqueId() != p.getUniqueId()) return;
            e.setCancelled(true);
            new Thread(()->{
                p.playSound(p.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
                if(e.getRawSlot() == 53) clickBackFunction.apply(e);
                int s = e.getRawSlot();
                if(s == 26 || s == 35 || s == 44){
                    if(category.get(currentCategory).getContent().size() - (currentSelectionPage + 1)  * 21 >= 0){
                        currentSelectionPage += 1;
                        renderSelection();
                    }
                    return;
                }
                if(s == 18 || s == 27 || s == 36){
                    if(currentSelectionPage - 1 >= 0){
                        currentSelectionPage -= 1;
                        renderSelection();
                    }
                    return;
                }
                if(e.getRawSlot() == 0){
                    if(start - 1 >= 0){
                        start -= 1;
                        renderCategory();
                    }
                }
                if(e.getRawSlot() == 8){
                    if(category.size() - 7 > start){
                        start += 1;
                        renderCategory();
                    }
                }
                if(e.getRawSlot() <= 7 && e.getRawSlot() >= 1){
                    currentCategory = categorySlotToCategoryId.get(e.getRawSlot());
                    currentSelectionPage = 0;
                    renderCategory();
                    renderSelection();
                    return;
                }
                if(e.getRawSlot() >= 19 && e.getRawSlot() <= 43) {
                    if(e.getClickedInventory().getItem(e.getRawSlot()) != null){
                        clickFunction.apply(e, new CategorizedMenuLocation(currentCategory, selectionSlotToSelectionId.get(e.getRawSlot())));
                    }
                }
            }).start();
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close((Player) e.getPlayer());
        }

    }

    public class CategorizedMenuLocation{
        private int category;
        private int num;
        public CategorizedMenuLocation(int category, int num){
            this.category = category;
            this.num = num;
        }

        public int getCategory() {
            return category;
        }

        public int getNum() {
            return num;
        }
    }
}
