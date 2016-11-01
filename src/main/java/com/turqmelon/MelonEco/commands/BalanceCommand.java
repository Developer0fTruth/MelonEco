package com.turqmelon.MelonEco.commands;

import com.turqmelon.MelonEco.MelonEco;
import com.turqmelon.MelonEco.utils.Account;
import com.turqmelon.MelonEco.utils.AccountManager;
import com.turqmelon.MelonEco.utils.Currency;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/
public class BalanceCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        new BukkitRunnable(){
            @Override
            public void run() {
                if (!sender.hasPermission("eco.balance")){
                    sender.sendMessage("§c§l[Eco] §cYou don't have permission to view balances.");
                    return;
                }

                Account account = null;
                if (args.length == 0 && (sender instanceof Player)){
                    account = AccountManager.getAccount((Player)sender);
                }
                else if (sender.hasPermission("eco.balance.other") && args.length == 1){
                    account = AccountManager.getAccount(args[0]);
                }

                if (account != null){

                    if (AccountManager.getCurrencies().size() == 0){
                        sender.sendMessage("§c§l[Eco] §cNo balances to show for \"" + account.getDisplayName() + "\".");
                    }
                    else if (AccountManager.getCurrencies().size() == 1){
                        Currency currency = AccountManager.getDefaultCurrency();
                        if (currency == null){
                            sender.sendMessage("§c§l[Eco] §cNo default currency.");
                            return;
                        }
                        double balance = account.getBalance(currency);
                        sender.sendMessage("§e§l[Eco] §e" + account.getDisplayName() + "'s balance: " + currency.getColor() + currency.format(balance));
                    }
                    else{
                        sender.sendMessage("§e§l[Eco] §e" + account.getDisplayName() + "'s balances:");
                        for(Currency currency : AccountManager.getCurrencies()){
                            double balance = account.getBalance(currency);
                            sender.sendMessage("§e§l[Eco]     " + currency.getColor() + currency.format(balance));
                        }
                    }
                }
                else{
                    sender.sendMessage("§c§l[Eco] §cAccount not found.");
                }
            }
        }.runTaskAsynchronously(MelonEco.getInstance());



        return true;
    }
}
