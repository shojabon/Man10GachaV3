package com.shojabon.man10gachav3.GamePackages;


import com.shojabon.man10gachav3.DataPackages.*;
import com.shojabon.man10gachav3.ToolPackages.GachaVault;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import com.shojabon.man10gachav3.enums.GachaPaymentType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Man10GachaAPI {
    private Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10GachaV3");
    public static ConcurrentHashMap<String, GachaGame> gachaGameMap = new ConcurrentHashMap<>();
    static ConcurrentHashMap<Location, GachaSignData> signDataMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<UUID, String> inGamePlayerMap = new ConcurrentHashMap<>();
    private GachaVault vault;

    public Man10GachaAPI(){
        vault = new GachaVault();
    }

    public boolean ifGachaSign(Location l){
        return signDataMap.containsKey(l);
    }

    public String getSignGacha(Location l){
        if(!ifGachaSign(l)){
            return null;
        }
        return signDataMap.get(l).getGacha();
    }

    public void registerNewSign(GachaSignData data){
        createSignsFileIfNotExist();
        resetSignFile();
        signDataMap.put(data.getLocation(), data);
        wrightSignsFile();
    }

    public void deleteSign(Location location){
        createSignsFileIfNotExist();
        resetSignFile();
        signDataMap.remove(location);
        wrightSignsFile();
    }

    public void loadSignFile(){
        File f = new File(plugin.getDataFolder(), "signs.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        signDataMap.clear();
        for(String key : config.getKeys(false)){
            GachaSignData data = new GachaSignData(new Location(Bukkit.getWorld(config.getString(key + ".world")), config.getInt(key + ".x"), config.getInt(key + ".y"), config.getInt(key + ".z")), config.getString(key + ".gacha"));
            signDataMap.put(data.getLocation(), data);
        }
    }

    public void resetSignFile(){
        File f = new File(plugin.getDataFolder(), "signs.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        for(String key: config.getKeys(false)){
            config.set(key, null);
        }
        try {
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void wrightSignsFile(){
        createSignsFileIfNotExist();
        resetSignFile();
        File f = new File(plugin.getDataFolder(), "signs.yml");
        Configuration signConfig = YamlConfiguration.loadConfiguration(f);
        for(int i = 0;i < signDataMap.keySet().size();i++){
            GachaSignData data = signDataMap.get(signDataMap.keySet().toArray()[i]);
            signConfig.set(i + ".gacha", data.getGacha());
            signConfig.set(i + ".x", data.getLocation().getBlockX());
            signConfig.set(i + ".y", data.getLocation().getBlockY());
            signConfig.set(i + ".z", data.getLocation().getBlockZ());
            signConfig.set(i + ".world", data.getLocation().getWorld().getName());
        }
        try {
            ((YamlConfiguration) signConfig).save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void createSignsFileIfNotExist(){
        File f = new File(plugin.getDataFolder(), "signs.yml");
        if(f.exists()){
            return;
        }
        try {
            boolean b = f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean ifSignsFileExists(){
        return new File(plugin.getDataFolder(), "signs.yml").exists();
    }



    public void createGacha(GachaSettings gachaSettings, ArrayList<GachaPayment> payments, ArrayList<GachaItemStack> itemStacks){
        new Thread(() -> {
            File file = new File(plugin.getDataFolder(), "gacha" + File.separator + gachaSettings.name + ".yml");
            createFileIfNotExists(file);
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            Map<String, Object> settingsMap = gachaSettings.getStringData();
            printSettings(settingsMap, config);

            printPayment(payments, config);



            ArrayList<GachaItemStack> compressedItemStacks = compressItemStackList(itemStacks);


            StringBuilder out = new StringBuilder();
            for (GachaItemStack itemStack : itemStacks) {
                int index = getIndexOfItem(compressedItemStacks, itemStack);
                out.append(index).append("|");
            }

            if(out.length() != 0){
                config.set("storage", out.toString().substring(0, out.toString().length() -1));
            }else{
                config.set("storage", null);
            }
            for(int i = 0;i < compressedItemStacks.size();i++){
                Map<String, Object> item = compressedItemStacks.get(i).getStringData();
                printItemIndex(item, config, i);
            }

            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }

    private int getIndexOfItem(ArrayList<GachaItemStack> items, GachaItemStack item){
        for(int i =0; i < items.size(); i++){
            if(items.get(i).isTheSame(item)) return i;
        }
        return -1;
    }

    private void printItemIndex(Map<String, Object> itemData, FileConfiguration config, int id){
        for(String key: itemData.keySet()){
            switch (key) {
                case "teleport": {
                    Map<String, String> map = ((GachaTeleport) itemData.get(key)).getStringData();
                    config.set("index." + id + "." + key + ".world", map.get("world"));
                    config.set("index." + id + "." + key + ".x", map.get("x"));
                    config.set("index." + id + "." + key + ".y", map.get("y"));
                    config.set("index." + id + "." + key + ".z", map.get("z"));
                    config.set("index." + id + "." + key + ".pitch", map.get("pitch"));
                    config.set("index." + id + "." + key + ".yaw", map.get("yaw"));
                    break;
                }
                case "items": {
                    ArrayList<String> out = new ArrayList<>();
                    ArrayList<String> items = (ArrayList<String>) itemData.get(key);
                    for (String item : items) {
                        out.add(new SItemStack(item).toBase64());
                    }
                    config.set("index." + id + "." + key, out);
                    break;
                }
                case "playerTitle":{
                    GachaTitleText text = (GachaTitleText) itemData.get(key);
                    Map<String, String> map = text.getStringData();
                    config.set("index." + id + "." + key + ".mainText", map.get("mainText"));
                    config.set("index." + id + "." + key + ".subText", map.get("subText"));
                    config.set("index." + id + "." + key + ".fadeInTime", map.get("fadeInTime"));
                    config.set("index." + id + "." + key + ".time", map.get("time"));
                    config.set("index." + id + "." + key + ".fadeOutTime", map.get("fadeOutTime"));
                    break;
                }
                case "serverTitle": {
                    GachaTitleText text = (GachaTitleText) itemData.get(key);
                    Map<String, String> map = text.getStringData();
                    config.set("index." + id + "." + key + ".mainText", map.get("mainText"));
                    config.set("index." + id + "." + key + ".subText", map.get("subText"));
                    config.set("index." + id + "." + key + ".fadeInTime", map.get("fadeInTime"));
                    config.set("index." + id + "." + key + ".time", map.get("time"));
                    config.set("index." + id + "." + key + ".fadeOutTime", map.get("fadeOutTime"));
                    break;
                }
                case "broadcastSound":{
                    Map<String, String> map = ((GachaSound) itemData.get(key)).getStringData();
                    config.set("index." + id + "." + key + ".sound", map.get("sound"));
                    config.set("index." + id + "." + key + ".volume", map.get("volume"));
                    config.set("index." + id + "." + key + ".pitch", map.get("pitch"));
                    break;
                }
                case "playerSound": {
                    Map<String, String> map = ((GachaSound) itemData.get(key)).getStringData();
                    config.set("index." + id + "." + key + ".sound", map.get("sound"));
                    config.set("index." + id + "." + key + ".volume", map.get("volume"));
                    config.set("index." + id + "." + key + ".pitch", map.get("pitch"));
                    break;
                }
                default:
                    config.set("index." + id + "." + key, itemData.get(key));
                    break;
            }
        }
    }

    private void printSettings(Map<String, Object> settings, FileConfiguration config){
        for(String key : settings.keySet()) {
            switch (key) {
                case "sound":
                    Map<String, String> soundMap = ((GachaSound) settings.get(key)).getStringData();
                    config.set("settings." + key + ".sound", soundMap.get("sound"));
                    config.set("settings." + key + ".volume", soundMap.get("volume"));
                    config.set("settings." + key + ".pitch", soundMap.get("pitch"));
                    break;
                case "icon":
                    config.set("settings." + key, new SItemStack((ItemStack) settings.get(key)).setAmount(1).toBase64());
                    break;
                default:
                    config.set("settings." + key, settings.get(key));
                    break;
            }
        }
    }

    public int printSettings(String gacha, GachaSettings settings){
        File file = new File(plugin.getDataFolder(), "gacha" + File.separator + gacha + ".yml");
        if(!file.exists()){
            return -1;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("settings", null);
        printSettings(settings.getStringData(), config);
        try {
            config.save(file);
        } catch (IOException e) {
            return -2;
        }
        return 0;
    }

    public void createFileIfNotExists(File file){
        if(!file.exists()){
            try {
                boolean b = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void createFolderIfNotExists(File file){
        boolean b = file.mkdirs();
    }

    private void printPayment(ArrayList<GachaPayment> payments, FileConfiguration config){
        for(int i = 0;i < payments.size();i++){
            Map<String, String> data = payments.get(i).getStringData();
            for(String key: data.keySet()){
                config.set("payments." + i + "." + key, data.get(key));
            }
        }
    }

    public List<String> getGachasInDirectory(){
        List<String> name = new ArrayList<>();
        File folder = new File(plugin.getDataFolder(), "gacha");
        File[] listOfFiles = folder.listFiles();
        for (File listOfFile : listOfFiles) {
            name.add(deleteExtention(listOfFile.getName()));
        }
        return name;
    }

    private String deleteExtention(String fileName){
        String fname = fileName;
        int pos = fname.lastIndexOf(".");
        if (pos > 0) {
            fname = fname.substring(0, pos);
        }
        return fname;
    }

    public GachaGame getGacha(String name){
        if(!gachaGameMap.containsKey(name)){
            GachaGame game = new GachaGame(name, (JavaPlugin) plugin);
            gachaGameMap.put(name, game);
        }
        return gachaGameMap.get(name);
    }

    public void reloadGacha(String gacha){
        gachaGameMap.remove(gacha);
        getGacha(gacha);
    }

    public void clearSavedGachas(){
        gachaGameMap.clear();
    }

    public int renameGacha(String oldGacha, String newGacha){
        File oldFile = new File(Bukkit.getPluginManager().getPlugin("Man10GachaV3").getDataFolder(), "gacha" + File.separator + oldGacha + ".yml");
        if(!oldFile.exists()){
            return -1;
        }
        File newFile = new File(Bukkit.getPluginManager().getPlugin("Man10GachaV3").getDataFolder(), "gacha" + File.separator + newGacha + ".yml");
        if(newFile.exists()){
            return -2;
        }
        boolean success = oldFile.renameTo(newFile);
        if(!success){
            return -3;
        }
        gachaGameMap.remove(oldGacha);
        getGacha(newGacha);
        return 0;
    }

    public void loadAllGachas(){
        new Thread(() -> {
            List<String> names = this.getGachasInDirectory();
            Bukkit.broadcastMessage(String.valueOf(names.size()));
            for(String name : names){
                Bukkit.broadcastMessage(name);
                this.getGacha(name);
                Bukkit.broadcastMessage(name);
            }
        }).start();
    }


    public boolean ifGachaExists(String name){
        if(gachaGameMap.containsKey(name)){
            return true;
        }
        File file = new File(plugin.getDataFolder(), "gacha" + File.separator + name + ".yml");
        return file.exists();
    }

    public void deleteGacha(GachaGame game){
        if(ifGachaExists(game.getSettings().name)){
            File file = new File(plugin.getDataFolder(), "gacha" + File.separator + game.getSettings().name + ".yml");
            file.delete();
            gachaGameMap.remove(game.getSettings().name);

        }
    }


    public void deleteGachaFile(GachaGame game){
        if(ifGachaExists(game.getSettings().name)){
            File file = new File(plugin.getDataFolder(), "gacha" + File.separator + game.getSettings().name + ".yml");
            file.delete();

        }
    }

    public void createGacha(GachaGame game){
        createGacha(game.getSettings(), game.getPayments(), game.renderStorageItem());
    }

    public ArrayList<GachaItemStack> compressItemStackList(ArrayList<GachaItemStack> arr){
        ArrayList<GachaItemStack> out = new ArrayList<>();
        for(GachaItemStack item : arr){
            boolean same = false;
            for(GachaItemStack outItem: out){
                if(item.isTheSame(outItem)) {
                    same = true;
                    break;
                }
            }
            if(!same) out.add(item);
        }
        return out;
    }

    public void updateGacha(GachaGame game){
        if(!ifGachaExists(game.getSettings().name)){
            createGacha(game);
            return;
        }
        new Thread(() -> {
            deleteGachaFile(game);
            createGacha(game);
            getGacha(game.getSettings().name);
        }).start();
    }


    public boolean ifPlayerHasEnoughForPayment(Player p, GachaGame game){
        if(game == null){
            return false;
        }
        for(GachaPayment payment: game.getPayments()){
            if(payment.getType() == GachaPaymentType.VAULT){
                if(vault.getBalance(p.getUniqueId()) < payment.getVaultPayment().getValue()){
                    return false;
                }
            }
            if(payment.getType() == GachaPaymentType.ITEM){
                if(!new SItemStack(p.getInventory().getItemInMainHand()).setAmount(1).build().isSimilar(new SItemStack(payment.getItemStackPayment().getItemStack()).setAmount(1).build())){
                    return false;
                }else{
                    if(p.getInventory().getItemInMainHand().getAmount() < payment.getItemStackPayment().getAmount()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean takePayment(Player p, GachaGame game){
        if(game == null){
            return false;
        }
        if(!ifPlayerHasEnoughForPayment(p, game)){
            return false;
        }
        ArrayList<GachaPayment> payments = game.getPayments();
        for(GachaPayment payment : payments) {
            if (payment.getType() == GachaPaymentType.VAULT) {
                vault.takeMoney(p.getUniqueId(), payment.getVaultPayment().getValue());
            } else if (payment.getType() == GachaPaymentType.ITEM) {
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - payment.getItemStackPayment().getAmount());
            }
        }
        return true;
    }

    public List<String> getPayMessage(Player p, GachaGame game){
        if(game == null){
            return null;
        }
        List<String> messages = new ArrayList<>();
        ArrayList<GachaPayment> payments = game.getPayments();
        for(GachaPayment payment : payments){
            if(payment.getType() == GachaPaymentType.VAULT){
                p.sendMessage("§a§l現金§7§l|§a§l" + NumberFormat.getNumberInstance(Locale.US).format(payment.getVaultPayment().getValue()) + "円");
            }
            if(payment.getType() == GachaPaymentType.ITEM){
                String name = payment.getItemStackPayment().getItemStack().getItemMeta().getDisplayName();
                if(name == null) name = payment.getItemStackPayment().getItemStack().getType().name();
                p.sendMessage("§a§lアイテム§7§l|§a§l『" + name + "§a§l』を " + NumberFormat.getNumberInstance(Locale.US).format(payment.getItemStackPayment().getAmount()) +"個");
            }
        }
        return messages;
    }

    public List<String> getLackingPaymentMessage(Player p, GachaGame game){
        if(game == null){
            return null;
        }
        List<String> messages = new ArrayList<>();
        ArrayList<GachaPayment> payments = game.getPayments();
        for(GachaPayment payment : payments){
            if(payment.getType() == GachaPaymentType.VAULT){
                if(vault.hasEnough(p.getUniqueId(), payment.getVaultPayment().getValue())) {
                    messages.add("§2§l✔ §7§l| §a§l現金 " + payment.getVaultPayment().getValue() + "円");
                }else{
                    messages.add("§4§l✖ §7§l| §c§l現金 " + payment.getVaultPayment().getValue() + "円");
                }
            }
            if(payment.getType() == GachaPaymentType.ITEM){
                int amountInHand = p.getInventory().getItemInMainHand().getAmount();
                String name = payment.getItemStackPayment().getItemStack().getType().name();
                if(payment.getItemStackPayment().getItemStack().getItemMeta().getDisplayName() != null) name = payment.getItemStackPayment().getItemStack().getItemMeta().getDisplayName();
                if(!new SItemStack(p.getInventory().getItemInMainHand()).setAmount(1).build().isSimilar(new SItemStack(payment.getItemStackPayment().getItemStack()).setAmount(1).build())){
                    messages.add("§4§l✖ §7§l| §4§lアイテム 『" + name + "§4§l』が" + payment.getItemStackPayment().getAmount() + "個");
                }else{
                    if(p.getInventory().getItemInMainHand().getAmount() < payment.getItemStackPayment().getAmount()){
                        messages.add("§4§l✖ §7§l| §4§lアイテム 『" + name + "§4§l』が" + payment.getItemStackPayment().getAmount() + "個");
                    }else{
                        messages.add("§2§l✔ §7§l| §a§lアイテム 『" + name + "§a§l』が" + payment.getItemStackPayment().getAmount() + "個");
                    }
                }
            }
        }
        return messages;
    }

    public String getPaymentSignStrig(String gacha){
        int vault = getVaultAmount(getGacha(gacha));
        GachaPayment itemPayment = getItemStackPayment(getGacha(gacha));
        if(vault == 0 && itemPayment == null) return "Free";
        if(vault != 0 && itemPayment == null) return String.valueOf(vault);
        if(vault == 0 && itemPayment != null) return "Ticket";
        if(vault != 0 && itemPayment != null) return vault + "&T";
     return "null";
    }

    private GachaPayment getItemStackPayment(GachaGame game){
        for(GachaPayment payment : game.getPayments()){
            if(payment.getType() == GachaPaymentType.ITEM){
                return payment;
            }
        }
        return null;
    }

    private int getVaultAmount(GachaGame game){
        for(GachaPayment payment : game.getPayments()){
            if(payment.getType() == GachaPaymentType.VAULT){
                return (int) payment.getVaultPayment().getValue();
            }
        }
        return 0;
    }




}