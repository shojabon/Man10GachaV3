package com.shojabon.man10gachav3.DataPackages;

import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by sho on 2018/06/23.
 */
public class GachaItemStack implements Serializable {
    public ItemStack item;
    public ArrayList<String> broadcastMessage = null;
    public ArrayList<String> playerMessage = null;

    public ArrayList<ItemStack> items = null;
    public int amount = 1;

    public GachaSound broadcastSound = new GachaSound();
    public GachaSound playerSound = new GachaSound(Sound.ENTITY_PLAYER_LEVELUP, 1, 1);


    public GachaItemStack(ItemStack item, int amount){
        this.amount = amount;
        this.item = new SItemStack(item).setAmount(1).build();
    }

    public GachaItemStack(ItemStack item){
        this.amount = item.getAmount();
        this.item = new SItemStack(item).setAmount(1).build();
    }



    public GachaItemStack(GachaItemStack itemStack){
        loadMap(itemStack.getStringData());
    }

    public GachaItemStack(Map<String, Object> settings){
        loadMap(settings);
    }

    private void loadMap(Map<String, Object> settings){
        for(String key: settings.keySet()){
            switch(key){
                case "item":
                    item = new SItemStack(String.valueOf(settings.get(key))).build();
                    break;
                case "broadcastMessage":
                    broadcastMessage = ((ArrayList<String>) settings.get(key));
                    break;
                case "playerMessage":
                    playerMessage = ((ArrayList<String>) settings.get(key));
                    break;
                case "items":
                    ArrayList<String> list = ((ArrayList<String>) settings.get(key));
                    items = new ArrayList<>();
                    for(String st : list){
                        items.add(new SItemStack(st).build());
                    }
                    break;
                case "amount":
                    amount = (Integer) settings.get(key);
                    break;
                case "broadcastSound":
                    broadcastSound = ((GachaSound) settings.get(key));
                    break;
                case "playerSound":
                    playerSound = ((GachaSound) settings.get(key));
                    break;
            }
        }
    }




    public GachaItemStack(ItemStack item,
                          ArrayList<ItemStack> outputItems,
                          ArrayList<String> broadcastMessage,
                          ArrayList<String> playerMessage,
                          GachaSound broadcastSound,
                          GachaSound playerSound


    ){
        this.item = item;
        this.broadcastMessage = broadcastMessage;
        this.playerMessage = playerMessage;
        this.items = outputItems;
        this.broadcastSound = broadcastSound;
        this.playerSound = playerSound;
    }

    public Map<String, Object> getStringData(){
        Map<String, Object> objects = new HashMap<>();
        objects.put("item", new SItemStack(this.item).setAmount(1).toBase64());
        if(broadcastMessage != null){
            objects.put("broadcastMessage", this.broadcastMessage);
        }
        if(amount != 1 && amount > 0){
            objects.put("amount", this.amount);
        }
        if(playerMessage != null){
            objects.put("playerMessage", this.playerMessage);
        }
        if(items != null){
            ArrayList<String> items = new ArrayList<>();
            for(ItemStack item: this.items){
                items.add(new SItemStack(item).toBase64());
            }
            objects.put("items", items);
        }
        if(!broadcastSound.getStringData().equals(new GachaSound(Sound.ENTITY_PLAYER_LEVELUP, 1, 1).getStringData()) && !broadcastSound.getStringData().equals(new GachaSound().getStringData())){
            objects.put("broadcastSound", this.broadcastSound);
        }
        if(!playerSound.getStringData().equals(new GachaSound(Sound.ENTITY_PLAYER_LEVELUP, 1, 1).getStringData())){
            objects.put("playerSound", this.playerSound);
        }
        return objects;
    }

    public boolean isTheSame(GachaItemStack comparison){
        Map<String, Object> obj = comparison.getStringData();
        Map<String, Object> obj2 = getStringData();
        if(obj.keySet().size() != obj2.keySet().size()) return false;
        for(String key: obj.keySet()){
            if(obj.get(key) != null && obj2.get(key) != null){
                if(obj.get(key) instanceof GachaSound){
                    if(!((GachaSound) obj.get(key)).getStringData().equals(((GachaSound) obj2.get(key)).getStringData())) return false;
                }else {
                    if(!obj.get(key).equals(obj2.get(key)))return false;
                }
            }else{
                return false;
            }
        }
        return true;
    }

}
