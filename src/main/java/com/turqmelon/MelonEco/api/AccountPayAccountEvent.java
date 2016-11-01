package com.turqmelon.MelonEco.api;

import com.turqmelon.MelonEco.utils.Account;
import com.turqmelon.MelonEco.utils.Currency;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Creator: Devon
 * Project: MelonEco
 */
public class AccountPayAccountEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Account sender;
    private Account recipient;
    private Currency currency;
    private double amount;
    private boolean cancelled = false;

    public AccountPayAccountEvent(Account sender, Account recipient, Currency currency, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.currency = currency;
        this.amount = amount;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Account getSender() {
        return sender;
    }

    public Account getRecipient() {
        return recipient;
    }

    public void setRecipient(Account recipient) {
        this.recipient = recipient;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }


}
