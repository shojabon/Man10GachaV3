package com.shojabon.man10gachav3.DataPackages;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class GachaSignData {
    private Location location;
    private String gacha;

    public GachaSignData(Location l, String gacha){
        this.location = l;
        this.gacha = gacha;
    }

    public boolean isPlaced(){
        Block b = location.getBlock();
        return b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN;
    }

    public void deleteSign(){
        if(isPlaced()){
            location.getBlock().setType(Material.AIR);
        }
    }

    public String getGacha() {
        return gacha;
    }

    public Location getLocation() {
        return location;
    }

    public void setGacha(String gacha) {
        this.gacha = gacha;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
