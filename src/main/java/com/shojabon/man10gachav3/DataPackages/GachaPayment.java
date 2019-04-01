package com.shojabon.man10gachav3.DataPackages;
import com.shojabon.man10gachav3.DataPackages.GachaPaymentData.GachaItemStackPayment;
import com.shojabon.man10gachav3.DataPackages.GachaPaymentData.GachaVaultPayment;
import com.shojabon.man10gachav3.ToolPackages.SItemStack;
import com.shojabon.man10gachav3.enums.GachaPaymentType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sho on 2018/07/14.
 */
public class GachaPayment {
    private GachaPaymentType type;
    private GachaItemStackPayment itemStackPayment;
    private GachaVaultPayment vaultPayment;

    public GachaPayment(GachaItemStackPayment itemPayment){
        this.type = GachaPaymentType.ITEM;
        this.itemStackPayment = itemPayment;
    }

    public GachaPayment(GachaVaultPayment vaultPayment) {
        this.type = GachaPaymentType.VAULT;
        this.vaultPayment = vaultPayment;
    }

    public GachaPaymentType getType(){
        return type;
    }

    public GachaVaultPayment getVaultPayment(){
        return vaultPayment;
    }

    public GachaItemStackPayment getItemStackPayment(){
        return itemStackPayment;
    }




    public Map<String, String> getStringData(){
        Map<String, String> out = new HashMap<>();
        out.put("type", type.toString());
        if(type == GachaPaymentType.VAULT){
            out.put("amount", String.valueOf(vaultPayment.getValue()));
        }
        if(type == GachaPaymentType.ITEM){
            out.put("item", new SItemStack(itemStackPayment.getItemStack()).toBase64());
            out.put("amount", String.valueOf(itemStackPayment.getAmount()));
        }
        return out;
    }


}
