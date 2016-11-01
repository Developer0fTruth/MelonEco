package com.turqmelon.MelonEco.api;

import com.turqmelon.MelonEco.utils.Account;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Creator: Devon
 * Project: MelonEco
 */
public class AccountLoadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Account account;

    public AccountLoadedEvent(Account account) {
        this.account = account;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Account getAccount() {
        return account;
    }

    public HandlerList getHandlers() {
        return handlers;
    }


}
