package com.turqmelon.MelonEco.commands;

/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/

import com.turqmelon.MelonEco.MelonEco;
import com.turqmelon.MelonEco.utils.Account;
import com.turqmelon.MelonEco.utils.AccountManager;
import com.turqmelon.MelonEco.utils.Currency;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class EcoSetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        new BukkitRunnable(){
            @Override
            public void run() {
                if (!sender.hasPermission("eco.set")){
                    sender.sendMessage("§c§l[Eco] §cYou don't have permission to set.");
                    return;
                }

                if (args.length < 2){
                    sender.sendMessage("§c§l[Eco] §cAllows you set an account balance.");
                    sender.sendMessage("§c§l[Eco] §cUsage: §f/ecoset <Account> <Amount> [Currency]");
                    return;
                }

                Currency currency = AccountManager.getDefaultCurrency();

                if (args.length == 3){
                    currency = AccountManager.getCurrency(args[2]);
                }

                if (currency != null){

                    double amount;

                    if (currency.isDecimalSupported()){
                        try {

                            amount = Double.parseDouble(args[1]);

                        }catch(NumberFormatException ex){
                            sender.sendMessage("§c§l[Eco] §cPlease provide a valid amount.");
                            return;
                        }
                    }
                    else{
                        try {

                            amount = Integer.parseInt(args[1]);

                        }catch(NumberFormatException ex){
                            sender.sendMessage("§c§l[Eco] §cPlease provide a valid amount.");
                            return;
                        }
                    }


                    Account target  = AccountManager.getAccount(args[0]);
                    if (target != null){

                        target.setBalance(currency, amount);

                        MelonEco.getDataStore().saveAccount(target);

                        sender.sendMessage("§a§l[Eco] §aBalance set to " + currency.getColor() + currency.format(amount) + "§a for " + target.getDisplayName() + ".");

                    }
                    else{
                        sender.sendMessage("§c§l[Eco] §cTarget account not found.");
                    }

                }
                else{
                    sender.sendMessage("§c§l[Eco] §cUnknown currency.");
                }
            }
        }.runTaskAsynchronously(MelonEco.getInstance());



        return true;
    }
}
