package com.turqmelon.MelonEco.utils;

import com.turqmelon.MelonEco.MelonEco;
import com.turqmelon.MelonEco.api.AccountLoadedEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/
public class AccountManager {

    private static final List<Account> accounts = new ArrayList<>();
    private static List<Currency> currencies = new ArrayList<>();

    public static Currency getDefaultCurrency() {
        for (Currency currency : getCurrencies()) {
            if (currency.isDefaultCurrency()) {
                return currency;
            }
        }
        return null;
    }

    public static Currency getCurrency(String name) {
        for (Currency currency : getCurrencies()) {
            if (currency.getSingular().equalsIgnoreCase(name) ||
                    currency.getPlural().equalsIgnoreCase(name)) {
                return currency;
            }
        }
        return null;
    }

    public static Currency getCurrency(UUID uuid) {
        for (Currency currency : getCurrencies()) {
            if (currency.getUuid().equals(uuid)) {
                return currency;
            }
        }
        return null;
    }

    public static List<Currency> getCurrencies() {
        return currencies;
    }

    public static Account getAccount(Player player) {
        return getAccount(player.getUniqueId());
    }

    public static Account getAccount(String name) {
        for (Account account : new ArrayList<>(getAccounts())) {
            if (account.getNickname() != null && account.getNickname().equalsIgnoreCase(name)) {
                return account;
            }
        }

        Account account = MelonEco.getDataStore().loadAccount(name);
        Bukkit.getPluginManager().callEvent(new AccountLoadedEvent(account));
        return account;
    }

    public static Account getAccount(UUID uuid) {
        for (Account account : new ArrayList<>(getAccounts())) {
            if (uuid == null) return null;
            if (account.getUuid() == null) continue;
            if (account.getUuid().equals(uuid)) {
                return account;
            }
        }
        Account account = MelonEco.getDataStore().loadAccount(uuid);
        Bukkit.getPluginManager().callEvent(new AccountLoadedEvent(account));
        return account;
    }

    public static List<Account> getAccounts() {
        return accounts;
    }
}
