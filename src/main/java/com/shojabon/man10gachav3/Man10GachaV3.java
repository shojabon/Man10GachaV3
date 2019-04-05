package com.shojabon.man10gachav3;

import com.shojabon.man10gachav3.Commands.Man10GachaV3Command;
import com.shojabon.man10gachav3.DataPackages.GachaItemStack;
import com.shojabon.man10gachav3.DataPackages.GachaPayment;
import com.shojabon.man10gachav3.DataPackages.GachaPaymentData.GachaVaultPayment;
import com.shojabon.man10gachav3.DataPackages.GachaSettings;
import com.shojabon.man10gachav3.DataPackages.GachaSound;
import com.shojabon.man10gachav3.GameDataPackages.Menu.SettingsMenu.GachaSettingsSelectionMenu;
import com.shojabon.man10gachav3.GamePackages.Man10GachaAPI;
import com.shojabon.man10gachav3.ToolPackages.DatabaseConnector;
import com.shojabon.man10gachav3.ToolPackages.GachaVault;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import com.shojabon.man10gachav3.events.SignClickEvent;
import com.shojabon.man10gachav3.events.SignDestroyEvent;
import com.shojabon.man10gachav3.events.SignUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;

public final class Man10GachaV3 extends JavaPlugin implements Listener {

    public Man10GachaAPI api = null;
    public GachaVault vault = null;
    public FileConfiguration pluginConfig = null;

    public static String prefix = "§6[§aMg§fac§dha§5V3§6]§f";
    public static DatabaseConnector mysql = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        api = new Man10GachaAPI();
        pluginConfig = getConfig();
        databaseBootSequence();
        createTables();
        prefix = pluginConfig.getString("prefix").replace("&", "§");
        api.loadSignFile();
        vault = new GachaVault();
        getCommand("mgachav3").setExecutor(new Man10GachaV3Command(this));
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginManager().registerEvents(new SignUpdateEvent(this),this);
        Bukkit.getServer().getPluginManager().registerEvents(new SignDestroyEvent(this),this);
        Bukkit.getServer().getPluginManager().registerEvents(new SignClickEvent(this),this);
        new BukkitRunnable() {
            @Override
            public void run() {
                api.loadAllGachas();
            }
        }.runTaskLater(this, 3);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            p.closeInventory();
        }
    }

    public void a(){
        Man10GachaAPI api = new Man10GachaAPI();
        GachaSettings settings = new GachaSettings("test", "a", new GachaSound(Sound.BLOCK_DISPENSER_DISPENSE, 1, 1), new SItemStack(Material.STONE).build());
        ArrayList<GachaPayment> payments = new ArrayList<>();
        payments.add(new GachaPayment(new GachaVaultPayment(100)));
        ArrayList<GachaItemStack> items = new ArrayList<>();
        items.add(new GachaItemStack(new SItemStack(Material.STONE).setAmount(1).build()));
        items.add(new GachaItemStack(new SItemStack(Material.DIAMOND).setAmount(2).build()));
        items.add(new GachaItemStack(new SItemStack(Material.GOLD_AXE).setAmount(3).build()));
        items.add(new GachaItemStack(new SItemStack(Material.ANVIL).setAmount(4).build()));
        items.add(new GachaItemStack(new SItemStack(Material.TNT).setAmount(5).build()));
        items.add(new GachaItemStack(new SItemStack(Material.WATCH).setAmount(6).build()));
        items.add(new GachaItemStack(new SItemStack(Material.FENCE_GATE).setAmount(7).build()));
        items.add(new GachaItemStack(new SItemStack(Material.GLASS).setAmount(8).build()));
        items.add(new GachaItemStack(new SItemStack(Material.TNT).setAmount(9).build()));
        api.createGacha(settings, payments, items);
    }

    public void databaseBootSequence(){
        if(pluginConfig.getString("database_settings.type").equalsIgnoreCase("mysql")){
            mysql = new DatabaseConnector(pluginConfig.getString("database_settings.host"),
                    pluginConfig.getInt("database_settings.port"),
                    pluginConfig.getString("database_settings.username"),
                    pluginConfig.getString("database_settings.password"),
                    pluginConfig.getString("database_settings.database"));
        }else if(pluginConfig.getString("database_settings.type").equalsIgnoreCase("sqlite")){
            String databaseName = pluginConfig.getString("database_settings.database");
            if(databaseName == null || databaseName.equals("")) databaseName = "database.db";
            if(!databaseName.contains(".db")) databaseName = databaseName + ".db";
            mysql = new DatabaseConnector(new File(getDataFolder(), databaseName));
        }else{
            createLog("database setting is none");
            mysql = null;
            return;
        }
        createLog("connecting to database..." + pluginConfig.getString("database_settings.type"));
        if(mysql.connectable()){
            createLog("connected to database");
            return;
        }
        createLog("failed to connect to database");
        mysql = null;
    }
    public void createTables(){
        String logTable = "CREATE TABLE IF NOT EXISTS `gachav3_history` (\n" +
                "\t`id` INT(11) NOT NULL AUTO_INCREMENT,\n" +
                "\t`gacha_name` VARCHAR(128) NULL DEFAULT NULL,\n" +
                "\t`player_name` VARCHAR(128) NULL DEFAULT NULL,\n" +
                "\t`player_uuid` VARCHAR(128) NULL DEFAULT NULL,\n" +
                "\t`payment_item_data` LONGTEXT NULL,\n" +
                "\t`payment_item_name` VARCHAR(512) NULL DEFAULT NULL,\n" +
                "\t`payment_item_amount` INT(11) NULL DEFAULT NULL,\n" +
                "\t`payment_vault_value` BIGINT(20) NULL DEFAULT NULL,\n" +
                "\t`win_item_data` LONGTEXT NULL,\n" +
                "\t`win_item_name` VARCHAR(128) NULL DEFAULT NULL,\n" +
                "\t`win_itme_amount` INT(11) NULL DEFAULT NULL,\n" +
                "\t`win_gacha_item_data` LONGTEXT NULL,\n" +
                "\t`location` VARCHAR(512) NULL DEFAULT NULL,\n" +
                "\t`spin_time` BIGINT(20) NULL DEFAULT NULL,\n" +
                "\t`spin_date_time` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ")\n" +
                "COLLATE='utf8_general_ci'\n" +
                "ENGINE=InnoDB\n" +
                ";\n";

        mysql.execute(logTable);
    }
    public void createLog(String message){
        Bukkit.getLogger().info("[Mgachav3]" + message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("gachav3")){
            Player p = ((Player)sender);
            if(args.length == 1){
                if(args[0].equalsIgnoreCase("create")){
                    a();
                    return false;
                }
                if(args[0].equalsIgnoreCase("play")){
                    api.getGacha("test").play(p);
                    return false;
                }
                if(args[0].equalsIgnoreCase("test")){
                }
            }
            //a();

            new GachaSettingsSelectionMenu(((Player)sender));
        }
        return false;
    }
}
