package com.shojabon.man10gachav3.DataPackages;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sho on 2018/07/01.
 */
public class GachaTitleText {
    String mainText;
    String subText;
    int fadeIntTime;
    int time;
    int fadeoutTime;
    boolean nul = false;

    public GachaTitleText(String mainText, String subText, int fadeIntTime, int time, int fadeoutTime){
        this.mainText = mainText;
        this.subText = subText;
        this.fadeIntTime = fadeIntTime;
        this.time = time;
        this.fadeoutTime = fadeoutTime;
    }

    public GachaTitleText(){
        nul = true;
    }

    public boolean usable(){
        return !nul;
    }
    public void playTitleToPlayer(Player p){
        p.sendTitle(mainText, subText,fadeIntTime,time,fadeoutTime);
    }

    public void playTitleToServer(){
        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            p.sendTitle(mainText, subText, fadeIntTime,time,fadeoutTime);
        }
    }
    public void playTitleToServerExeptPlayer(Player player){
        for(Player p : Bukkit.getServer().getOnlinePlayers()){
            if(!p.equals(player)){
                p.sendTitle(mainText, subText, fadeIntTime,time,fadeoutTime);
            }
        }
    }

    public int getFadeinTime() {
        return fadeIntTime;
    }

    public int getFadeoutTime() {
        return fadeoutTime;
    }

    public int getTime() {
        return time;
    }

    public String getMainText() {
        if(!usable())return "なし";
        return mainText;
    }

    public String getSubText() {
        if(!usable()) return  "なし";
        return subText;
    }

    public void setFadeinTime(int fadeIntTime) {
        nul = false;
        this.fadeIntTime = fadeIntTime;
    }

    public void setFadeoutTime(int fadeoutTime) {
        nul = false;
        this.fadeoutTime = fadeoutTime;
    }

    public void setMainText(String mainText) {
        nul = false;
        this.mainText = mainText.replaceAll("&", "§");
    }

    public void setSubText(String subText) {
        nul = false;
        this.subText = subText.replaceAll("&", "§");
    }

    public void setTime(int time) {
        nul = false;
        this.time = time;
    }

    public Map<String, String> getStringData(){
        Map<String, String> out = new HashMap<>();
        out.put("mainText", String.valueOf(mainText));
        out.put("subText", String.valueOf(subText));
        out.put("fadeInTime", String.valueOf(fadeIntTime));
        out.put("time", String.valueOf(time));
        out.put("fadeOutTime", String.valueOf(fadeoutTime));
        return out;
    }
}
