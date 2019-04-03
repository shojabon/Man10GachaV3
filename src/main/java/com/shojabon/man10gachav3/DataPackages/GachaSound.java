package com.shojabon.man10gachav3.DataPackages;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sho on 2018/07/09.
 */
public class GachaSound {
    Sound sound;
    float volume;
    float pitch;

    public GachaSound(Sound sound, float volume, float pitch){
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    public GachaSound(){
        sound = null;
        volume = 0;
        pitch = 0;
    }


    public boolean usable(){
        return sound != null;
    }

    public void playSoundToPlayer(Player player){
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    public void playSoundToServer(){
        for (Player p : Bukkit.getServer().getOnlinePlayers()){
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    public void playSoundToServerExeptPlayer(Player player){
        for(Player pp: Bukkit.getServer().getOnlinePlayers()){
            if(!pp.equals(player)){
                pp.playSound(pp.getLocation(), sound, volume, pitch);
            }
        }
    }

    public float getPitch() {
        return pitch;
    }

    public float getVolume() {
        return volume;
    }

    public Sound getSound() {
        return sound;
    }


    public Map<String, String> getStringData(){
        Map<String, String> out = new HashMap<>();
        if(sound == null){
            out.put("sound", "なし");
        }else{
            out.put("sound", sound.name());
        }
        out.put("volume", String.valueOf(volume));
        out.put("pitch", String.valueOf(pitch));
        return out;
    }
}
