package com.turqmelon.MelonEco.commands;

import com.turqmelon.MelonEco.utils.AccountManager;
import com.turqmelon.MelonEco.utils.Currency;
import com.turqmelon.MelonEco.utils.CurrencyTicket;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Turqmelon on 11/1/16.
 */
public class CurrencyTicketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("eco.currencyticket.create")) {


            if (args.length >= 2) {

                Player target = Bukkit.getPlayer(args[0]);
                if (target != null && target.isOnline()) {

                    Map<Currency, Double> values = new HashMap<>();

                    for (int i = 1; i < args.length; i++) {
                        String arg = args[i];
                        String[] parts = arg.split(":");
                        if (parts.length == 2) {
                            Currency currency = AccountManager.getCurrency(parts[0]);
                            if (currency == null) {
                                sender.sendMessage("§c§l[Eco] §cUnknown currency \"" + parts[0] + "\".");
                                return true;
                            }
                            try {
                                double amount = Double.parseDouble(parts[1]);

                                values.put(currency, amount);

                            } catch (NumberFormatException ex) {
                                sender.sendMessage("§c§l[Eco] §c\"" + parts[1] + "\" is not a number.");
                                return true;
                            }
                        } else {
                            sender.sendMessage("§c§l[Eco] §c\"" + arg + "\" is invalid. Separate your currency and value with a \":\".");
                            return true;
                        }
                    }

                    if (!values.isEmpty()) {

                        CurrencyTicket ticket = new CurrencyTicket("Economy Ticket", values);
                        target.getInventory().addItem(ticket.toItemStack());
                        sender.sendMessage("§a§l[Eco] §aGave the ticket to " + target.getName() + ".");
                        target.sendMessage("§a§l[Eco] §aYou got a currency ticket from " + sender.getName() + ".");

                    } else {
                        sender.sendMessage("§c§l[Eco] §cThat'd be an empty ticket.");
                    }

                } else {
                    sender.sendMessage("§c§l[Eco] §cPlayer not found.");
                }

            } else {
                sender.sendMessage("§c§l[Eco] §cUsage: §f/currencyticket <Player> <Currency:Value> [Currency:Value...]");
            }

        } else {
            sender.sendMessage("§c§l[Eco] §cYou don't have permission.");
        }

        return true;
    }
}
