package com.shojabon.man10gachav3.ToolPackages;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.function.BiFunction;

public class AnvilGUI {

    Player player;
    String text;
    BiFunction<InventoryClickEvent, String, String> function;
    NMSWrapper wrapper = new NMSWrapper();
    Inventory inv;
    Listener listener = new ListenUp();
    boolean open = false;
    int containerId;
    public AnvilGUI(Player player, String text, BiFunction<InventoryClickEvent, String, String> function){
        this.player = player;
        this.text = text;
        this.function = function;
        ItemStack paper = new SItemStack(Material.LEATHER_CHESTPLATE).setDisplayname(text).build();
        wrapper.handleInventoryCloseEvent(player);
        wrapper.setActiveContainerDefault(player);
        Bukkit.getPluginManager().registerEvents(listener, Bukkit.getPluginManager().getPlugin("Man10GachaV3"));
        Object container = wrapper.newContainerAnvil(player);
        inv = wrapper.toBukkitInventory(container);
        inv.setItem(0, paper);
        containerId = wrapper.getNextContainerId(player);
        wrapper.sendPacketOpenWindow(player, containerId);
        wrapper.setActiveContainer(player, container);
        wrapper.setActiveContainerId(container, containerId);
        wrapper.addActiveContainerSlotListener(container, player);

        open = true;
    }

    public void closeInventory() {
        Validate.isTrue(open, "You can't close an inventory that isn't open!");
        open = false;
        wrapper.handleInventoryCloseEvent(player);
        wrapper.setActiveContainerDefault(player);
        wrapper.sendPacketCloseWindow(player, containerId);
        HandlerList.unregisterAll(listener);
    }

    private class ListenUp implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if(e.getInventory().equals(inv)) {
                e.setCancelled(true);
                if(e.getRawSlot() == 2) {
                    final ItemStack clicked = inv.getItem(e.getRawSlot());
                    if(clicked == null || clicked.getType() == Material.AIR) return;
                    final String ret = function.apply(e, clicked.hasItemMeta() ? clicked.getItemMeta().getDisplayName() : clicked.getType().toString());
                    if(ret != null) {
                        final ItemMeta meta = clicked.getItemMeta();
                        meta.setDisplayName(ret);
                        clicked.setItemMeta(meta);
                        inv.setItem(e.getRawSlot(), clicked);
                    } else closeInventory();
                }
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            if(open && e.getInventory().equals(inv)) {
                closeInventory();
                function.apply(null, null);
            }
        }

    }
}
