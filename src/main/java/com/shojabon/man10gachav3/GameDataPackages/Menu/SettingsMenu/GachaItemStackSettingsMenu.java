package com.shojabon.man10gachav3.GameDataPackages.Menu.SettingsMenu;


import com.shojabon.man10gachav3.DataPackages.CategorizedMenuCategory;
import com.shojabon.man10gachav3.DataPackages.GachaBannerDictionary;
import com.shojabon.man10gachav3.DataPackages.GachaItemStack;
import com.shojabon.man10gachav3.GameDataPackages.GachaSound;
import com.shojabon.man10gachav3.GameDataPackages.Menu.CategorizedMenuAPI;
import com.shojabon.man10gachav3.ToolPackages.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GachaItemStackSettingsMenu {
    GachaContainerSettingsMenu menu;
    Function<InventoryClickEvent, String> cancelFunction;
    GachaItemStack gItemStack;
    Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10GachaV3");
    int index;
    int amount;
    private ItemStack renderItemList(SItemStack itemStack, ArrayList<String> string){
        if(string == null){
            return itemStack.addLore("§b§l現在設定：なし").build();
        }
        for(String o : string){
            itemStack.addLore("§b§l" + o);
        }
        return itemStack.build();
    }

    public GachaItemStackSettingsMenu(GachaContainerSettingsMenu menu, int index, Function<InventoryClickEvent, String> cancelFunction){
        menu.p.closeInventory();
        this.index = index;
        this.menu = menu;
        gItemStack = menu.game.getItemIndex().get(index);
        this.cancelFunction = cancelFunction;
        createMenu(0, 0);
    }

    public void createMenu(int startCategory, int startPage){
        List<ItemStack> generalSettingsItem = new ArrayList<>();
        String name = gItemStack.item.getType().name();
        if(gItemStack.item.getItemMeta().getDisplayName() != null) name = gItemStack.item.getItemMeta().getDisplayName();
        generalSettingsItem.add(new SItemStack(Material.NAME_TAG).setDisplayname("§c§l§nアイテム設定").addLore("§b§l現在設定:" + name + " §b§l" + gItemStack.amount).build());
        generalSettingsItem.add(new SItemStack(Material.CHEST).setDisplayname("§6§l§nストレージ個数設定設定").addLore("§b§l現在設定:" + menu.game.getStorageAmount().get(index)+ " §b§l").build());

        List<ItemStack> messageSettings = new ArrayList<>();
        List<ItemStack> soundSettings = new ArrayList<>();
        List<ItemStack> permissionSettings = new ArrayList<>();

        List<ItemStack> vaultSettings = new ArrayList<>();
        List<ItemStack> itemBankSettings = new ArrayList<>();
        List<ItemStack> misc = new ArrayList<>();
        SItemStack playerMessage = new SItemStack(Material.BOOK).setDisplayname("§6§l§nプレイヤーメッセージ設定");
        messageSettings.add(renderItemList(playerMessage, gItemStack.playerMessage));

        SItemStack broadcastMessage = new SItemStack(Material.BOOKSHELF).setDisplayname("§6§l§nサーバーメッセージ設定設定");
        messageSettings.add(renderItemList(broadcastMessage, gItemStack.broadcastMessage));

        List<CategorizedMenuCategory> categories = new ArrayList<>();
        categories.add(new CategorizedMenuCategory(new SItemStack(Material.DIAMOND).setDisplayname("§7§n§l主要設定").build(), generalSettingsItem));
        categories.add(new CategorizedMenuCategory(new SItemStack(Material.NOTE_BLOCK).setDisplayname("§a§n§lサ§b§n§lウ§c§n§lン§d§n§lド§e§n§l設§f§n§l定").build(), soundSettings));
        categories.add(new CategorizedMenuCategory(new SItemStack(Material.BOOK_AND_QUILL).setDisplayname("§6§n§lメッセージ設定").build(), messageSettings));
        categories.add(new CategorizedMenuCategory(new SItemStack(Material.BARRIER).setDisplayname("§c§n§l権限設定").build(), permissionSettings));
        categories.add(new CategorizedMenuCategory(new SItemStack(Material.EMERALD).setDisplayname("§e§n§lVault設定").build(), vaultSettings));
        categories.add(new CategorizedMenuCategory(new SItemStack(Material.STORAGE_MINECART).setDisplayname("§8§n§lアイテムバンク設定").build(), itemBankSettings));
        categories.add(new CategorizedMenuCategory(new SItemStack(Material.LAVA_BUCKET).setDisplayname("§b§n§lその他の設定").build(), misc));
        new CategorizedMenuAPI("", menu.p, categories, (event, categorizedMenuLocation) -> {
            onClick(categorizedMenuLocation.getCategory(), categorizedMenuLocation.getNum());
            return null;
        }, cancelFunction, startCategory, startPage);
    }

    private void reopenMenu(int startCategory, int startPage){
        new BukkitRunnable(){

            @Override
            public void run() {
                createMenu(startCategory, startPage);
            }
        }.runTaskLater(plugin, 1);
    }

    private void onClick(int category, int slot){
        switch (category){
            case 0: {
                generalSettings(slot);
                break;
            }
            case 1:{
                soundSettings(slot);
                break;
            }
            case 2:{
                messageSettings(slot);
                break;
            }
            case 3:{
                permissionSettings(slot);
                break;
            }
            case 4:{
                vaultSettings(slot);
                break;
            }
            case 5:{
                //itembank
                break;
            }
            case 6:{
                miscSettings(slot);
            }
        }
    }

    private void pushSettings(){
        menu.game.getItemIndex().set(index, gItemStack);
    }

    private void generalSettings(int slot){
        Player p = menu.p;
        switch (slot){
            case 0:{
                //アイテム設定
                new ItemStackSelectorAPI("§b§lアイテムを選択してください", p, gItemStack.item, gItemStack.amount, (event, itemStack) -> {
                    gItemStack.item = itemStack;
                    gItemStack.amount = itemStack.getAmount();
                    pushSettings();
                    reopenMenu(0,0);
                    return null;
                }, event -> {
                    reopenMenu(0,0);
                    return null;
                });
                break;
            }
            case 1:{
                //ストレージ個数設定
                new NumberInputAPI("§b§lストレージ個数を選択してください", p, 9, (event, integer) -> {
                    menu.game.setStorageAmound(index, amount);
                    pushSettings();
                    reopenMenu(0,0);
                    return null;
                }, event -> {
                    reopenMenu(0,0);
                    return null;
                });
            }
        }
    }

    private void soundSettings(int slot){
        switch (slot){
            case 0:{
                //プレイヤー再生音設定
                GachaSound sound = gItemStack.playerSound;
                new SoundSelectorAPI("§b§lプレイヤー再生音を選択してください", menu.p, sound.getVolume(), sound.getPitch(), sound.getSound(), (event, gachaSound) -> {
                    gItemStack.playerSound = gachaSound;
                    pushSettings();
                    reopenMenu(1, 0);
                    return null;
                }, event -> {
                    reopenMenu(1, 0);
                    return null;
                });
                break;
            }
            case 1:{
                //サーバー再生音設定
                GachaSound sound = gItemStack.playerSound;
                new SoundSelectorAPI("§b§lサーバー再生音を選択してください", menu.p, sound.getVolume(), sound.getPitch(), sound.getSound(), (event, gachaSound) -> {
                    gItemStack.playerSound = gachaSound;
                    pushSettings();
                    reopenMenu(1, 0);
                    return null;
                }, event -> {
                    reopenMenu(1, 0);
                    return null;
                });
                break;
            }
        }
    }

    private void messageSettings(int slot){
        switch (slot){
            case 0:{
                //プレイヤーメッセージ設定
                new StringListEditorAPI(menu.p, gItemStack.playerMessage, (strings, player) -> {
                    gItemStack.playerMessage = new ArrayList<>(strings);
                    pushSettings();
                    reopenMenu(2,0);
                    return null;
                }, strings -> {
                    reopenMenu(2,0);
                    return null;
                });
                break;
            }
            case 1:{
                //サーバーメッセージ設定
                new StringListEditorAPI(menu.p, gItemStack.broadcastMessage, (strings, player) -> {
                    gItemStack.broadcastMessage = new ArrayList<>(strings);
                    pushSettings();
                    reopenMenu(2,0);
                    return null;
                }, strings -> {
                    reopenMenu(2,0);
                    return null;
                });
                break;
            }
        }
    }

    private void permissionSettings(int slot){
        switch (slot){
        }
    }

    private void vaultSettings(int slot){
        switch (slot){
        }
    }

    private void itemBankSettings(int slot){
    }

    private void miscSettings(int slot){
        switch (slot){
        }
    }



}
