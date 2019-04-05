package com.shojabon.man10gachav3.Commands;

import com.shojabon.man10gachav3.DataPackages.GachaSettings;
import com.shojabon.man10gachav3.GameDataPackages.Menu.SettingsMenu.GachaSettingsSelectionMenu;
import com.shojabon.man10gachav3.Man10GachaV3;
import com.shojabon.man10gachav3.ToolPackages.GachaVault;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Man10GachaV3Command implements CommandExecutor {
    Man10GachaV3 plugin;
    public Man10GachaV3Command(Man10GachaV3 plugin){
        this.plugin = plugin;
    }

    public void help(CommandSender sender){
        sender.sendMessage("§c§l===========§6[§aMg§fac§dha§5V3§6]§f§c§l===========");
        sender.sendMessage("§b§l/mgachav3 setting §6ガチャ設定画面");
        sender.sendMessage("§b§l/mgachav3 create <name> §6ガチャを作成");
        sender.sendMessage("§b§l/mgachav3 delete <name> §6ガチャを削除");
        sender.sendMessage("§b§l/mgachav3 creload §6設定をリロード");
        sender.sendMessage("§b§l/mgachav3 reload <name> §6ガチャをリロード");
        sender.sendMessage("§b§l/mgachav3 reloadall §6すべてのガチャをリロード");
        sender.sendMessage("§c§l==============================");
        sender.sendMessage("§6§lCreated By Sho0");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1){
            if(args[0].equalsIgnoreCase("setting")){
                if(!sender.hasPermission("mgachav3.command.setting")){
                    sender.sendMessage(Man10GachaV3.prefix + "§4§lあなたには権限がありません");
                    return false;
                }
                new GachaSettingsSelectionMenu(((Player)sender));
                return false;
            }
            if(args[0].equalsIgnoreCase("reloadall")){
                if(!sender.hasPermission("mgachav3.command.reloadall")){
                    sender.sendMessage(Man10GachaV3.prefix + "§4§lあなたには権限がありません");
                    return false;
                }
                new Thread(() -> {
                    sender.sendMessage(Man10GachaV3.prefix + "§a§lリロードが開始しました");
                    plugin.api.clearSavedGachas();
                    List<String> gachas = plugin.api.getGachasInDirectory();
                    for(String gach : gachas){
                        plugin.api.reloadGacha(gach);
                    }
                    sender.sendMessage(Man10GachaV3.prefix + "§a§lリロードが完了しました");
                }).start();
                return false;
            }
            if(args[0].equalsIgnoreCase("creload")){
                if(!sender.hasPermission("mgachav3.command.creload")){
                    sender.sendMessage(Man10GachaV3.prefix + "§4§lあなたには権限がありません");
                    return false;
                }
                plugin.reloadConfig();
                plugin.pluginConfig = plugin.getConfig();
                plugin.databaseBootSequence();
                plugin.createTables();
                plugin.prefix = plugin.pluginConfig.getString("prefix").replace("&", "§");
                plugin.api.loadSignFile();
                plugin.vault = new GachaVault();
                sender.sendMessage(Man10GachaV3.prefix + "§a§lコンフィグをリロードしました");
                return false;
            }
        }else if(args.length == 2){
            if(args[0].equalsIgnoreCase("create")){
                if(!sender.hasPermission("mgachav3.command.create")){
                    sender.sendMessage(Man10GachaV3.prefix + "§4§lあなたには権限がありません");
                    return false;
                }
                if(plugin.api.ifGachaExists(args[1])){
                    sender.sendMessage(Man10GachaV3.prefix + "§4§lすでにガチャが存在します");
                    return false;
                }
                plugin.api.createGacha(new GachaSettings(args[1]), new ArrayList<>(), new ArrayList<>());
                sender.sendMessage(Man10GachaV3.prefix + "§a§lガチャが作成されました");
                return false;
            }
            if(args[0].equalsIgnoreCase("delete")){
                if(!sender.hasPermission("mgachav3.command.delete")){
                    sender.sendMessage(Man10GachaV3.prefix + "§4§lあなたには権限がありません");
                    return false;
                }
                if(!plugin.api.ifGachaExists(args[1])){
                    sender.sendMessage(Man10GachaV3.prefix + "§4§lすでにガチャが存在しません");
                    return false;
                }
                plugin.api.deleteGacha(plugin.api.getGacha(args[1]));
                sender.sendMessage(Man10GachaV3.prefix + "§a§lガチャが消去されました");
                return false;
            }
            if(args[0].equalsIgnoreCase("reload")){
                if(!sender.hasPermission("mgachav3.command.reload")){
                    sender.sendMessage(Man10GachaV3.prefix + "§4§lあなたには権限がありません");
                    return false;
                }
                if(!plugin.api.ifGachaExists(args[1])){
                    sender.sendMessage(Man10GachaV3.prefix + "§4§lすでにガチャが存在しません");
                    return false;
                }
                plugin.api.reloadGacha(args[1]);
                sender.sendMessage(Man10GachaV3.prefix + "§a§l" + args[1] + "をリロードしました");
                return false;
            }
        }else{
            help(sender);
        }
        return false;
    }
}
