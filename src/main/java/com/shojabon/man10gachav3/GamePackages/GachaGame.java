package com.shojabon.man10gachav3.GamePackages;

import com.shojabon.man10gachav3.DataPackages.GachaItemStack;
import com.shojabon.man10gachav3.DataPackages.GachaPayment;
import com.shojabon.man10gachav3.DataPackages.GachaPaymentData.GachaItemStackPayment;
import com.shojabon.man10gachav3.DataPackages.GachaPaymentData.GachaVaultPayment;
import com.shojabon.man10gachav3.DataPackages.GachaSettings;
import com.shojabon.man10gachav3.GameDataPackages.GachaSound;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import com.shojabon.man10gachav3.enums.GachaPaymentType;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GachaGame {

    private GachaSettings settings;
    private ArrayList<GachaPayment> payments;
    private Listener listener = new Listener();
    private ArrayList<GachaItemStack> itemIndex;
    private ArrayList<GachaItemStack> storage = new ArrayList<>();
    private HashMap<UUID, Inventory> inventoryMap = new HashMap<>();
    private HashMap<Integer, Integer> storageAmount = new HashMap<>();
    private JavaPlugin plugin;

    public GachaGame(String name, JavaPlugin plugin){
        this.plugin = plugin;
        File file = new File(Bukkit.getPluginManager().getPlugin("Man10GachaV3").getDataFolder(), "gacha" + File.separator + name + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        this. settings = new GachaSettings(getSettingsMap(config));
        this.settings.name = name;
        this.itemIndex = getItemStackMap(config);
        this.payments = getPaymentList(config);
        Bukkit.getPluginManager().registerEvents(listener, this.plugin);
        getItemStacks(config);
    }



    private Map<String, Object> getSettingsMap(FileConfiguration config){
        Map<String, Object> map = new HashMap<>();
        ConfigurationSection configurationSection = config.getConfigurationSection("settings");
        for(String keys: configurationSection.getKeys(false)){
            switch (keys) {
                case "sound":
                    Sound sound = Sound.valueOf(config.getString("settings.sound.sound"));
                    map.put("sound", new GachaSound(sound, Float.valueOf(config.getString("settings.sound.volume")), Float.valueOf(config.getString("settings.sound.pitch"))));
                    break;
                case "icon":
                    map.put("icon", new SItemStack(config.getString("settings.icon")).build());
                    break;
                default:
                    map.put(keys, config.get("settings." + keys));
                    break;
            }
        }
        return map;
    }

    private ArrayList<GachaPayment> getPaymentList(FileConfiguration config){
        ArrayList<GachaPayment> payments = new ArrayList<>();
        ConfigurationSection configurationSection = config.getConfigurationSection("payments");
        for(String numKeys: configurationSection.getKeys(false)){
            GachaPaymentType paymentType = GachaPaymentType.valueOf(config.getString("payments." + numKeys + ".type"));
            switch (paymentType){
                case VAULT:
                    double amount = Double.parseDouble(config.getString("payments." + numKeys + ".amount"));
                    payments.add(new GachaPayment(new GachaVaultPayment(amount)));
                    break;
                case ITEM:
                    payments.add(new GachaPayment(new GachaItemStackPayment(new SItemStack(config.getString("payments." + numKeys + ".item")).build(), config.getInt("amount"))));
                    break;
            }
        }
        return payments;
    }

    private ArrayList<GachaItemStack> getItemStackMap(FileConfiguration config){
        ConfigurationSection configurationSection = config.getConfigurationSection("index");
        ArrayList<GachaItemStack> index = new ArrayList<>();
        for(String numKey : configurationSection.getKeys(false)){
            Map<String, Object> map = new HashMap<>();
            for(String key: config.getConfigurationSection("index." + numKey).getKeys(false)){
                switch (key){
                    case "item":
                        map.put(key, new SItemStack(config.getString("index." + numKey + "." + key)).toBase64());
                        break;
                    case "broadcastMessage":
                        map.put(key, config.getStringList("index." + numKey + "." + key));
                        break;
                    case "playerMessage":
                        map.put(key, config.getStringList("index." + numKey + "." + key));
                        break;
                    case "broadcastSound":
                        map.put(key, new GachaSound(
                                Sound.valueOf(config.getString("index." + numKey + "." + key + ".sound")),
                                Float.valueOf(config.getString("index." + numKey + "." + key + ".volume")),
                                Float.valueOf(config.getString("index." + numKey + "." + key + ".pitch"))
                        ));
                        break;
                    case "playerSound":
                        GachaSound sound = new GachaSound(
                                Sound.valueOf(config.getString("index." + numKey + "." + key + ".sound")),
                                Float.valueOf(config.getString("index." + numKey + "." + key + ".volume")),
                                Float.valueOf(config.getString("index." + numKey + "." + key + ".pitch")));
                        map.put(key, sound);
                        break;
                    default:
                        map.put(key,  config.get("index." + numKey + "." + key));
                        break;
                }
            }
            index.add(new GachaItemStack(map));
        }
        return index;
    }

    private void getItemStacks(FileConfiguration config){
        String items = config.getString("storage");
        String[] split = items.split("\\|");
        for(String s : split){
            if(storageAmount.containsKey(Integer.valueOf(s))){
                storageAmount.put(Integer.valueOf(s), storageAmount.get(Integer.valueOf(s))+1);
            }else{
                storageAmount.put(Integer.valueOf(s), 1);
            }
        }
        storage = renderStorage();
    }

    public ArrayList<GachaItemStack> getStorage() {
        return storage;
    }
    private class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(!inventoryMap.containsKey(e.getWhoClicked().getUniqueId())){
                return;
            }
            e.setCancelled(true);
        }

        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(!inventoryMap.containsKey(e.getPlayer().getUniqueId())) {
                inventoryMap.remove(e.getPlayer().getUniqueId());
            }
        }
    }

    public GachaSettings getSettings(){
        return settings;
    }

    public ArrayList<GachaItemStack> getItemIndex() {
        return itemIndex;
    }

    public HashMap<Integer, Integer> getStorageAmount() {
        return storageAmount;
    }

    public ArrayList<GachaPayment> getPayments() {
        return payments;
    }

    public ArrayList<GachaItemStack> renderStorage(){
        ArrayList<GachaItemStack> items = new ArrayList<>();
        for(int i =0; i < storageAmount.keySet().size(); i++){
            int key = (int) storageAmount.keySet().toArray()[i];
            for(int ii =0; ii < storageAmount.get(key); ii++){
                items.add(itemIndex.get(key));
            }

        }
        return items;
    }

    public void setStorageAmound(int index, int amount){
        storageAmount.put(index, amount);
        storage = renderStorage();
    }
}
