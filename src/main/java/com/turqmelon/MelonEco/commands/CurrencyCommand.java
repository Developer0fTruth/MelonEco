package com.turqmelon.MelonEco.commands;

/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/

import com.turqmelon.MelonEco.MelonEco;
import com.turqmelon.MelonEco.utils.AccountManager;
import com.turqmelon.MelonEco.utils.Currency;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class CurrencyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        new BukkitRunnable(){
            @Override
            public void run() {
                if (!sender.hasPermission("eco.currencies")){
                    sender.sendMessage("§c§l[Eco] §cYou don't have permission to manage currencies.");
                    return;
                }

                if (args.length == 0){
                    sender.sendMessage("§e§l[Eco] §eCurrency Management");
                    sender.sendMessage("§e§l[Eco] §b§l-> §b/currency create §7<Singular> <Plural>");
                    sender.sendMessage("§e§l[Eco] §b§l-> §b/currency delete §7<Name>");
                    sender.sendMessage("§e§l[Eco] §b§l-> §b/currency view §7<Name>");
                    sender.sendMessage("§e§l[Eco] §b§l-> §b/currency list");
                    sender.sendMessage("§e§l[Eco] §b§l-> §b/currency symbol §7<Name> <Char|Remove>");
                    sender.sendMessage("§e§l[Eco] §b§l-> §b/currency color §7<Name> <ChatColor>");
                    sender.sendMessage("§e§l[Eco] §b§l-> §b/currency decimals §7<Name>");
                    sender.sendMessage("§e§l[Eco] §b§l-> §b/currency payable §7<Name>");
                    sender.sendMessage("§e§l[Eco] §b§l-> §b/currency default §7<Name>");
                    sender.sendMessage("§e§l[Eco] §b§l-> §b/currency startingbal §7<Name> <Amount>");
                }
                else{
                    String cmd = args[0];
                    if (cmd.equalsIgnoreCase("create")){

                        if (args.length == 3){

                            String single = args[1];
                            String plural = args[2];

                            if (AccountManager.getCurrency(single) == null && AccountManager.getCurrency(plural) == null){

                                Currency currency = new Currency(UUID.randomUUID(), single, plural);

                                sender.sendMessage("§a§l[Eco] §aCreated currency: " + currency.getPlural());

                                AccountManager.getCurrencies().add(currency);

                                if (AccountManager.getCurrencies().size() == 1){
                                    currency.setDefaultCurrency(true);
                                }

                                MelonEco.getDataStore().saveCurrency(currency);


                            }
                            else{
                                sender.sendMessage("§c§l[Eco] §cCurrency already exists.");
                            }

                        }
                        else{
                            sender.sendMessage("§c§l[Eco] §cUsage: §f/currency create <Singular> <Plural>");
                        }

                    }
                    else if (cmd.equalsIgnoreCase("list")){

                        sender.sendMessage("§a§l[Eco] §aThere are §f" + AccountManager.getCurrencies().size() + "§a currencies.");
                        for(Currency currency : AccountManager.getCurrencies()){
                            sender.sendMessage("§a§l[Eco] §b§l-> §b" + currency.getSingular());
                        }

                    }
                    else if (cmd.equalsIgnoreCase("view")){

                        if (args.length == 2){

                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null){

                                sender.sendMessage("§a§l[Eco] §aInfo for " + currency.getUuid().toString());
                                sender.sendMessage("§a§l[Eco] §aSingular: §f" + currency.getSingular() + "§a, Plural: §f" + currency.getPlural());
                                sender.sendMessage("§a§l[Eco] §aNew players start with §f" + currency.format(currency.getDefaultBalance()) +"§a.");
                                sender.sendMessage("§a§l[Eco] §aDecimals? §f" + (currency.isDecimalSupported()?"Yes":"No"));
                                sender.sendMessage("§a§l[Eco] §aDefault? §f" + (currency.isDefaultCurrency()?"Yes":"No"));
                                sender.sendMessage("§a§l[Eco] §aPayable? §f" + (currency.isPayable()?"Yes":"No"));
                                sender.sendMessage("§a§l[Eco] §aColor: " + currency.getColor() + currency.getColor().name());

                            }
                            else{
                                sender.sendMessage("§c§l[Eco] §cUnknown currency.");
                            }

                        }
                        else{
                            sender.sendMessage("§c§l[Eco] §cUsage: §f/currency create <Singular> <Plural>");
                        }

                    }
                    else if (cmd.equalsIgnoreCase("startingbal")){

                        if (args.length == 3){

                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null){

                                double amount;

                                if (currency.isDecimalSupported()){
                                    try {

                                        amount = Double.parseDouble(args[2]);
                                        if (amount <= 0){
                                            throw new NumberFormatException();
                                        }

                                    }catch(NumberFormatException ex){
                                        sender.sendMessage("§c§l[Eco] §cPlease provide a valid amount.");
                                        return;
                                    }
                                }
                                else{
                                    try {

                                        amount = Integer.parseInt(args[2]);
                                        if (amount <= 0){
                                            throw new NumberFormatException();
                                        }

                                    }catch(NumberFormatException ex){
                                        sender.sendMessage("§c§l[Eco] §cPlease provide a valid amount.");
                                        return;
                                    }
                                }

                                currency.setDefaultBalance(amount);
                                sender.sendMessage("§a§l[Eco] §aStarting balance for " + currency.getPlural() + " set: " + currency.getDefaultBalance());
                                MelonEco.getDataStore().saveCurrency(currency);

                            }
                            else{
                                sender.sendMessage("§c§l[Eco] §cUnknown currency.");
                            }

                        }
                        else{
                            sender.sendMessage("§c§l[Eco] §cUsage: §f/currency create <Singular> <Plural>");
                        }

                    }
                    else if (cmd.equalsIgnoreCase("color")){

                        if (args.length == 3){

                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null){

                                try {

                                    ChatColor color = ChatColor.valueOf(args[2].toUpperCase());
                                    if (color.isFormat()){
                                        throw new Exception();
                                    }

                                    currency.setColor(color);
                                    sender.sendMessage("§a§l[Eco] §aColor for " + currency.getPlural() + " updated: " + color + color.name());
                                    MelonEco.getDataStore().saveCurrency(currency);

                                }catch (Exception ex){
                                    sender.sendMessage("§c§l[Eco] §cInvalid chat color.");
                                }

                            }
                            else{
                                sender.sendMessage("§c§l[Eco] §cUnknown currency.");
                            }

                        }
                        else{
                            sender.sendMessage("§c§l[Eco] §cUsage: §f/currency create <Singular> <Plural>");
                        }

                    }
                    else if (cmd.equalsIgnoreCase("symbol")){

                        if (args.length == 3){

                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null){

                                String symbol = args[2];
                                if (symbol.equalsIgnoreCase("remove")){
                                    currency.setSymbol(null);
                                    sender.sendMessage("§a§l[Eco] §aCurrency symbol removed for " + currency.getPlural());
                                    MelonEco.getDataStore().saveCurrency(currency);
                                }
                                else if (symbol.length() == 1){
                                    currency.setSymbol(symbol);
                                    sender.sendMessage("§a§l[Eco] §aCurrency symbol for " + currency.getPlural() + " updated: " + symbol);
                                    MelonEco.getDataStore().saveCurrency(currency);
                                }
                                else{
                                    sender.sendMessage("§c§l[Eco] §cSymbol must be 1 character, or say \"remove\".");
                                }

                            }
                            else{
                                sender.sendMessage("§c§l[Eco] §cUnknown currency.");
                            }

                        }
                        else{
                            sender.sendMessage("§c§l[Eco] §cUsage: §f/currency create <Singular> <Plural>");
                        }

                    }
                    else if (cmd.equalsIgnoreCase("default")){

                        if (args.length == 2){

                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null){

                                Currency c = AccountManager.getDefaultCurrency();
                                if (c != null){
                                    c.setDefaultCurrency(false);
                                    MelonEco.getDataStore().saveCurrency(c);
                                }

                                currency.setDefaultCurrency(true);
                                sender.sendMessage("§a§l[Eco] §aSet default currency to " + currency.getPlural());
                                MelonEco.getDataStore().saveCurrency(currency);

                            }
                            else{
                                sender.sendMessage("§c§l[Eco] §cUnknown currency.");
                            }

                        }
                        else{
                            sender.sendMessage("§c§l[Eco] §cUsage: §f/currency create <Singular> <Plural>");
                        }

                    }
                    else if (cmd.equalsIgnoreCase("payable")){

                        if (args.length == 2){

                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null){

                                currency.setPayable(!currency.isPayable());
                                sender.sendMessage("§a§l[Eco] §aToggled payability for " + currency.getPlural() + ": " + currency.isPayable());
                                MelonEco.getDataStore().saveCurrency(currency);

                            }
                            else{
                                sender.sendMessage("§c§l[Eco] §cUnknown currency.");
                            }

                        }
                        else{
                            sender.sendMessage("§c§l[Eco] §cUsage: §f/currency create <Singular> <Plural>");
                        }

                    }
                    else if (cmd.equalsIgnoreCase("decimals")){

                        if (args.length == 2){

                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null){

                                currency.setDecimalSupported(!currency.isDecimalSupported());
                                sender.sendMessage("§a§l[Eco] §aToggled Decimal Support for " + currency.getPlural() + ": " + currency.isDecimalSupported());
                                MelonEco.getDataStore().saveCurrency(currency);

                            }
                            else{
                                sender.sendMessage("§c§l[Eco] §cUnknown currency.");
                            }

                        }
                        else{
                            sender.sendMessage("§c§l[Eco] §cUsage: §f/currency create <Singular> <Plural>");
                        }

                    }
                    else if (cmd.equalsIgnoreCase("delete")){

                        if (args.length == 2){

                            Currency currency = AccountManager.getCurrency(args[1]);
                            if (currency != null){

                                AccountManager.getAccounts().stream().filter(account -> account.getBalances().containsKey(currency)).forEach(account -> account.getBalances().remove(currency));

                                MelonEco.getDataStore().deleteCurrency(currency);

                                AccountManager.getCurrencies().remove(currency);

                                sender.sendMessage("§a§l[Eco] §aDeleted currency: " + currency.getPlural());

                            }
                            else{
                                sender.sendMessage("§c§l[Eco] §cUnknown currency.");
                            }

                        }
                        else{
                            sender.sendMessage("§c§l[Eco] §cUsage: §f/currency create <Singular> <Plural>");
                        }

                    }
                    else{
                        sender.sendMessage("§c§l[Eco] §cUnknown currency sub-command.");
                    }
                }
            }
        }.runTaskAsynchronously(MelonEco.getInstance());



        return true;
    }
}
