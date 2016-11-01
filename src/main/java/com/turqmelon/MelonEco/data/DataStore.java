package com.turqmelon.MelonEco.data;

import com.turqmelon.MelonEco.utils.Account;
import com.turqmelon.MelonEco.utils.Currency;

import java.util.Map;
import java.util.UUID;

/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/
public abstract class DataStore {

    private String name;
    private boolean topSupported;

    public DataStore(String name, boolean topSupported) {
        this.name = name;
        this.topSupported = topSupported;
    }

    public abstract void initalize();
    public abstract void close();

    public abstract void loadCurrencies();
    public abstract void saveCurrency(Currency currency);
    public abstract void deleteCurrency(Currency currency);

    public abstract Map<String, Double> getTopList(Currency currency, int offset, int amount);
    public abstract Account loadAccount(String name);
    public abstract Account loadAccount(UUID uuid);
    public abstract void saveAccount(Account account);
    public abstract void deleteAccount(Account account);

    public String getName() {
        return name;
    }

    public boolean isTopSupported() {
        return topSupported;
    }
}
