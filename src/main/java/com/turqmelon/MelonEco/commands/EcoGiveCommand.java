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

public class EcoGiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        new BukkitRunnable(){
            @Override
            public void run() {
                if (!sender.hasPermission("eco.give")){
                    sender.sendMessage("§c§l[Eco] §cYou don't have permission to give.");
                    return;
                }

                if (args.length < 2){
                    sender.sendMessage("§c§l[Eco] §cAllows you to give currency to players.");
                    sender.sendMessage("§c§l[Eco] §cUsage: §f/ecogive <Account> <Amount> [Currency]");
                    return;
                }

                Currency currency = AccountManager.getDefaultCurrency();

                if (args.length == 3){
                    currency = AccountManager.getCurrency(args[2]);
                }

                if (currency != null){

                    if (!currency.validateInput(sender, args[1])) {
                        return;
                    }

                    double amount = Double.parseDouble(args[1]);

                    Account target  = AccountManager.getAccount(args[0]);
                    if (target != null){

                        target.setBalance(currency, target.getBalance(currency)+amount);

                        MelonEco.getDataStore().saveAccount(target);

                        sender.sendMessage("§a§l[Eco] §aYou gave " + currency.getColor() + currency.format(amount) + "§a to " + target.getDisplayName() + ".");

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
