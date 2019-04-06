package com.shojabon.man10gachav3.GameDataPackages.Menu.SettingsMenu;

import com.shojabon.man10gachav3.DataPackages.CategorizedMenuCategory;
import com.shojabon.man10gachav3.DataPackages.GachaSound;
import com.shojabon.man10gachav3.ToolPackages.CategorizedMenuAPI;
import com.shojabon.man10gachav3.GamePackages.GachaGame;
import com.shojabon.man10gachav3.GamePackages.Man10GachaAPI;
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

public class GachaGeneralSettingsMenu {
    String gacha;
    Player p;
    Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10GachaV3");
    Man10GachaAPI api;
    String prefix = "§6[§aMg§fac§dha§5V2§6]§f";
    GachaGame game;
    int startCategory;
    int startPage;
    Function<GachaGame, String> clickBackFunction;
    public GachaGeneralSettingsMenu(String gacha, Player p, int startCategory, int startPage, Function<GachaGame, String> clickBackFunction){
        this.gacha = gacha;
        this.p = p;
        this.clickBackFunction = clickBackFunction;
        this.startCategory = startCategory;
        this.startPage = startPage;
        api = new Man10GachaAPI();
        game =  Man10GachaAPI.gachaGameMap.get(gacha);
        createMenu(startCategory, startPage);
    }
    public void createMenu(int startCategory, int startPage){
        p.closeInventory();
        game = Man10GachaAPI.gachaGameMap.get(gacha);
        List<ItemStack> generalSettingsItem = new ArrayList<>();
        generalSettingsItem.add(new SItemStack(Material.NAME_TAG).setDisplayname("§c§l§nガチャの登録名設定").addLore("§b§l現在設定:" + gacha).build());
        generalSettingsItem.add(new SItemStack(Material.ANVIL).setDisplayname("§c§l§nガチャのタイトル設定").addLore("§b§l現在設定:" + game.getSettings().title.replaceAll("&", "§")).build());
        generalSettingsItem.add(new SItemStack(Material.PAINTING).setDisplayname("§c§l§nアイコン設定").addLore("§b§l現在設定:" + game.getSettings().icon.getType().name()).build());

        List<ItemStack> soundSettings = new ArrayList<>();
        Map<String, String> soundMap = game.getSettings().spinSound.getStringData();
        soundSettings.add(new SItemStack(Material.NOTE_BLOCK).setDisplayname("§c§l§n回転音設定").addLore("§b§l音名:" + soundMap.get("sound")).addLore("§b§lボリューム:" + soundMap.get("volume")).addLore("§b§lピッチ:" + soundMap.get("pitch")).build());

        List<ItemStack> misc = new ArrayList<>();
        //misc.add(new SItemStack(Material.COMPASS).setDisplayname("§c§l§n確率表示設定").addLore("§b§l現在設定:" + game.getSettings().showPercentage).build());


        List<CategorizedMenuCategory> categories = new ArrayList<>();
        categories.add(new CategorizedMenuCategory(new SItemStack(Material.DIAMOND).setDisplayname("§7§n§l主要設定").build(), generalSettingsItem));
        categories.add(new CategorizedMenuCategory(new SItemStack(Material.NOTE_BLOCK).setDisplayname("§a§n§lサ§b§n§lウ§c§n§lン§d§n§lド§e§n§l設§f§n§l定").build(), soundSettings));
        categories.add(new CategorizedMenuCategory(new SItemStack(Material.LAVA_BUCKET).setDisplayname("§b§n§lその他の設定").build(), misc));

        new CategorizedMenuAPI("§f§l" + gacha + "の一般設定", p, categories, (event, categorizedMenuLocation) -> {
            p.closeInventory();
            settings(categorizedMenuLocation.getCategory(), categorizedMenuLocation.getNum());
            return null;
        }, event -> {
            clickBackFunction.apply(game);
            return null;
        }, startCategory, startPage);
    }

    private void restartMenu(int startCategory, int startPage){
        new BukkitRunnable(){

            @Override
            public void run() {
                new GachaGeneralSettingsMenu(gacha, p , startCategory, startPage, clickBackFunction);
            }
        }.runTaskLater(plugin, 2);
    }

    private void pushSettings(){
        api.printSettings(gacha, game.getSettings());
        api.reloadGacha(gacha);
    }


    private void settings(int category, int id){
        switch (category){
            case 0: {
                generalCategory(id);
                return;
            }
            case 1:{
                soundGategory(id);
                return;
            }
            case 2:{
                miscCategory(id);
                return;
            }
        }
    }


    private void generalCategory(int id){
        switch(id){
            case 0: {
                //登録名変更設定
                new AnvilGUI(p, gacha, (event, s) -> {
                    if (event == null) {
                        restartMenu(0,0);
                        return null;
                    }
                    if (s == null) {
                        p.sendMessage(prefix + "§c§lガチャ名に空白は使えません");
                        return "restart";
                    }
                    if (gacha.equalsIgnoreCase(s)) {
                        p.sendMessage(prefix + "§c§l変更予定名が現在名と同じです");
                        restartMenu(0, 0);
                        return null;
                    }
                    int n = api.renameGacha(gacha, s);
                    if (n == -1) {
                        p.sendMessage(prefix + "§c§lガチャが存在しません");
                    }
                    if (n == -2) {
                        p.sendMessage(prefix + "§c§l変更予定名のガチャがすでに存在します");
                    }
                    if (n == -3) {
                        p.sendMessage(prefix + "§c§l内部的エラーが発生しました");
                    }
                    if (n == 0) {
                        gacha = s;
                    }
                    restartMenu(0,0);
                    return null;
                });
                break;
            }
            case 1: {
                //タイトル設定
                new LongTextInputAPI(p, "§5§lガチャのタイトルを入力してください", (player, s) -> {
                    game.getSettings().title = s;
                    pushSettings();
                    restartMenu(0,0);
                    return null;
                }, player -> {
                    restartMenu(0,0);
                    return null;
                });
                break;
            }
            case 2: {
                //アイコン設定
                new ItemStackSelectorAPI("§b§lのアイコンを選択してください", p, game.getSettings().icon, 1, (event, itemStack) -> {
                    itemStack = new SItemStack(itemStack).build();
                    if(itemStack == game.getSettings().icon){
                        p.sendMessage(prefix + "§c§l過去と同じアイコンは使用できません");
                        return "restart";
                    }
                    game.getSettings().icon = itemStack;
                    int i = api.printSettings(gacha, game.getSettings());
                    if(i == -1){
                        p.sendMessage(prefix + "§c§lガチャが存在しません");
                        restartMenu(0,0);
                        return null;
                    }
                    if(i == -2){
                        p.sendMessage(prefix + "§c§l内部的エラーが発生しました");
                        restartMenu(0,0);
                        return null;
                    }
                    if(i == 0){
                        api.reloadGacha(gacha);
                        restartMenu(0,0);
                        return null;
                    }
                    return null;
                }, event -> {
                    restartMenu(0,0);
                    return null;
                });
                break;
            }
        }
    }

    private void soundGategory(int id){
        switch (id){
            case 0:{
                //回転音設定
                GachaSound sound = game.getSettings().spinSound;
                new SoundSelectorAPI("§b§l回転音を設定してください", p, sound.getVolume(), sound.getPitch(), sound.getSound(), (event, gachaSound) -> {
                    game.getSettings().spinSound = gachaSound;
                    pushSettings();
                    restartMenu(1,0);
                    return null;
                }, event -> {
                    restartMenu(1, 0);
                    return null;
                });
            }
        }
    }

    private void miscCategory(int id){
        switch (id){
        }
    }
}
