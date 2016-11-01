package com.turqmelon.MelonEco.commands;

/******************************************************************************
 *  Copyright (c) 2016.  Written by Devon "Turqmelon": http://turqmelon.com   *
 *  For more information, see LICENSE.TXT.                                    *
 ******************************************************************************/

import com.turqmelon.MelonEco.MelonEco;
import com.turqmelon.MelonEco.utils.AccountManager;
import com.turqmelon.MelonEco.utils.Currency;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class BalTopCommand implements CommandExecutor {

    private static final int ACCOUNTS_PER_PAGE = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        new BukkitRunnable(){
            @Override
            public void run() {
                if (!sender.hasPermission("eco.top")){
                    sender.sendMessage("§c§l[Eco] §cYou don't have permission to view the toplist.");
                    return;
                }

                if (!MelonEco.getDataStore().isTopSupported()){
                    sender.sendMessage("§c§l[Eco] §c" + MelonEco.getDataStore().getName() + " doesn't support the /baltop command.");
                    sender.sendMessage("§c§l[Eco] §cConsider asking an administrator to modify this setting.");
                    return;
                }

                Currency currency = AccountManager.getDefaultCurrency();
                int page = 1;

                if (args.length > 0){
                    currency = AccountManager.getCurrency(args[0]);
                    if (args.length == 2){
                        try {
                            page = Integer.parseInt(args[1]);
                        }catch(NumberFormatException ex){
                            sender.sendMessage("§c§l[Eco] §cPlease provide a valid page.");
                            return;
                        }
                    }
                }

                if (page < 1){
                    page = 1;
                }

                int offset = ACCOUNTS_PER_PAGE*(page-1);

                if (currency != null){


                    Map<String, Double> toplist = MelonEco.getDataStore().getTopList(currency, offset, ACCOUNTS_PER_PAGE);
                    sender.sendMessage("§a§l[Eco] §f----- " + currency.getColor() + "Top " + currency.getSingular() + " Holders §7(Page " + page + ")§f -----");
                    int num = (10*(page-1))+1;
                    for (String name : toplist.keySet()) {
                        double balance = toplist.get(name);
                        sender.sendMessage("§a§l[Eco] §b§l" + num + ". " + currency.getColor() + name + "§7 - " + currency.getColor() + currency.format(balance));
                        num++;
                    }
                    if (toplist.isEmpty()){
                        sender.sendMessage("§a§l[Eco] §a§oNo accounts to display.");
                    }
                    else{
                        sender.sendMessage("§a§l[Eco] " + currency.getColor() + "/baltop " + currency.getPlural() + " " + (page+1) + " §afor more!");
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
