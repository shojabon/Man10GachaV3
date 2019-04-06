package com.shojabon.man10gachav3.ToolPackages;

import com.shojabon.man10gachav3.DataPackages.GachaBannerDictionary;
import com.shojabon.man10gachav3.DataPackages.SBannerItemStack;
import com.shojabon.man10gachav3.DataPackages.GachaSound;
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
    private int volu;
    private int pitc;
    private String title;
    private BiFunction<InventoryClickEvent, GachaSound, String> okFunction;
    private Function<InventoryClickEvent, String> cancelFunction;
    boolean trans = false;

    public SoundSelectorAPI(String title, Player p, float volume, float pitch, Sound sound, BiFunction<InventoryClickEvent, GachaSound, String> okFunction, Function<InventoryClickEvent, String> cancelFunction){
        p.closeInventory();
        this.p = p;
        this.title = title;
        this.volu = (int) (volume * 10);
        this.pitc = (int) (pitch * 10);
        this.okFunction = okFunction;
        this.cancelFunction = cancelFunction;
        this.sound = sound;
        this.plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin("Man10GachaV3");
        dictionary = new GachaBannerDictionary();
        Bukkit.getPluginManager().registerEvents(listener, plugin);
        SInventory inventory = new SInventory(5,title);
        inventory.fillInventory(new SItemStack(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDamage(11).build());

        inventory.setItem(15, new SItemStack(dictionary.getSymbol("plus")).setDisplayname("§b§lヴォリューム +").build());
        inventory.setItem(16, new SItemStack(dictionary.getSymbol("minus")).setDisplayname("§b§lヴォリューム -").build());
        inventory.setItem(33, new SItemStack(dictionary.getSymbol("plus")).setDisplayname("§b§lピッチ +").build());
        inventory.setItem(34, new SItemStack(dictionary.getSymbol("minus")).setDisplayname("§b§lピッチ -").build());

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
        String[] value = String.valueOf(volu).split("");
        if(volu == 0) value = new String[]{"0", "0"};
        if(value.length != 2) value = new String[]{"0", value[0]};
        item.add(new SItemStack(dictionary.getItem(Integer.valueOf(value[0]))).setDisplayname("§b§l" + value[0]).build());
        item.add(new SItemStack(Material.STONE_BUTTON).setDisplayname("§b§l.").build());
        item.add(new SItemStack(dictionary.getItem(Integer.valueOf(value[1]))).setDisplayname("§b§l" + value[1]).build());
        for(int i = 0;i < 3;i++){
            inv.setItem(12 + i, item.get(i));
        }
    }

    public void renderPitch(){
        List<ItemStack> item = new ArrayList<>();
        String[] value = String.valueOf(pitc).split("");
        if(pitc == 0) value = new String[]{"0", "0"};
        if(value.length != 2) value = new String[]{"0", value[0]};
        item.add(new SItemStack(dictionary.getItem(Integer.valueOf(value[0]))).setDisplayname("§b§l" + value[0]).build());
        item.add(new SItemStack(Material.STONE_BUTTON).setDisplayname("§b§l.").build());
        item.add(new SItemStack(dictionary.getItem(Integer.valueOf(value[1]))).setDisplayname("§b§l" + value[1]).build());
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
                new SoundSelectorAPI(title, p, volu, pitc, sound, okFunction, cancelFunction);
            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("Man10GachaV3"), 1);
    }

    class Listener implements org.bukkit.event.Listener
    {

        @EventHandler
        public void onClick(InventoryClickEvent e){
            if(e.getWhoClicked().getUniqueId() != p.getUniqueId()) return;
            if(e.getRawSlot() <= 53 && e.getRawSlot() != -999 && e.getInventory().getItem(e.getRawSlot()) != null) new GachaSound(Sound.BLOCK_DISPENSER_DISPENSE, 1 ,1).playSoundToPlayer((Player) e.getWhoClicked());
            e.setCancelled(true);
            int r = e.getRawSlot();
            if(r == 15){
                if(volu <= 99){
                    volu += 1;
                    renderVolume();
                }
            }
            if(r == 18){
                volu = 0;
                pitc = 0;
                sound = null;
                inv.setItem(19, new SItemStack(Material.ANVIL).setDisplayname("§a§l音を変更する").addLore("§b§l現在設定:なし").build());
                render();
                return;
            }
            if(r == 16){
                if(volu >= 0){
                    volu -= 1;
                    if(volu < 0D) volu = 0;
                    renderVolume();
                }
                return;
            }
            if(r == 33){
                if(pitc <= 99){
                    pitc += 1;
                    renderPitch();
                }
                return;
            }
            if(r == 34){
                if(pitc >= 0){
                    pitc -= 1;
                    if(pitc < 0D) pitc = 0;
                    renderPitch();
                }
                return;
            }
            if(r == 44) {
                trans = true;
                cancelFunction.apply(e);
                return;
            }
            if(r == 28){
                float vol = ((float) volu)/10;
                float pit = ((float) pitc)/10;
                trans = true;
                String func = okFunction.apply(e, new GachaSound(sound, vol, pit));
                if(func == null){
                    p.closeInventory();
                    return;
                }
            }
            if(r == 10){
                float vol = ((float) volu)/10;
                float pit = ((float) pitc)/10;
                new GachaSound(sound, vol, pit).playSoundToPlayer(p);
            }
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
