package com.turqmelon.MelonEco.utils;

import com.archeinteractive.rc.RedisConnect;
import com.archeinteractive.rc.redis.pubsub.NetTask;
import com.archeinteractive.rc.redis.pubsub.NetTaskSubscribe;
import com.turqmelon.MelonEco.MelonEco;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Creator: Devon
 * Project: MelonEco
 */
public class RedisManager {

    // For multi-server setups
    // If a balance is updated on one server, require the account to be re-cached on others

    public static final String CHANNEL_NAME = "melonecocom";

    public RedisManager() {
        RedisConnect.getRedis().registerChannel(CHANNEL_NAME);
        RedisConnect.getRedis().getDispatch().registerTasks(this);
    }

    public void sendRecacheRequest(Account account) {
        NetTask.withName("recacheact")
                .withArg("accountid", account.getUuid().toString())
                .send(CHANNEL_NAME);
    }

    @NetTaskSubscribe(name = "recacheact", args = {"accountid"})
    public void onRecacheRequestReceived(HashMap<String, Object> args) {

        UUID accountID = UUID.fromString((String) args.get("accountid"));
        Account cachedAccount = null;
        for (Account account : new ArrayList<>(AccountManager.getAccounts())) {
            if (account.getUuid().equals(accountID)) {
                cachedAccount = account;
                break;
            }
        }

        // Check to make sure the account is cached. If it's not, we don't do anything.
        if (cachedAccount != null) {

            // Remove the account from the cache
            AccountManager.getAccounts().remove(cachedAccount);

            // Redownload account data
            new BukkitRunnable() {
                @Override
                public void run() {
                    Account account = AccountManager.getAccount(accountID);
                    if (account != null && !AccountManager.getAccounts().contains(account)) {
                        AccountManager.getAccounts().add(account);
                    }
                }
            }.runTaskAsynchronously(MelonEco.getInstance());

        }

    }


}
