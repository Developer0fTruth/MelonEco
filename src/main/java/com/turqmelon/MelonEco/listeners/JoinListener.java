package com.turqmelon.MelonEco.listeners;

import com.turqmelon.MelonEco.MelonEco;
import com.turqmelon.MelonEco.utils.Account;
import com.turqmelon.MelonEco.utils.AccountManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/
public class JoinListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLogin(AsyncPlayerPreLoginEvent event){
        Account account = AccountManager.getAccount(event.getUniqueId());

        if (account == null){
            account = new Account(event.getUniqueId(), event.getName());
            MelonEco.getDataStore().saveAccount(account);
            MelonEco.getInstance().getLogger().log(Level.INFO, "Created account: " + account.getDisplayName());
        }
        else if ((account.getNickname() != null && !account.getNickname().equals(event.getName())) || account.getNickname() == null){
            account.setNickname(event.getName());
            MelonEco.getDataStore().saveAccount(account);
            MelonEco.getInstance().getLogger().log(Level.INFO, "Updated account nickname due to name change: " + account.getDisplayName());
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        new BukkitRunnable(){
            @Override
            public void run() {
                Account account = AccountManager.getAccount(player);
                if (account != null && AccountManager.getAccounts().contains(account)){
                    AccountManager.getAccounts().remove(account);
                }
            }
        }.runTaskAsynchronously(MelonEco.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        new BukkitRunnable(){
            @Override
            public void run() {
                Account account = AccountManager.getAccount(player);
                if (account != null && player.isOnline() && !AccountManager.getAccounts().contains(account)){
                    AccountManager.getAccounts().add(account);
                }
            }
        }.runTaskAsynchronously(MelonEco.getInstance());
    }

}
