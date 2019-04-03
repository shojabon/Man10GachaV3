package com.shojabon.man10gachav3.ToolPackages;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by sho on 2018/06/18.
 */

public class SInventory {

    Inventory inv = null;
    String title;
    public SInventory(int rows, String title){
        inv = Bukkit.createInventory(null, rows *9, title);
        this.title = title;
    }

    public SInventory setItem(int slot, ItemStack item){
        inv.setItem(slot, item);
        return this;
    }

    public SInventory setItem(int[] slots, ItemStack item){
        for(int i = 0;i < slots.length;i++){
            inv.setItem(slots[i], item);
        }
        return this;
    }

    public SInventory fillInventory(ItemStack item){
        for(int i = 0;i < inv.getSize();i++){
            inv.setItem(i, item);
        }
        return this;
    }

    public SInventory setTitle(String title){
        this.title = title;
        return this;
    }

    public SInventory clear(){
        this.inv.clear();
        return this;
    }

    public SInventory setLine(int line, ItemStack item){
        int[] a = new int[] {0,1,2,3,4,5,6,7,8};
        for(int i =0; i < a.length; i++){
            inv.setItem(line * 9 + a[i], item);
        }
        return this;
    }

    public Inventory build(){
        return inv;
    }



}