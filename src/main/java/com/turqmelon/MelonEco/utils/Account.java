package com.turqmelon.MelonEco.utils;

import com.turqmelon.MelonEco.MelonEco;
import com.turqmelon.MelonEco.api.AccountBalanceChangedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/

public class Account {

    private UUID uuid;
    private String nickname;
    private Map<Currency, Double> balances = new HashMap<>();

    private boolean canReceiveCurrency = true;

    public Account(UUID uuid, String nickname) {
        this.uuid = uuid;
        this.nickname = nickname;
    }

    // If RedisConnect is in use, tells all instances of MelonEco to recache
    // this account's data, if it's stored locally.
    public void requireRecache() {
        RedisManager redis = MelonEco.getInstance().getRedisManager();
        if (redis != null) {
            redis.sendRecacheRequest(this);
        }
    }

    public boolean withdraw(Currency currency, double amount){
        return withdraw(currency, amount, false);
    }

    public boolean withdraw(Currency currency, double amount, boolean silent){
        if (getBalance(currency) >= amount){
            if (amount == 0) return false;
            setBalance(currency, getBalance(currency)-amount);
            if (!silent){
                Player player = Bukkit.getPlayer(getUuid());
                if (player!=null&&player.isOnline()){
                    player.sendMessage("§c§l[Eco] §c-" + currency.format(amount));
                }
            }
            MelonEco.getDataStore().saveAccount(this);
            requireRecache();

            Bukkit.getPluginManager().callEvent(new AccountBalanceChangedEvent(this, AccountBalanceChangedEvent.AccountAction.WITHDRAW, currency, amount));
            return true;
        }
        return false;
    }

    public boolean deposit(Currency currency, double amount){
        return deposit(currency, amount, false);
    }

    public boolean deposit(Currency currency, double amount, boolean silent){
        if (isCanReceiveCurrency()){
            if (amount == 0) return false;
            setBalance(currency, getBalance(currency)+amount);
            if (!silent){
                Player player = Bukkit.getPlayer(getUuid());
                if (player!=null&&player.isOnline()){
                    player.sendMessage("§a§l[Eco] §a+" + currency.format(amount));
                }
            }
            MelonEco.getDataStore().saveAccount(this);
            requireRecache();

            Bukkit.getPluginManager().callEvent(new AccountBalanceChangedEvent(this, AccountBalanceChangedEvent.AccountAction.DEPOSIT, currency, amount));
            return true;
        }
        return false;
    }

    public String getDisplayName(){
        return getNickname() != null ? getNickname() : getUuid().toString();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setBalance(Currency currency, double amount){
        getBalances().put(currency, amount);
    }

    public double getBalance(Currency currency){
        if (getBalances().containsKey(currency)){
            return getBalances().get(currency);
        }
        return currency.getDefaultBalance();
    }

    public boolean isCanReceiveCurrency() {
        return canReceiveCurrency;
    }

    public void setCanReceiveCurrency(boolean canReceiveCurrency) {
        this.canReceiveCurrency = canReceiveCurrency;
    }

    public Map<Currency, Double> getBalances() {
        return balances;
    }
}
