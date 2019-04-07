package com.shojabon.man10gachav3.DataPackages;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sho on 2018/07/09.
 */
public class GachaTeleport {
    public Location location;
    public boolean nul = false;
    public GachaTeleport(String world, double x, double y, double z, float pitch, float yaw){
        this.location = new Location(Bukkit.getWorld(world), x, y, z, pitch, yaw);
    }

    public GachaTeleport(Location location){
        this.location = location;
    }

    public GachaTeleport(){
        nul = true;
    }

    public boolean useable(){
        return !nul;
    }

    public void teleportPlayerToLocation(Player player){
        player.teleport(location);
    }

    public Map<String, String> getStringData(){
        if(!useable()) {
            Map<String, String> out = new HashMap<>();
            out.put("world", "なし");
            out.put("x", "なし");
            out.put("y", "なし");
            out.put("z", "なし");
            out.put("pitch", "なし");
            out.put("yaw", "なし");
            return out;
        }
        Map<String, String> out = new HashMap<>();
        out.put("world", String.valueOf(location.getWorld().getName()));
        out.put("x", String.valueOf(location.getX()));
        out.put("y", String.valueOf(location.getY()));
        out.put("z", String.valueOf(location.getZ()));
        out.put("pitch", String.valueOf(location.getPitch()));
        out.put("yaw", String.valueOf(location.getYaw()));
        return out;
    }


}
