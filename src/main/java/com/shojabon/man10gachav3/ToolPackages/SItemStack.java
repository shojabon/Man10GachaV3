package com.shojabon.man10gachav3.ToolPackages;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SItemStack {

    //created by Sho0
    private ItemStack item;

    public SItemStack(Material material){
        item = new ItemStack(material);
    }

    public SItemStack(ItemStack item){
        this.item = item.clone();
    }

    public SItemStack(String data){
        this.item = itemFromBase64(data);
    }

    public String getDisplayName(){
        String a = item.getItemMeta().getDisplayName();
        if(a == null) return item.getType().name();
        return a;
    }

    private ItemStack itemFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items[0];
        } catch (Exception e) {
            return null;
        }
    }

    private String itemToBase64(ItemStack item) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            ItemStack[] items = new ItemStack[1];
            items[0] = item;
            dataOutput.writeInt(items.length);
            for (ItemStack item1 : items) {
                dataOutput.writeObject(item1);
            }

            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }


    public SItemStack setAmount(int amount){
        item.setAmount(amount);
        return this;
    }

    public SItemStack setDisplayname(String name){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);
        return this;
    }

    public SItemStack addLore(String lore){
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lores = itemMeta.getLore();
        if(lores == null) lores = new ArrayList<>();
        lores.add(lore);
        itemMeta.setLore(lores);
        item.setItemMeta(itemMeta);
        return this;
    }

    public SItemStack addEnchantment(Enchantment enchant, int level){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addEnchant(enchant, level, true);
        item.setItemMeta(itemMeta);
        return this;
    }

    public SItemStack addFlag(ItemFlag itemFalg){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(itemFalg);
        item.setItemMeta(itemMeta);
        return this;
    }

    public SItemStack setLore(List<String> lore){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return this;
    }

    public SItemStack setFlags(List<ItemFlag> itemFlags){
        ItemMeta itemMeta = item.getItemMeta();
        for(ItemFlag itemFlag : itemMeta.getItemFlags()){
            itemMeta.removeItemFlags(itemFlag);
        }
        for(ItemFlag itemFlag : itemFlags){
            itemMeta.addItemFlags(itemFlag);
        }
        item.setItemMeta(itemMeta);
        return this;
    }

    public SItemStack setDamage(int damage){
        item.setDurability((short)damage);
        return this;
    }

    public SItemStack setGlowingEffect(boolean enabled) {
        if(enabled){
            this.addFlag(ItemFlag.HIDE_ENCHANTS);
            this.addEnchantment(Enchantment.LURE, 1);
        }else{
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemMeta.removeEnchant(Enchantment.LURE);
            item.setItemMeta(itemMeta);
        }
        return this;
    }
    public SItemStack setUnBreakable(boolean enabled){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setUnbreakable(enabled);
        item.setItemMeta(itemMeta);
        return this;
    }

    public ItemStack build(){
        return item;
    }

    public String toBase64(){
        return itemToBase64(this.item);
    }


}
