package com.turqmelon.MelonEco.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Turqmelon on 11/1/16.
 */
public class CurrencyTicket {

    private static final String identifier = "§6§oEco Ticket";

    private String name;
    private Map<Currency, Double> value;

    public CurrencyTicket(String name, Map<Currency, Double> value) {
        this.name = name;
        this.value = value;
    }

    public CurrencyTicket(ItemStack itemStack) throws Exception {
        Map<Currency, Double> values = new HashMap<>();
        String name = null;
        if (itemStack != null && itemStack.getType() == Material.PAPER && itemStack.getItemMeta() != null && itemStack.getItemMeta().getLore() != null && itemStack.getItemMeta().getLore().size() > 1) {
            if (itemStack.getItemMeta().getDisplayName() != null) {
                name = itemStack.getItemMeta().getDisplayName().substring(4);
            }
            List<String> lore = itemStack.getItemMeta().getLore();
            String firstLine = lore.get(0);
            if (firstLine.equalsIgnoreCase(identifier)) {
                String lastLine = lore.get(lore.size() - 1).substring(2);
                String[] parts = lastLine.split(",");
                for (String part : parts) {
                    String[] segments = part.split(":");
                    String currencyName = segments[0];
                    double value = Double.parseDouble(segments[1]);
                    Currency currency = AccountManager.getCurrency(currencyName);
                    if (currency == null) continue;
                    values.put(currency, value);
                }
            } else {
                throw new Exception("This isn't a Currency Ticket.");
            }
        }
        this.value = values;
        this.name = name;
    }

    public void redeem(Account account) {
        for (Currency currency : getValue().keySet()) {
            double val = getValue().get(currency);
            account.deposit(currency, val);
        }
    }

    public ItemStack toItemStack() {
        ItemStack paper = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = paper.getItemMeta();
        meta.setDisplayName("§6§l" + getName());
        List<String> lore = new ArrayList<>();
        lore.add(identifier);
        if (value.isEmpty()) {
            lore.add("§c§oThis is a worthless ticket.");
        } else if (value.size() == 1) {

            Currency currency = (Currency) value.keySet().toArray()[0];
            double amount = (double) value.values().toArray()[0];
            lore.add("§6§lRight Click§f to redeem for " + currency.format(amount));
            lore.add("§0" + currency.getSingular() + ":" + amount);
        } else {
            lore.add("§6§lRight Click§f to redeem for...");
            StringBuilder builder = new StringBuilder();
            for (Currency currency : getValue().keySet()) {
                double amount = getValue().get(currency);
                lore.add("§8 - §f" + currency.format(amount));
                builder.append(",");
                builder.append(currency.getSingular());
                builder.append(":");
                builder.append(amount);
            }
            lore.add("§0" + builder.toString().substring(1));
        }
        meta.setLore(lore);
        paper.setItemMeta(meta);

        return paper;
    }

    public String getName() {
        return name;
    }

    public Map<Currency, Double> getValue() {
        return value;
    }
}
