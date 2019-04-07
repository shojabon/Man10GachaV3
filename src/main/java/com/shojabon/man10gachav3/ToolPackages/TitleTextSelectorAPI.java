package com.shojabon.man10gachav3.ToolPackages;

import com.shojabon.man10gachav3.DataPackages.GachaBannerDictionary;
import com.shojabon.man10gachav3.DataPackages.GachaTitleText;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TitleTextSelectorAPI {
    Inventory inv;
    Listener listener = new Listener();
    JavaPlugin plugin;
    Player p;

    GachaTitleText titleText;
    BiFunction<InventoryClickEvent, GachaTitleText, String> okFucntion;
    Function<InventoryClickEvent, String> cancelFunction;
    GachaBannerDictionary bd = new GachaBannerDictionary();

    boolean travel = false;


    public TitleTextSelectorAPI(Player p, String title, GachaTitleText titleText, BiFunction<InventoryClickEvent, GachaTitleText, String> okFucntion, Function<InventoryClickEvent, String> cancelFunction){
        this.p = p;
        p.closeInventory();
        this.titleText = titleText;
        this.okFucntion = okFucntion;
        this.cancelFunction = cancelFunction;

        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        SInventory inventory = new SInventory(5, title);
        inventory.fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDamage(11).build());
        inventory.setItem(new int[]{40, 41, 42}, new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname("§c§l決定").setDamage(14).build());
        inv = inventory.build();
        inv.setItem(44, bd.getSymbol("back"));
        render(titleText);
        p.openInventory(inv);
    }

    private void render(GachaTitleText titleText){
        inv.setItem(10, new SItemStack(Material.SIGN).setDisplayname("§b§lメインタイトル").addLore("§b§l現在設定：" + titleText.getMainText()).build());
        inv.setItem(28, new SItemStack(Material.SIGN).setDisplayname("§b§lサブテキスト").addLore("§b§l現在設定：" + titleText.getSubText()).build());
        inv.setItem(19, new SItemStack(Material.BARRIER).setDisplayname("§b§lタイトル設定をクリアする").build());
        inv.setItem(21, new SItemStack(Material.WATCH).setDisplayname("§b§lフェードイン時間設定").addLore("§b§l現在設定：" + titleText.getFadeinTime()).build());
        inv.setItem(23, new SItemStack(Material.WATCH).setDisplayname("§b§l表示時間設定").addLore("§b§l現在設定：" + titleText.getTime()). build());
        inv.setItem(25, new SItemStack(Material.WATCH).setDisplayname("§b§lフェードアウト時間設定").addLore("§b§l現在設定：" + titleText.getFadeoutTime()).build());
    }

    private void close(Player p){
        HandlerList.unregisterAll(listener);
        p.closeInventory();
    }

    private void reopen(){
        travel = false;
        new BukkitRunnable(){

            @Override
            public void run() {
                render(titleText);
                p.openInventory(inv);
                Bukkit.getPluginManager().registerEvents(listener, plugin);
            }
        }.runTaskLater(plugin, 1);
    }

    class Listener implements org.bukkit.event.Listener
    {

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getWhoClicked().getUniqueId() != p.getUniqueId()) return;
            e.setCancelled(true);
            int r  = e.getRawSlot();
            if(r == 40 || r == 41 || r == 42){
                String s = okFucntion.apply(e, titleText);
                if(s == null){
                    close(p);
                    return;
                }
            }
            switch (r){
                case 19:{
                    titleText = new GachaTitleText();
                    render(titleText);
                    break;
                }
                case 44:{
                    cancelFunction.apply(null);
                    return;
                }
                case 10:{
                    travel = true;
                    new LongTextInputAPI(p, "§c§lメインテキストを入力してください", (player, s) -> {
                        titleText.setMainText(s);
                        reopen();
                        return null;
                    }, player -> {
                        reopen();
                        return null;
                    });
                    break;
                }
                case 28:{
                    travel = true;
                    new LongTextInputAPI(p, "§c§lサブテキストを入力してください", (player, s) -> {
                        titleText.setSubText(s);
                        reopen();
                        return null;
                    }, player -> {
                        reopen();
                        return null;
                    });
                    break;
                }
                case 21:{
                    travel = true;
                    new NumberInputAPI("§c§lフェードイン時間を入力してください", p, 9, (event, integer) -> {
                        titleText.setFadeinTime(integer);
                        reopen();
                        return null;
                    }, event -> {
                        reopen();
                        return null;
                    });
                    break;
                }
                case 23:{
                    travel = true;
                    new NumberInputAPI("§c§lタイトル表示時間を入力してください", p, 9, (event, integer) -> {
                        titleText.setTime(integer);
                        reopen();
                        return null;
                    }, event -> {
                        reopen();
                        return null;
                    });
                    break;
                }
                case 25:{
                    travel = true;
                    new NumberInputAPI("§c§lフェードアウト時間を入力してください", p, 9, (event, integer) -> {
                        titleText.setFadeoutTime(integer);
                        reopen();
                        return null;
                    }, event -> {
                        reopen();
                        return null;
                    });
                    break;
                }
            }
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close((Player) e.getPlayer());
            if(!travel){
                cancelFunction.apply(null);
            }
        }

    }
}
