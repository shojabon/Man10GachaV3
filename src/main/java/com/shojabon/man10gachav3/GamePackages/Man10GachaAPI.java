package com.shojabon.man10gachav3.GamePackages;


import com.shojabon.man10gachav3.DataPackages.GachaItemStack;
import com.shojabon.man10gachav3.DataPackages.GachaPayment;
import com.shojabon.man10gachav3.DataPackages.GachaSettings;
import com.shojabon.man10gachav3.GameDataPackages.GachaSound;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Man10GachaAPI {
    private Plugin plugin = Bukkit.getPluginManager().getPlugin("Man10GachaV3");
    public static HashMap<String, GachaGame> gachaGameMap = new HashMap<>();

    public Man10GachaAPI(){

    }

    public void createGacha(GachaSettings gachaSettings, ArrayList<GachaPayment> payments, ArrayList<GachaItemStack> itemStacks){
        File file = new File(plugin.getDataFolder(), "gacha" + File.separator + gachaSettings.name + ".yml");
        createFileIfNotExists(file);
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        Map<String, Object> settingsMap = gachaSettings.getStringData();
        printSettings(settingsMap, config);

        printPayment(payments, config);


        HashMap<String, Integer> compressedItems = new HashMap<>();
        HashMap<Integer, GachaItemStack> compressedGachaItem = new HashMap<>();

        int count = 0;
        for(GachaItemStack item : itemStacks){
            if(!compressedItems.containsKey(new SItemStack(item.item).toBase64())){
                compressedItems.put(new SItemStack(item.item).toBase64(), count);
                compressedGachaItem.put(count, item);
                count += 1;
            }
        }

        StringBuilder out = new StringBuilder();
        for (GachaItemStack itemStack : itemStacks) {
            out.append(compressedItems.get(new SItemStack(itemStack.item).toBase64())).append(",").append(itemStack.amount).append("|");
        }

        if(out.length() != 0){
            config.set("storage", out.toString().substring(0, out.toString().length() -1));
        }else{
            config.set("storage", null);
        }
        for(int i = 0;i < compressedGachaItem.size();i++){
            Map<String, Object> item = compressedGachaItem.get(i).getStringData();
            printItemIndex(item, config, i);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void printItemIndex(Map<String, Object> itemData, FileConfiguration config, int id){
        for(String key: itemData.keySet()){
            switch (key) {
                case "commands":
                case "broadcastMessage":
                case "playerMessage":
                case "broadcastSound":
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

    private void createFileIfNotExists(File file){
        if(!file.exists()){
            try {
                boolean b = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        List<String> names = this.getGachasInDirectory();
        for(String name : names){
            this.getGacha(name);
        }
    }



}