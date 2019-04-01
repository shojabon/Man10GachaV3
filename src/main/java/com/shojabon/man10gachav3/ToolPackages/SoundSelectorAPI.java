package com.shojabon.man10gachav3.ToolPackages;

import com.shojabon.man10gachav3.DataPackages.GachaBannerDictionary;
import com.shojabon.man10gachav3.DataPackages.SBannerItemStack;
import com.shojabon.man10gachav3.GameDataPackages.GachaSound;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SoundSelectorAPI {
    Inventory inv;
    Listener listener = new Listener();
    JavaPlugin plugin;
    Player p;
    private GachaBannerDictionary dictionary;
    private String prefix = "§6[§aMg§fac§dha§5V2§6]§f";
    private Sound sound;
    private float volume;
    private float pitch;
    private String title;
    private BiFunction<InventoryClickEvent, GachaSound, String> okFunction;
    private Function<InventoryClickEvent, String> cancelFunction;
    boolean trans = false;

    public SoundSelectorAPI(String title, Player p, float volume, float pitch, Sound sound, BiFunction<InventoryClickEvent, GachaSound, String> okFunction, Function<InventoryClickEvent, String> cancelFunction){
        p.closeInventory();
        this.p = p;
        this.title = title;
        this.volume = volume;
        this.pitch = pitch;
        this.okFunction = okFunction;
        this.cancelFunction = cancelFunction;
        this.sound = sound;
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        dictionary = new GachaBannerDictionary();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        SInventory inventory = new SInventory(5,title);
        inventory.fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDamage(11).build());
        inventory.setItem(new int[]{15, 33}, dictionary.getSymbol("plus"));
        inventory.setItem(new int[]{16, 34}, dictionary.getSymbol("minus"));
        inventory.setItem(18, new SItemStack(Material.BARRIER).setDisplayname("§c§l無音に設定する").build());
        inventory.setItem(new int[]{14,13,12,32,31,30}, new ItemStack(Material.AIR));
        String name = "なし";
        if(sound != null) name = sound.name();
        inventory.setItem(19, new SItemStack(Material.ANVIL).setDisplayname("§a§l音を変更する").addLore("§b§l現在設定:" + name).build());
        inventory.setItem(10, new SItemStack(Material.NOTE_BLOCK).setDisplayname("§c§l試聴する").build());
        inventory.setItem(28, new SItemStack(Material.STAINED_GLASS_PANE).setDamage(5).setDisplayname("§a§l決定する").build());
        inventory.setItem(44, new SItemStack(new SBannerItemStack((short) 4).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_LEFT)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.WHITE, PatternType.STRIPE_MIDDLE)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_TOP)).pattern(new Pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)).pattern(new Pattern(DyeColor.BLUE, PatternType.CURLY_BORDER)).build()).setDisplayname("§c§l§n戻る").build());
        inv = inventory.build();
        render();
        p.openInventory(inv);
    }

    public void render(){
        renderPitch();
        renderVolume();
    }

    public void renderVolume(){
        List<ItemStack> item = new ArrayList<>();
        if(String.valueOf(volume).contains("E")) volume = 0.0f;
        String[] value = String.valueOf(volume).split("");
        for(String s : value){
            if(s.equalsIgnoreCase(".")){
                item.add(new SItemStack(Material.STONE_BUTTON).setDisplayname("§b§l.").build());
            }else{
                item.add(new SItemStack(dictionary.getItem(Integer.valueOf(s))).setDisplayname("§b§l" + s).build());
            }
        }
        for(int i = 0;i < 3;i++){
            inv.setItem(12 + i, item.get(i));
        }
    }

    public void renderPitch(){
        List<ItemStack> item = new ArrayList<>();
        if(String.valueOf(volume).contains("E")) pitch = 0.0f;
        String[] value = String.valueOf(pitch).split("");
        for(String s : value){
            if(s.equalsIgnoreCase(".")){
                item.add(new SItemStack(Material.STONE_BUTTON).setDisplayname("§b§l.").build());
            }else{
                item.add(new SItemStack(dictionary.getItem(Integer.valueOf(s))).setDisplayname("§b§l" + s).build());
            }
        }
        for(int i = 0;i < 3;i++){
            inv.setItem(30 + i, item.get(i));
        }
    }

    private void close(Player p){
        HandlerList.unregisterAll(listener);
        p.closeInventory();
    }

    private void reopen(){
        trans = false;
        new BukkitRunnable(){
            @Override
            public void run() {
                new SoundSelectorAPI(title, p, volume, pitch, sound, okFunction, cancelFunction);
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("Man10GachaV3"), 1);
    }

    class Listener implements org.bukkit.event.Listener
    {

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getWhoClicked().getUniqueId() != p.getUniqueId()) return;
            e.setCancelled(true);
            int r = e.getRawSlot();
            if(r == 15){
                if(volume <= 9.9){
                    volume += 0.1;
                    renderVolume();
                }
            }
            if(r == 18){
                volume = 0;
                pitch = 0;
                sound = null;
                inv.setItem(19, new SItemStack(Material.ANVIL).setDisplayname("§a§l音を変更する").addLore("§b§l現在設定:なし").build());
                render();
                return;
            }
            if(r == 16){
                if(volume >= 0){
                    volume -= 0.1;
                    if(volume < 0D) volume = 0.0f;
                    renderVolume();
                }
                return;
            }
            if(r == 33){
                if(pitch <= 9.9){
                    pitch += 0.1;
                    renderPitch();
                }
                return;
            }
            if(r == 34){
                if(pitch >= 0){
                    pitch -= 0.1;
                    if(pitch < 0D) pitch = 0.0f;
                    renderPitch();
                }
                return;
            }
            if(r == 44) {
                cancelFunction.apply(e);
                return;
            }
            if(r == 28){
                String func = okFunction.apply(e, new GachaSound(sound, volume, pitch));
                if(func == null){
                    p.closeInventory();
                    return;
                }
            }
            if(r == 10) new GachaSound(sound, volume, pitch).playSoundToPlayer(p);
            if(r == 19){
                trans = true;
                p.closeInventory();
                String name = "None";
                if(sound != null) name = sound.name();
                new AnvilGUI(p, name, (event, s) -> {
                    if(event == null){
                        reopen();
                        return null;
                    }
                    try{
                        sound = Sound.valueOf(s);
                    }catch (IllegalArgumentException ee){
                        p.sendMessage(prefix + "音名が存在しません");
                        return "restart";
                    }
                    reopen();
                    return null;
                });
            }
        }



        @EventHandler
        public void onClose(InventoryCloseEvent e){
            if(e.getPlayer().getUniqueId() != p.getUniqueId()) return;
            close((Player) e.getPlayer());
            if(!trans){
                cancelFunction.apply(null);
            }
        }

    }
}
