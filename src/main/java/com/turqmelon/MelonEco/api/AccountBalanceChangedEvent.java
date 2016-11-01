package com.turqmelon.MelonEco.api;

import com.turqmelon.MelonEco.utils.Account;
import com.turqmelon.MelonEco.utils.Currency;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Creator: Devon
 * Project: MelonEco
 */
public class AccountBalanceChangedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Account account;
    private AccountAction action;
    private Currency currency;
    private double amount;

    public AccountBalanceChangedEvent(Account account, AccountAction action, Currency currency, double amount) {
        this.account = account;
        this.action = action;
        this.currency = currency;
        this.amount = amount;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Account getAccount() {
        return account;
    }

    public AccountAction getAction() {
        return action;
    }

    public Currency getCurrency() {
        return currency;
    }

    public double getAmount() {
        return amount;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public enum AccountAction {

        DEPOSIT,
        WITHDRAW

    }

}
