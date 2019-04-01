package com.shojabon.man10gachav3.DataPackages.GachaPaymentData;

import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sho on 2018/07/14.
 */
public class GachaItemStackPayment {
    private ItemStack item;
    private int amount;

    public GachaItemStackPayment(ItemStack item, int amount){
        this.item = item;
        if(amount >= 64){
            amount = 64;
        }
        this.amount = amount;
    }

    public ItemStack getItemStack(){
        return this.item;
    }

    public int getAmount(){
        return this.amount;
    }

    public Map<String, String> getStringData(){
        Map<String, String> out = new HashMap<>();
        out.put("item", new SItemStack(item).setAmount(1).toBase64());
        out.put("amount", String.valueOf(amount));
        return out;
    }

}
