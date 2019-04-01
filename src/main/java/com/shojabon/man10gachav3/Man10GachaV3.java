package com.shojabon.man10gachav3;

import com.shojabon.man10gachav3.DataPackages.GachaItemStack;
import com.shojabon.man10gachav3.DataPackages.GachaPayment;
import com.shojabon.man10gachav3.DataPackages.GachaPaymentData.GachaVaultPayment;
import com.shojabon.man10gachav3.DataPackages.GachaSettings;
import com.shojabon.man10gachav3.GameDataPackages.GachaSound;
import com.shojabon.man10gachav3.GameDataPackages.Menu.GachaSettingsSelectionMenu;
import com.shojabon.man10gachav3.GamePackages.Man10GachaAPI;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public final class Man10GachaV3 extends JavaPlugin {

    public Man10GachaAPI api = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        api = new Man10GachaAPI();
        new BukkitRunnable() {
            @Override
            public void run() {
                api.loadAllGachas();
            }
        }.runTaskLater(this, 3);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void a(){
        Man10GachaAPI api = new Man10GachaAPI();
        GachaSettings settings = new GachaSettings("test", "a", new GachaSound(Sound.BLOCK_DISPENSER_DISPENSE, 1, 1), new SItemStack(Material.DIAMOND).build());
        ArrayList<GachaPayment> payments = new ArrayList<>();
        payments.add(new GachaPayment(new GachaVaultPayment(100)));

        ArrayList<GachaItemStack> items = new ArrayList<>();
        items.add(new GachaItemStack(new SItemStack(Material.STONE).setAmount(10).build()));
        items.add(new GachaItemStack(new SItemStack(Material.DIAMOND).setAmount(10).build()));
        items.add(new GachaItemStack(new SItemStack(Material.GOLD_AXE).setAmount(10).build()));
        items.add(new GachaItemStack(new SItemStack(Material.ANVIL).setAmount(10).build()));
        items.add(new GachaItemStack(new SItemStack(Material.TNT).setAmount(10).build()));
        items.add(new GachaItemStack(new SItemStack(Material.WATCH).setAmount(10).build()));
        items.add(new GachaItemStack(new SItemStack(Material.FENCE_GATE).setAmount(10).build()));
        items.add(new GachaItemStack(new SItemStack(Material.GLASS).setAmount(10).build()));
        items.add(new GachaItemStack(new SItemStack(Material.TNT).setAmount(10).build()));
        api.createGacha(settings, payments, items);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("gachav3")){
            //a();
            new GachaSettingsSelectionMenu(((Player)sender));
        }
        return false;
    }
}
