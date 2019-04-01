package com.shojabon.man10gachav3.DataPackages.GachaPaymentData;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by sho on 2018/07/14.
 */
public class GachaVaultPayment {
    private double value;
    public GachaVaultPayment(double value){
        this.value = value;
    }

    public double getValue(){
        return this.value;
    }

    public Map<String, String> getStringData(){
        Map<String, String> out = new HashMap<>();
        out.put("value", String.valueOf(value));
        return out;
    }
}
