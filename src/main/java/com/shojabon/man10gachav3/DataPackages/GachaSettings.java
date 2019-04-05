package com.shojabon.man10gachav3.DataPackages;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class GachaSettings {
    public String name;
    public String title = "Gacha";
    public GachaSound spinSound = new GachaSound(Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
    public ItemStack icon = new ItemStack(Material.DIAMOND);

    public GachaSettings(Map<String, Object> settings){
        for(String key: settings.keySet()){
            switch (key){
                case "name":
                    this.name = String.valueOf(settings.get(key));
                    break;
                case "title":
                    this.title = String.valueOf(settings.get(key));
                    break;
                case "sound":
                    this.spinSound = (GachaSound) settings.get(key);
                    break;
                case "icon":
                    this.icon = (ItemStack) settings.get(key);
                    break;
            }
        }
    }

    public GachaSettings(String name,
                         String title,
                         GachaSound spinSound,
                         ItemStack icon){
        this.name = name;
        this.title = title;
        this.spinSound = spinSound;
        this.icon = icon;
    }

    public GachaSettings(String name){
        this.name = name;
    }

    public Map<String, Object> getStringData() {
        Map<String, Object> map = new HashMap<>();
        map.put("title", this.title);
        if (!spinSound.getStringData().equals(new GachaSound(Sound.BLOCK_DISPENSER_DISPENSE, 1, 1).getStringData())) {
            map.put("sound", this.spinSound);
        }
        if (icon != null) {
            if(!icon.equals(new ItemStack(Material.DIAMOND))){
                map.put("icon", icon);
            }
        }
        return map;
    }

}
