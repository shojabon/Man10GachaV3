package com.shojabon.man10gachav3.ToolPackages;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import red.man10.man10vaultapiplus.Man10VaultAPI;
import red.man10.man10vaultapiplus.enums.TransactionCategory;
import red.man10.man10vaultapiplus.enums.TransactionType;

import java.util.UUID;

enum GachaVaultMode {
    VAULT_API_PLUS,
    VAULT,
    NONE
}


/**
 * Created by sho on 2018/06/23.
 */
public class GachaVault {
    private GachaVaultMode vaultMode = GachaVaultMode.NONE;

    private Man10VaultAPI man10VaultAPI = null;
    private VaultAPI vault = null;
    public GachaVault(){
        if(Bukkit.getPluginManager().getPlugin("Man10VaultAPIPlus") != null){
            vaultMode = GachaVaultMode.VAULT_API_PLUS;
            man10VaultAPI = new Man10VaultAPI("Man10Gacha");
        }else if(Bukkit.getPluginManager().getPlugin("Vault") != null){
            vaultMode = GachaVaultMode.VAULT;
            vault = new VaultAPI();
        }else{
            vaultMode = GachaVaultMode.NONE;
        }
    }

    public GachaVaultMode getVaultMode(){
        return this.vaultMode;
    }

    public void setVaultMode(GachaVaultMode mode){
        vaultMode = mode;
    }

    public boolean canTransferMoney(){
        if(vaultMode == GachaVaultMode.NONE){
            return false;
        }
        return true;
    }

    public void takeMoney(UUID uuid, double value){
        if(vaultMode == GachaVaultMode.VAULT_API_PLUS){
            man10VaultAPI.transferMoneyPlayerToCountry(uuid, value, TransactionCategory.GAME, TransactionType.FEE, "Man10GachaPayment");
            return;
        }else if(vaultMode == GachaVaultMode.VAULT){
            vault.silentWithdraw(uuid, value);
            return;
        }
    }

    public void giveMoney(UUID uuid, double value){
        if(vaultMode == GachaVaultMode.VAULT_API_PLUS){
            man10VaultAPI.transferMoneyCountryToPlayer(uuid, value, TransactionCategory.GAME, TransactionType.FEE, "Man10GachaPayment");
            return;
        }else if(vaultMode == GachaVaultMode.VAULT){
            vault.silentDeposit(uuid, value);
            return;
        }
    }

    public double getBalance(UUID uuid){
        if(vaultMode == GachaVaultMode.VAULT_API_PLUS){
            return man10VaultAPI.getBalance(uuid);
        }else if(vaultMode == GachaVaultMode.VAULT){
            return vault.getBalance(uuid);
        }
        return 0;
    }

    public boolean hasEnough(UUID uuid, double value){
        return getBalance(uuid) >= value;
    }




}

class VaultAPI {
    public static Economy economy = null;

    public  VaultAPI(){
        setupEconomy();
    }

    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            Bukkit.getLogger().warning("Vault plugin is not installed");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Bukkit.getLogger().warning("Can't get vault service");
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    /////////////////////////////////////
    //      残高確認
    /////////////////////////////////////
    public double  getBalance(UUID uuid){
        return economy.getBalance(Bukkit.getOfflinePlayer(uuid));
    }

    public double  getBalance(OfflinePlayer p){
        return economy.getBalance(p);
    }

    /////////////////////////////////////
    //      引き出し
    /////////////////////////////////////
    public Boolean  silentWithdraw(UUID uuid, double money){
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if(p == null){
            Bukkit.getLogger().info(uuid.toString()+"は見つからない");
            return false;
        }
        EconomyResponse resp = economy.withdrawPlayer(p,money);
        if(resp.transactionSuccess()){
            return true;
        }
        return  false;
    }
    /////////////////////////////////////
    //      お金を入れる
    /////////////////////////////////////
    public Boolean  silentDeposit(UUID uuid,double money){
        OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
        if(p == null){
            Bukkit.getLogger().info(uuid.toString()+"は見つからない");

            return false;
        }
        EconomyResponse resp = economy.depositPlayer(p,money);
        if(resp.transactionSuccess()){
            return true;
        }
        return  false;
    }

    public Boolean silentDeposit(OfflinePlayer p, double money){
        EconomyResponse resp = economy.depositPlayer(p,money);
        if(resp.transactionSuccess()){
            return true;
        }
        return  false;
    }

    public Boolean silentWithdraw(OfflinePlayer p, double money){
        EconomyResponse resp = economy.withdrawPlayer(p,money);
        if(resp.transactionSuccess()){
            return true;
        }
        return  false;
    }


    public String complexJpyBalForm(Long val){
        if(val < 10000){
            return String.valueOf(val);
        }
        if(val < 100000000){
            long man = val/10000;
            String left = String.valueOf(val).substring(String.valueOf(val).length() - 4);
            if(Long.parseLong(left) == 0){
                return man + "万";
            }
            return man + "万" + Long.parseLong(left);
        }
        if(val < 100000000000L){
            long oku = val/100000000;
            String man = String.valueOf(val).substring(String.valueOf(val).length() - 8);
            String te = man.substring(0, 4);
            String left = String.valueOf(val).substring(String.valueOf(val).length() - 4);
            if(Long.parseLong(te)  == 0){
                if( Long.parseLong(left) == 0){
                    return oku + "億";
                }else{
                    return oku + "億"+ Long.parseLong(left);
                }
            }else{
                if( Long.parseLong(left) == 0){
                    return oku + "億" + Long.parseLong(te) + "万";
                }
            }
            return oku + "億" + Long.parseLong(te) + "万" + Long.parseLong(left);
        }
        return "Null";
    }
}