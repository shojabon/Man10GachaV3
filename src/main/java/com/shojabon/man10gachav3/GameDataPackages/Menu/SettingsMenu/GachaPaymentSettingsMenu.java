package com.shojabon.man10gachav3.GameDataPackages.Menu.SettingsMenu;

import com.shojabon.man10gachav3.DataPackages.GachaBannerDictionary;
import com.shojabon.man10gachav3.DataPackages.GachaPayment;
import com.shojabon.man10gachav3.DataPackages.GachaPaymentData.GachaItemStackPayment;
import com.shojabon.man10gachav3.DataPackages.GachaPaymentData.GachaVaultPayment;
import com.shojabon.man10gachav3.DataPackages.GachaSound;
import com.shojabon.man10gachav3.GamePackages.GachaGame;
import com.shojabon.man10gachav3.GamePackages.Man10GachaAPI;
import com.shojabon.man10gachav3.ToolPackages.ItemStackSelectorAPI;
import com.shojabon.man10gachav3.ToolPackages.NumberInputAPI;
import com.shojabon.man10gachav3.ToolPackages.SInventory;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import com.shojabon.man10gachav3.enums.GachaPaymentType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Function;

public class GachaPaymentSettingsMenu {
    Inventory inv;
    Listener listener = new Listener();
    JavaPlugin plugin;
    String gacha;
    Player p;
    GachaGame game;
    Man10GachaAPI api;
    GachaBannerDictionary dict = new GachaBannerDictionary();
    Function<InventoryClickEvent, String> cancelFunction;
    public GachaPaymentSettingsMenu(String gacha, Player p, Function<InventoryClickEvent, String> cancelFunction){
        p.closeInventory();
        this.cancelFunction = cancelFunction;
        this.gacha = gacha;
        this.api = new Man10GachaAPI();
        this.game = api.getGacha(gacha);
        this.p = p;
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        Bukkit.getPluginManager().registerEvents(listener, plugin);

        SInventory inve = new SInventory(3, "§b§l" + gacha + "金額設定");
        inve.fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDamage(11).setDisplayname(" ").build());
        inve.setItem(11, new SItemStack(Material.BARRIER).setDisplayname("§c§lアイテム設定をクリア").build());
        inve.setItem(14, new SItemStack(Material.BARRIER).setDisplayname("§c§l金額設定をクリア").build());
        inve.setItem(26, new SItemStack(dict.getSymbol("back")).setDisplayname("§c§l戻る").build());
        inv = inve.build();
        render();

        p.openInventory(inv);
    }

    private void close(Player p){
        HandlerList.unregisterAll(listener);
    }

    private void render(){
        inv.setItem(12, new SItemStack(Material.CHEST).setDisplayname("§c§lアイテム設定なし").build());
        inv.setItem(15, new SItemStack(Material.EMERALD).setDisplayname("§a§l金額設定").addLore("§6§l設定金額：なし").build());
        ArrayList<GachaPayment> payments = game.getPayments();
        for(int i =0; i < payments.size(); i++){
            GachaPaymentType type = payments.get(i).getType();
            if(type == GachaPaymentType.ITEM){
                inv.setItem(12, new SItemStack(payments.get(i).getItemStackPayment().getItemStack()).setAmount(payments.get(i).getItemStackPayment().getAmount()).build());
            }else if(type == GachaPaymentType.VAULT){
                inv.setItem(15, new SItemStack(Material.EMERALD).setDisplayname("§a§l金額設定").addLore("§6§l設定設定金額：" + payments.get(i).getVaultPayment().getValue()).build());
            }
        }
    }

    private void reopen(){
        new BukkitRunnable(){

            @Override
            public void run() {
                p.closeInventory();
                Bukkit.getPluginManager().registerEvents(listener, plugin);
                SInventory inve = new SInventory(3, "§b§l" + gacha + "金額設定");
                inve.fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDamage(11).setDisplayname(" ").build());
                inve.setItem(11, new SItemStack(Material.BARRIER).setDisplayname("§c§lアイテム設定をクリア").build());
                inve.setItem(14, new SItemStack(Material.BARRIER).setDisplayname("§c§l金額設定をクリア").build());
                inve.setItem(26, new SItemStack(dict.getSymbol("back")).setDisplayname("§c§l戻る").build());
                inv = inve.build();
                render();

                p.openInventory(inv);
            }
        }.runTaskLater(plugin, 1);
    }

    class Listener implements org.bukkit.event.Listener
    {

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getWhoClicked().getUniqueId() != p.getUniqueId()) return;
            e.setCancelled(true);
            if(e.getRawSlot() <= 26 && e.getRawSlot() != -999 && e.getInventory().getItem(e.getRawSlot()) != null) new GachaSound(Sound.BLOCK_DISPENSER_DISPENSE, 1 ,1).playSoundToPlayer((Player) e.getWhoClicked());
            if(e.getRawSlot() == 26){
                cancelFunction.apply(e);
                return;
            }
            if(e.getRawSlot() == 12){
                ItemStack noItem = new SItemStack(new ItemStack(Material.CHEST)).setDisplayname("§c§l現在設定なし").build();
                int amo = 1;
                for(GachaPayment payment : game.getPayments()){
                    if(payment.getType() == GachaPaymentType.ITEM){
                        noItem = payment.getItemStackPayment().getItemStack();
                        amo = payment.getItemStackPayment().getAmount();
                    }
                }
                new ItemStackSelectorAPI("§b§l使用アイテム設定", p, noItem, amo, (event, itemStack) -> {
                    if(new SItemStack(new ItemStack(Material.CHEST)).setDisplayname("§c§l現在設定なし").build().isSimilar(itemStack)) {
                        reopen();
                        return null;
                    }
                    game.updatePayments(GachaPaymentType.ITEM, new GachaPayment(new GachaItemStackPayment(itemStack, itemStack.getAmount())));
                    reopen();
                    return null;
                }, event -> {
                    reopen();
                    return null;
                });
            }


            if(e.getRawSlot() == 15){
                new NumberInputAPI("§b§l使用金額設定", p, 9, (event, integer) -> {
                    game.updatePayments(GachaPaymentType.VAULT, new GachaPayment(new GachaVaultPayment(integer)));
                    reopen();
                    return null;
                }, event -> {
                    reopen();
                    return null;
                });
            }

            if(e.getRawSlot() == 14){
                game.deletePayment(GachaPaymentType.VAULT);
                render();
            }
            if(e.getRawSlot() == 11){
                game.deletePayment(GachaPaymentType.ITEM);
                render();
            }



        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close(p);
        }

    }
}
