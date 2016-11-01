package com.turqmelon.MelonEco.commands;

/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/

import com.turqmelon.MelonEco.MelonEco;
import com.turqmelon.MelonEco.api.AccountPayAccountEvent;
import com.turqmelon.MelonEco.utils.Account;
import com.turqmelon.MelonEco.utils.AccountManager;
import com.turqmelon.MelonEco.utils.Currency;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!(sender instanceof Player)){
            sender.sendMessage("§c§l[Eco] §cYou must be a player to use this command.");
            return true;
        }

        new BukkitRunnable(){
            @Override
            public void run() {
                if (!sender.hasPermission("eco.pay")){
                    sender.sendMessage("§c§l[Eco] §cYou don't have permission to pay.");
                    return;
                }

                if (args.length < 2){
                    sender.sendMessage("§c§l[Eco] §cAllows you to pay other players.");
                    sender.sendMessage("§c§l[Eco] §cUsage: §f/pay <Account> <Amount> [Currency]");
                    return;
                }

                Currency currency = AccountManager.getDefaultCurrency();

                if (args.length == 3){
                    currency = AccountManager.getCurrency(args[2]);
                }

                if (currency != null){

                    if (!currency.isPayable()){
                        sender.sendMessage("§c§l[Eco] §c" + currency.getPlural() + " are not payable.");
                        return;
                    }

                    if (!sender.hasPermission("eco.pay." + currency.getPlural().toLowerCase()) && !sender.hasPermission("eco.pay." + currency.getSingular().toLowerCase())){
                        sender.sendMessage("§c§l[Eco] §cYou don't have permission to pay " + currency.getPlural() + ".");
                        return;
                    }

                    if (!currency.validateInput(sender, args[1])) {
                        return;
                    }

                    double amount = Double.parseDouble(args[1]);

                    Account account = AccountManager.getAccount((Player)sender);

                    if (account != null){

                        Account target  = AccountManager.getAccount(args[0]);
                        if (target != null){

                            AccountPayAccountEvent event = new AccountPayAccountEvent(account, target, currency, amount);
                            Bukkit.getPluginManager().callEvent(event);

                            if (event.isCancelled()) {
                                return;
                            }

                            account = event.getSender();
                            target = event.getRecipient();
                            currency = event.getCurrency();
                            amount = event.getAmount();

                            if (target.isCanReceiveCurrency()){

                                if (account.getBalance(currency) >= amount){

                                    account.setBalance(currency, account.getBalance(currency)-amount);
                                    target.setBalance(currency, target.getBalance(currency)+amount);

                                    MelonEco.getDataStore().saveAccount(account);
                                    MelonEco.getDataStore().saveAccount(target);

                                    sender.sendMessage("§a§l[Eco] §aYou sent " + currency.getColor() + currency.format(amount) + "§a to " + target.getDisplayName() + ".");

                                }
                                else{
                                    sender.sendMessage("§c§l[Eco] §cYou don't have enough " + currency.getPlural() + " to pay " + target.getDisplayName() + ".");
                                }

                            }
                            else{
                                sender.sendMessage("§c§l[Eco] §c" + target.getDisplayName() + " can't receive money.");
                            }

                        }
                        else{
                            sender.sendMessage("§c§l[Eco] §cTarget account not found.");
                        }

                    }
                    else{
                        sender.sendMessage("§c§l[Eco] §cYou don't have an account.");
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
