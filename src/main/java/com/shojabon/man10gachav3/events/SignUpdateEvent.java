package com.shojabon.man10gachav3.events;

import com.shojabon.man10gachav3.DataPackages.GachaSignData;
import com.shojabon.man10gachav3.Man10GachaV3;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignUpdateEvent implements Listener {
    private Man10GachaV3 plugin;

    public SignUpdateEvent(Man10GachaV3 plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignUpdate(SignChangeEvent e){
        if(!e.getLine(0).equalsIgnoreCase("mgachav3")) return;
        if(!e.getPlayer().hasPermission("mgachav3.sign.create")) {
            e.getPlayer().sendMessage(plugin.prefix + "§4§lあなたには権限がありません");
            e.setCancelled(true);
            e.getBlock().breakNaturally();
            return;
        }
        if(!plugin.api.ifGachaExists(e.getLine(1))){
            e.getPlayer().sendMessage(plugin.prefix + "§c§lガチャが存在しません");
            e.setCancelled(true);
            e.getBlock().breakNaturally();
            return;
        }
        String gachaname = e.getLine(1);
        //register process
        plugin.api.registerNewSign(new GachaSignData(e.getBlock().getLocation(), e.getLine(1)));
        e.setLine(0, "§d§l===============");
        e.setLine(1, "§b§l" + e.getLine(1));
        e.setLine(2, plugin.api.getPaymentSignStrig(gachaname));
        e.setLine(3, "§d§l===============");
    }

}
