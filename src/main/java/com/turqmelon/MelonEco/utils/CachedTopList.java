package com.turqmelon.MelonEco.utils;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Creator: Devon
 * Project: MelonEco
 */
public class CachedTopList {

    private Currency currency;
    private int amount;
    private int offset;
    private long cacheTime;
    private LinkedHashMap<String, Double> results = new LinkedHashMap<>();

    public CachedTopList(Currency currency, int amount, int offset, long cacheTime) {
        this.currency = currency;
        this.amount = amount;
        this.offset = offset;
        this.cacheTime = cacheTime;
    }

    public boolean matches(Currency currency, int offset, int amount) {
        return currency.getUuid().equals(getCurrency().getUuid()) && offset == getOffset() && amount == getAmount();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - getCacheTime() > TimeUnit.MINUTES.toMillis(1);
    }

    public Currency getCurrency() {
        return currency;
    }

    public int getAmount() {
        return amount;
    }

    public int getOffset() {
        return offset;
    }

    public long getCacheTime() {
        return cacheTime;
    }

    public LinkedHashMap<String, Double> getResults() {
        return results;
    }

    public void setResults(LinkedHashMap<String, Double> results) {
        this.results = results;
    }
}
