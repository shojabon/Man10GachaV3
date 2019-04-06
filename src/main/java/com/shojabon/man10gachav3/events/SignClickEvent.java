package com.shojabon.man10gachav3.events;

import com.shojabon.man10gachav3.GamePackages.GachaGame;
import com.shojabon.man10gachav3.GamePackages.Man10GachaAPI;
import com.shojabon.man10gachav3.Man10GachaV3;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignClickEvent implements Listener {
    private Man10GachaV3 plugin;

    public SignClickEvent(Man10GachaV3 plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK)return;
        Block b = e.getClickedBlock();
        if(b.getType() != Material.SIGN && b.getType() != Material.WALL_SIGN && b.getType() != Material.SIGN_POST ) return;
        if(!plugin.api.ifGachaSign(b.getLocation()))return;
        String gachaName = plugin.api.getSignGacha(b.getLocation());
        //if(plugin.api.ifGachaLocked(gachaName)){
        //    e.getPlayer().sendMessage(plugin.prefix + "§4§l現在このガチャはロックされています");
        //    return;
        //}
        //start time and end time process
        //here !!!
        GachaGame game = plugin.api.getGacha(gachaName);
        if (Man10GachaAPI.inGamePlayerMap.containsKey(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(Man10GachaV3.prefix + "§c§l現在あなたはゲームをプレー中です");
            return;
        }
        if(!plugin.api.ifPlayerHasEnoughForPayment(e.getPlayer(), game)){
            e.getPlayer().sendMessage("§6§l=-=-=-=[§f" + game.getSettings().title+ "§6§l]=-=-=-=");
            for(String s : plugin.api.getLackingPaymentMessage(e.getPlayer(),game)){
                e.getPlayer().sendMessage(s);
            }
            return;
        }
        boolean bool = plugin.api.takePayment(e.getPlayer(), game);
        if(!bool){
            e.getPlayer().sendMessage(plugin.prefix + "支払いが失敗しました");
            return;
        }
        game.play(e.getPlayer());
        if(game.getPayments().size() != 0){
            e.getPlayer().sendMessage("§a§l=-=-=-=[支払い明細]=-=-=-=");
        }
        for(String s : plugin.api.getPayMessage(e.getPlayer(), game)){
            e.getPlayer().sendMessage(s);
        }
    }
}
